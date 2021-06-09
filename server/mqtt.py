import sys
import json
import yaml
import time
from Adafruit_IO import MQTTClient
from firebaseLog import LogApp
from datetime import date, datetime
from threading import Thread

class MQTT:
    def __init__(self):
        self.POOL_TIME = 20
        self.TIME_FORMAT = "%d-%m-%y-%H:%M:%S"

        with open('./config.yml') as conf:
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
        #pump_bbc = bbc['relay']
        sensor_bbc = bbc['soil']
        #tempHumid_bbc = bbc['dht']

        # Test server
        self.client = {username: MQTTClient(username, key)}
        # Real servers
        self.client.update({bbc_username[i]: MQTTClient(bbc_username[i], bbc_key[i]) for i in range(len(bbc_key))})

        self.pump = {key: {'owner': username, 'value': None} for key in pump}
        #self.pump.update({pump_bbc['feedId']: {'owner': pump_bbc['IO_OWNER'], 'value': None}})

        self.sensor = {key: {'owner': username, 'value': None} for key in sensor}
        self.sensor.update({sensor_bbc['feedId']: {'owner': sensor_bbc['IO_OWNER'], 'value': None}})

        self.tempHumid = {key: {'owner': username, 'temp': None, 'humid': None} for key in tempHumid}
        #self.sensor.update({tempHumid_bbc['feedId']: {'owner': sensor_bbc['IO_OWNER'], 'temp': None, 'humid':None}})

        self.logApp = LogApp()

        self.writeHistory_loop = Thread(target=self.writeSensorHistory)
        self.writeHistory_loop.daemon = True
        
        def connected(client):
            for feed_id in self.pump:
                if client._username == self.pump[feed_id]['owner']:
                    client.subscribe(feed_id=feed_id)
                    client.receive(feed_id)

            for feed_id in self.sensor:
                if client._username == self.sensor[feed_id]['owner']:
                    client.subscribe(feed_id=feed_id, feed_user=self.sensor[feed_id]['owner'])
                    client.receive(feed_id)

            for feed_id in self.tempHumid:
                if client._username == self.tempHumid[feed_id]['owner']:
                    client.subscribe(feed_id=feed_id, feed_user=self.tempHumid[feed_id]['owner'])
                    client.receive(feed_id)

	    #self.pumpHistory = {key: {autoEnd:None, autoStart:  None, ...} for key in pump}


        def message(client, feed_id, payload):
            #print(payload)
            payload = json.loads(payload)
            value = payload['data']

            if(feed_id in self.pump):
                self.pump[feed_id]['value'] = value
                self.logApp.changePumpStatus(feed_id, value)
			## 1: #autoStart == None 
				#self.pumpHistory[key][autoStart] = ..
				#self.pumpHistory[key][startTime ] = ..
			## 0: [autoEnd]. [endTime] ....., write, set to None

            elif(feed_id in self.sensor):   
                self.sensor[feed_id]['value'] = value
                self.logApp.changeSoilMoisute(feed_id, value)
		    # Change waterlevel if auto status of this pump is enable (use prdict model): sendFeedData(pumpID, predictValue)
			            
            elif(feed_id in self.tempHumid):
                value = value.split('-')
                self.tempHumid[feed_id]['temp'] = value[0]
                self.tempHumid[feed_id]['humid'] = value[1]
                self.logApp.changeTempHumid(feed_id, value[0], value[1])                

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

        for client in self.client.values():
            client.loop_background()
        

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



