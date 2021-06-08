import sys
import json
import yaml
import time
from Adafruit_IO import MQTTClient, Client
from firebaseLog import LogApp
from threading import Thread

class MQTT:
    def __init__(self):
        with open('config.yml') as conf:
            config = yaml.safe_load(conf)

        self.POOL_TIME = 10
        pump = config['feed']['output']
        sensor = config['feed']['input']['sensor']

        # Change when we have official announcement about json format
        self.client = MQTTClient(config['IO_USERNAME'], config['IO_KEY'])

        self.pump = {key: None for key in pump}
        self.sensor = {key: None for key in sensor}

        self.logApp = LogApp()

        self.writeHistory_loop = Thread(target=self.writeSensorHistory)
        self.writeHistory_loop.daemon = True
        
        def connected(client):
            for feed_id in (list(self.pump.keys()) + list(self.sensor.keys())):
                client.subscribe(feed_id + '/json')
                #Publish the last published value
                client.receive(feed_id + '/json')

        def message(client, feed_id, payload):
            #print(payload)
            payload = json.loads(payload)
            try:
                value = payload['last_value']
            except:
                value = payload['value']

            if(feed_id in self.pump):
                # Depends on json format
                self.pump[feed_id] = value
                self.logApp.changePumpStatus(feed_id, value)

            elif(feed_id in self.sensor):   
                # Depends on json format
                self.sensor[feed_id] = value
                self.logApp.changeSoilMoisute(feed_id, value)

            # Depends on json format
            print('Feed {0} received new value: {1}'.format(feed_id, value))
            
        def disconnected(client):
            print('Disconnected from Adafruit IO!')
            sys.exit(1)
            
        def subscribe(client, userdata, mid, granted_qos):
            # This method is called when the client subscribes to a new feed.
            print("Subscribe pumps and sensors")
            #print('Subscribed to {0} with QoS {1}'.format(userdata, granted_qos[0]))

        self.client.on_connect = connected
        self.client.on_message = message
        self.client.on_disconnect = disconnected
        self.client.on_subscribe = subscribe
        self.client.connect()
        #self.writeHistory_loop.start()
        self.client.loop_background()

    def send_feed_data(self, feed_id, value):
        value_json = '{"value: {}"}'.format(value)
        self.client.publish(feed_id + '/json', value_json)

    def writeSensorHistory(self):
        while True:
            time.sleep(self.POOL_TIME)
            for sensor_id in self.sensor: 
                self.logApp.writeSensorHistory(sensor_id, self.sensor[sensor_id])
