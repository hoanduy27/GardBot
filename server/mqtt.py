import os
import sys
import json
from types import MethodType
from oauth2client import client
import yaml
import time
from Adafruit_IO import MQTTClient
from log.firebaseLog import LogApp
from log.printout import Printout
from datetime import date, datetime
from threading import Thread
from predict.PredictionModel import PredictionModel

class MQTT:
    def __init__(self):
        self.POOL_TIME = 40
        self.INACTIVE_LIMIT = 60
        self.TIME_FORMAT = "%d-%m-%Y-%H:%M:%S"
        self.INACTIVE = -1
        self.INIT = 0
        self.DEFAULT_TEMP = 25

        cur_dir = os.path.abspath(__file__)
        cur_dir = os.path.dirname(cur_dir)
        with open(f'{cur_dir}/config.yml') as conf:
            config = yaml.safe_load(conf)

        # Load config from test_server
        username = config['IO_USERNAME']
        key = config['IO_KEY']
        pump = config['feed']['output']
        sensor = config['feed']['input']['sensor']
        tempHumid = config['feed']['input']['tempHumid']
        
        # Load config from real servers
        bbc = config['realServer']
        bbc_username = bbc['USERNAME']
        bbc_key = bbc['KEY']
        pump_bbc = bbc['relay']
        sensor_bbc = bbc['soil']
        tempHumid_bbc = bbc['dht']

        self.logApp = LogApp()
        self.predictor = PredictionModel()

        # Test server
        self.client = {username: MQTTClient(username, key)}
        # Real servers
        self.client.update({bbc_username[i]: MQTTClient(bbc_username[i], bbc_key[i]) for i in range(len(bbc_key))})

        sensors = self.logApp.getSensors()

        self.pump = {key: {'owner': username, 'value': None} for key in pump}
        self.pump.update({pump_bbc['feedId']: {'owner': pump_bbc['IO_OWNER'], 'value': None}})

        self.sensor = {key: {
            'owner': username, 
            'value': None, 
            'sysID': sensors['soilMoisture'][key]['sysID'],
            'rcvTime': self.INACTIVE
        } for key in sensor}    
        self.sensor.update({sensor_bbc['feedId']: {
            'owner': sensor_bbc['IO_OWNER'], 
            'value': None,
            'sysID': sensors['soilMoisture'][sensor_bbc['feedId']]['sysID'], 
            'rcvTime': self.INACTIVE
        }})

        self.tempHumid = {key: {
            'owner': username, 
            'temp': None, 
            'humid': None,
            'sysID': sensors['dht'][key]['sysID'],
            'rcvTime': self.INACTIVE
        } for key in tempHumid}
        self.tempHumid.update({tempHumid_bbc['feedId']: {
            'owner': tempHumid_bbc['IO_OWNER'], 
            'temp': None, 
            'humid': None,
            'sysID': sensors['dht'][tempHumid_bbc['feedId']]['sysID'],
            'rcvTime': self.INACTIVE
        }})

        self.writeHistory_loop = Thread(target=self.writeSensorHistory)
        self.writeHistory_loop.daemon = True

        self.autoStop_loop = Thread(target=self.autoStop)
        self.autoStop_loop.daemon = True

        self.wateringHistory = {key:None for key in self.pump}
        self.sensorSendingCheck = True

        def writeWateringHistory(pump_id, waterLevel):
            # Get time
            timestamp = int(datetime.now().timestamp())
            # time_only = timestamp.strftime("%H:%M:%S")
            # timestamp = timestamp.strftime(self.TIME_FORMAT)

            # Get sensor value
            status = self.logApp.getPumpStatus(pump_id)
            auto = status['auto']
            moisture = self.sensor[status['soilMoistureID']]['value']
            humid = self.tempHumid['dht0001']['humid']
            temp = self.tempHumid['dht0001']['temp']

            if waterLevel == '1' and self.wateringHistory[pump_id] is None:
                # Create new record for pump_id
                self.wateringHistory[pump_id] = {
                    'autoStart': auto,
                    'autoEnd': None,
                    'startTime': timestamp,
                    'endTime': None,
                    'moistureStart': moisture,
                    'moistureEnd': None,
                    'humidityStart': humid,
                    'humidityEnd': None,
                    'temperatureStart': temp,
                    'temperatureEnd': None
                }
            if waterLevel == '0' and self.wateringHistory[pump_id] is not None:
                # Update record for pump_id
                self.wateringHistory[pump_id]['autoEnd'] = auto
                self.wateringHistory[pump_id]['endTime'] = timestamp
                self.wateringHistory[pump_id]['moistureEnd'] = moisture
                self.wateringHistory[pump_id]['humidityEnd'] = humid
                self.wateringHistory[pump_id]['temperatureEnd'] = temp
                # Write this record to firebase
                self.logApp.writeWateringHistory(pump_id, self.wateringHistory[pump_id], timestamp)
                # Set record of this pump_id to None
                self.wateringHistory[pump_id] = None
        
        def connected(client):
            for feed_id in self.sensor:
                if client._username == self.sensor[feed_id]['owner']:
                    client.subscribe(feed_id=feed_id, feed_user=self.sensor[feed_id]['owner'])
                    client.receive(feed_id)

            for feed_id in self.tempHumid:
                if client._username == self.tempHumid[feed_id]['owner']:
                    client.subscribe(feed_id=feed_id, feed_user=self.tempHumid[feed_id]['owner'])
                    client.receive(feed_id)
            
            for feed_id in self.pump:
                if client._username == self.pump[feed_id]['owner']:
                    client.subscribe(feed_id=feed_id)
                    client.receive(feed_id)

	    #self.pumpHistory = {key: {autoEnd:None, autoStart:  None, ...} for key in pump}


        def message(client, feed_id, payload):
            payload = json.loads(payload)
            value = payload['data']
            Printout.i('new value', 'Feed {0} received new value: {1}'.format(feed_id, value))

            if(feed_id in self.pump):
                self.pump[feed_id]['value'] = value
                self.logApp.changePumpStatus(feed_id, value)
                writeWateringHistory(feed_id, value)

            elif(feed_id in self.sensor):   
                self.sensor[feed_id]['value'] = value
                self.sensor[feed_id]['rcvTime'] = time.time()
                self.logApp.changeSoilMoisute(feed_id, value)
                self.changeWaterlevel(feed_id)
                
                # Predict if counter >= 
                # 1 + (number of active dht that has the same sysID as this sensor)
                # n_dht = len(self.getActiveTempHumidSensor(feed_id))
                # if self.sensor[feed_id]['counter'] >= 1 + n_dht:
                #     self.changeWaterlevel(feed_id)
                #     self.sensor[feed_id]['counter'] = 0
                # self.sensorSendingCheck = True
            
            elif(feed_id in self.tempHumid):
                value = value.split('-')
                self.tempHumid[feed_id]['temp'] = value[0]
                self.tempHumid[feed_id]['humid'] = value[1]
                self.tempHumid[feed_id]['rcvTime'] = time.time()
                self.logApp.changeTempHumid(feed_id, value[0], value[1])  
                
                # Predict for sensor that has counter >= 
                # 1 + (number of active dht that has the same sysID as this sensor)
                # n_dht = len(self.getActiveTempHumidSensor(feed_id, is_soil=False))
                # for sensor_id in self.getActiveSoilSensor(feed_id):
                #     self.sensor[sensor_id]['counter'] += 1
                #     if(self.sensor[sensor_id]['counter'] >= 1 + n_dht):
                #         self.changeWaterlevel(sensor_id)
                #         self.sensor[sensor_id]['counter'] = 0
                # self.sensorSendingCheck = True       
            
        def disconnected(client):
            Printout.i('client', 'Disconnected from Adafruit IO')
            sys.exit(1)
            
        def subscribe(client, userdata, mid, granted_qos):
            # This method is called when the client subscribes to a new feed.
            Printout.i('client', 'Subscribe to pumps and sensors')

        for client in self.client.values():
            client.on_connect = connected
            client.on_message = message
            client.on_disconnect = disconnected
            client.on_subscribe = subscribe
            client.connect()
            
        self.writeHistory_loop.start()
        self.autoStop_loop.start()

        for client in self.client.values(): 
            client.loop_background()

    def getTempHumidSensor(self, sensor_id, is_soil = True):
        """Get list of temp-humid sensor ID that is in the same system as this sensor_id
            If is_soil = True, sensor_id is from soil sensor
            If is_soil = False, sensor_id is from temphumid sensor
        """
        sysID = self.sensor[sensor_id]['sysID'] if is_soil else self.tempHumid[sensor_id]['sysID']
        return [s for s in self.tempHumid if self.tempHumid[s]['sysID'] == sysID]

    def getSoilSensor(self, sensor_id, is_soil = False):
        """Get list of soil sensor ID that is in the same system as this sensor_id
            If is_soil = True, sensor_id is from soil sensor
            If is_soil = False, sensor_id is from temphumid sensor
        """
        sysID = self.sensor[sensor_id]['sysID'] if is_soil else self.tempHumid[sensor_id]['sysID']
        return [s for s in self.sensor if self.sensor[s]['sysID'] == sysID]

    def getActiveTempHumidSensor(self, sensor_id, is_soil = True):
        """Get list of temp-humid sensor ID that is ACTIVE and in the same system as this sensor_id
            If is_soil = True, sensor_id is from soil sensor
            If is_soil = False, sensor_id is from temphumid sensor
        """
        sysID = self.sensor[sensor_id]['sysID'] if is_soil else self.tempHumid[sensor_id]['sysID']
        return [s for s in self.tempHumid if \
            self.tempHumid[s]['sysID'] == sysID
            and self.tempHumid[s]['rcvTime'] != self.INACTIVE
        ]

    def getActiveSoilSensor(self, sensor_id, is_soil = False):
        """Get list of soil sensor ID that is ACTIVE and in the same system as this sensor_id
            If is_soil = True, sensor_id is from soil sensor
            If is_soil = False, sensor_id is from temphumid sensor
        """
        sysID = self.sensor[sensor_id]['sysID'] if is_soil else self.tempHumid[sensor_id]['sysID']
        return [s for s in self.sensor if \
            self.sensor[s]['sysID'] == sysID
            and self.sensor[s]['rcvTime'] != self.INACTIVE
        ]

    def send_feed_data(self, feed_id, value):
        client = self.client[self.pump[feed_id]['owner']]
        client.publish(feed_id, value)

    def writeSensorHistory(self):
        while True:
            time.sleep(self.POOL_TIME)
            current_time = int(datetime.now().timestamp())
            for sensor_id in self.sensor: 
                self.logApp.writeSensorHistory("moisture", sensor_id, self.sensor[sensor_id]['value'], current_time)
            for dht_id in self.tempHumid:
                self.logApp.writeSensorHistory("temperature", dht_id, self.tempHumid[dht_id]['temp'], current_time)
                self.logApp.writeSensorHistory("humidity", dht_id, self.tempHumid[dht_id]['humid'], current_time)

    def changeWaterlevel(self, sensor_id):
        pumps = self.logApp.getPumpBySoilMoistureID(sensor_id)
        dhts = self.getActiveTempHumidSensor(sensor_id)    
        # Get temperature
        temp = [int(self.tempHumid[s]['temp']) for s in dhts]
        # If there is no active humid sensor, set temp as DEFAULT_TEMP
        temp = sum(temp)/len(temp) if temp else self.DEFAULT_TEMP
        for pump_id, pump_status in pumps.items():
            if pump_status['auto'] == '1':
                moisture = int(self.sensor[pump_status['soilMoistureID']]['value'])
                Printout.i(
                    'predict', 
                    f'{pump_id} | (soil={sensor_id}, dht={str(dhts)}) | (moisture={str(moisture)}, temp={str(temp)})'
                )
                pred_water_level = self.predictor.predict(moisture, temp)
                cur_water_level = int(pump_status['waterLevel'])
                # If current water level and predicted waterlevel are not both = 0 or > 0
                if (pred_water_level > 0) ^ (cur_water_level > 0):
                    value = json.dumps({
                        "id": "11", 
                        "name": "RELAY", 
                        "data": f"{pred_water_level}",
                        "unit": ""
                    })
                    self.send_feed_data(pump_id,value)

    def autoStop(self):
        while True:
            time.sleep(30)
            cur_time = time.time()
            # Check active status of soil sensor
            for sensor_id in self.sensor:
                rcv_time = self.sensor[sensor_id]['rcvTime']
                time_gap = cur_time - rcv_time
                if rcv_time != self.INACTIVE and time_gap > self.INACTIVE_LIMIT:
                    Printout.w('inactive soil sensor', f"{int(time_gap)}s since the last data from {sensor_id} was sent")                    
                    self.sensor[sensor_id]['rcvTime'] = self.INACTIVE
                    # Handle turning off pump
                    pumps = self.logApp.getPumpBySoilMoistureID(sensor_id)
                    value = json.dumps({
                        "id": "11", 
                        "name": "RELAY", 
                        "data": "0",
                        "unit": ""
                    })
                    for pump_id, pump_status in pumps.items():
                        if pump_status['auto'] == '1' and pump_status['waterLevel'] == '1':
                            self.send_feed_data(pump_id, value)
                            

            # Check active status of tempHumid sensor
            for dht_id in self.tempHumid:
                rcv_time = self.tempHumid[dht_id]['rcvTime']
                time_gap = cur_time - rcv_time
                if rcv_time != self.INACTIVE and time_gap > self.INACTIVE_LIMIT:
                    Printout.w('inactive temphumid sensor', f"{int(time_gap)}s since the last data from {dht_id} was sent")     
                    self.tempHumid[dht_id]['rcvTime'] = self.INACTIVE


    
