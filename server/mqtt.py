import os
import sys
import json
from oauth2client import client
import yaml
import time
from Adafruit_IO import MQTTClient
from firebaseLog import LogApp
from datetime import date, datetime
from threading import Thread
from predict.PredictionModel import PredictionModel

class MQTT:
    def __init__(self):
        self.POOL_TIME = 20
        self.WAIT_FOR_PREDICTION = 1
        self.TIME_FORMAT = "%d-%m-%Y-%H:%M:%S"

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
            'counter': 0
            
        } for key in sensor}
        
        self.sensor.update({sensor_bbc['feedId']: {
            'owner': sensor_bbc['IO_OWNER'], 
            'value': None,
            'sysID': sensors['soilMoisture'][sensor_bbc['feedId']]['sysID'], 
            'counter': 0
        }})

        self.tempHumid = {key: {
            'owner': username, 
            'temp': None, 
            'humid': None,
            'sysID': sensors['dht'][key]['sysID']
        } for key in tempHumid}
        self.tempHumid.update({tempHumid_bbc['feedId']: {
            'owner': tempHumid_bbc['IO_OWNER'], 
            'temp': None, 
            'humid': None,
            'sysID': sensors['dht'][tempHumid_bbc['feedId']]['sysID'],
            'newValue': False
        }})

        self.writeHistory_loop = Thread(target=self.writeSensorHistory)
        self.writeHistory_loop.daemon = True

        # self.autoWatering_loop = Thread(target=self.autoWatering)
        # self.autoWatering_loop.daemon = True

        self.wateringHistory = {key:None for key in self.pump}
        self.sensorSendingCheck = True

        def writeWateringHistory(pump_id, waterLevel):
            # soil = self.logApp.getSoilMoistureValue(status['soilMoistureID'])
            # for dht_id in self.tempHumid:
            #     humid = self.logApp.getHumidValue(dht_id)
            #     temp = self.logApp.getTempValue(dht_id)
            #     break

            # Get time
            timestamp = datetime.now()
            time_only = timestamp.strftime("%H:%M:%S")
            timestamp = timestamp.strftime(self.TIME_FORMAT)

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
                    'startTime': time_only,
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
                self.wateringHistory[pump_id]['endTime'] = time_only
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

            if(feed_id in self.pump):
                self.pump[feed_id]['value'] = value
                self.logApp.changePumpStatus(feed_id, value)
                writeWateringHistory(feed_id, value)

            elif(feed_id in self.sensor):   
                self.sensor[feed_id]['value'] = value
                self.logApp.changeSoilMoisute(feed_id, value)
                
                # self.sensor[feed_id]['counter'] += 1
                # Predict if counter = 2
                time.sleep(self.WAIT_FOR_PREDICTION)
                self.changeWaterlevel(feed_id)

                # if(self.sensor[feed_id]['counter'] == 2):
                #     self.changeWaterlevel(feed_id)
                #     self.sensor[feed_id]['counter'] = 0
                self.sensorSendingCheck = True
            
            elif(feed_id in self.tempHumid):
                value = value.split('-')
                self.tempHumid[feed_id]['temp'] = value[0]
                self.tempHumid[feed_id]['humid'] = value[1]
                self.logApp.changeTempHumid(feed_id, value[0], value[1])  
                # Predict for sensor that has counter = 2
                # for sensor_id in self.getSoilSensor(feed_id):
                #     self.sensor[sensor_id]['counter'] += 1
                #     if(self.sensor[sensor_id]['counter'] == 2):
                #         self.changeWaterlevel(sensor_id)
                #         self.sensor[sensor_id]['counter'] = 0
                self.sensorSendingCheck = True       

            # Depends on json format
            print('Feed {0} received new value: {1}'.format(feed_id, value))
            
        def disconnected(client):
            print('Disconnected from Adafruit IO!')
            sys.exit(1)
            
        def subscribe(client, userdata, mid, granted_qos):
            # This method is called when the client subscribes to a new feed.
            print("Subscribe pumps and sensors")
            #print('Subscribed to {0} with QoS {1}'.format(userdata, granted_qos[0]))

        for client in self.client.values():
            client.on_connect = connected
            client.on_message = message
            client.on_disconnect = disconnected
            client.on_subscribe = subscribe
            client.connect()
            
        self.writeHistory_loop.start()
        #self.autoWatering_loop.start()

        for client in self.client.values(): 
            client.loop_background()

    def getTempHumidSensor(self, sensor_id):
        sysID = self.sensor[sensor_id]['sysID']
        return list(filter(lambda s: self.tempHumid[s]['sysID'] == sysID, self.tempHumid))

    def getSoilSensor(self, dht_id):
        sysID = self.tempHumid[dht_id]['sysID']
        return list(filter(lambda s: self.sensor[s]['sysID'] == sysID, self.sensor))

    def send_feed_data(self, feed_id, value):
        client = self.client[self.pump[feed_id]['owner']]
        client.publish(feed_id, value)

    def writeSensorHistory(self):
        while True:
            time.sleep(self.POOL_TIME)
            current_time = datetime.now().strftime(self.TIME_FORMAT)
            for sensor_id in self.sensor: 
                self.logApp.writeSensorHistory("moisture", sensor_id, self.sensor[sensor_id]['value'], current_time)
            for dht_id in self.tempHumid:
                self.logApp.writeSensorHistory("temperature", dht_id, self.tempHumid[dht_id]['temp'], current_time)
                self.logApp.writeSensorHistory("humidity", dht_id, self.tempHumid[dht_id]['humid'], current_time)

    def changeWaterlevel(self, sensor_id):
        pumps = self.logApp.getPumpBySoilMoistureID(sensor_id)
        print(f'**************predict {sensor_id}********************')
        for pump_id, pump_status in pumps.items():
            if pump_status['auto'] == '1':
                moisture = int(self.sensor[pump_status['soilMoistureID']]['value'])
                
                dhts = self.getTempHumidSensor(sensor_id)
                temp = list(map(lambda s: int(self.tempHumid[s]['temp']), dhts))
                temp = sum(temp)/len(temp)

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

    # def autoWatering(self):
    #     while True:
    #         time.sleep(40)
    #         if self.sensorSendingCheck == True: # if sensor sent value continuously
    #             for pump_id in self.pump:
    #                 pump_status = self.logApp.getPumpStatus(pump_id)
    #                 if pump_status['auto'] == '1':
    #                     #moisture = int(self.logApp.getSoilMoistureValue(pump_status['soilMoistureID']))
    #                     moisture = int(self.sensor[pump_status['soilMoistureID']]['value'])
    #                     temp = int(self.tempHumid['dht0001']['temp'])

    #                     #if moisture < 100 and pump_status['waterLevel'] == '0':
    #                     if self.predictor.predict(moisture, temp) == 1 and pump_status['waterLevel'] == '0':
    #                         value = json.dumps({
    #                             "id": "11", 
    #                             "name": "RELAY", 
    #                             "data": "1",
    #                             "unit": ""
    #                         })
    #                         self.send_feed_data(pump_id,value)
    #                     #elif moisture > 500 and pump_status['waterLevel'] == '1':
    #                     elif self.predictor.predict(moisture, temp) == 0 and pump_status['waterLevel'] == 1:
    #                         value = json.dumps({
    #                             "id": "11", 
    #                             "name": "RELAY", 
    #                             "data": "0",
    #                             "unit": ""
    #                         })
    #                         self.send_feed_data(pump_id,value)
    #             self.sensorSendingCheck = False
    #         else: # if sensor did not send value continuously
    #             for pump_id in self.pump:
    #                 pump_status = self.logApp.getPumpStatus(pump_id)
    #                 if pump_status['auto'] == '1' and pump_status['waterLevel'] == '1':
    #                     value = json.dumps({
    #                         "id": "11", 
    #                         "name": "RELAY", 
    #                         "data": "0",
    #                         "unit": ""
    #                     })
    #                     self.send_feed_data(pump_id,value)