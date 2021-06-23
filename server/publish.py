import sys
import json
import random
from random import randint
import yaml
import time
from Adafruit_IO import MQTTClient, Client
from threading import Thread

class RandomPublisher:
    """
    This class is used for testing only, not being used in Flask server.
    Publish random value on gardbot's sensor feeds.
    """
    def __init__(self):
        with open('config.yml') as conf:
            config = yaml.safe_load(conf)

        self.POOL_TIME = 40
        pump = config['feed']['output']
        sensor = config['feed']['input']['sensor']
        tempHumid = config['feed']['input']['tempHumid']

        # Change when we have official announcement about json format
        self.client = MQTTClient(config['IO_USERNAME'], config['IO_KEY'])

        self.pump = {key: None for key in pump}
        self.sensor = {key: None for key in sensor}
        self.tempHumid = {key: None for key in tempHumid}

        self.publish_loop = Thread(target=self.publish_random)
        self.publish_loop.daemon = False

        def connected(client):
            test_feeds = list(self.pump.keys()) + list(self.sensor.keys()) + list(self.tempHumid.keys())
            for feed_id in test_feeds:
                client.subscribe(feed_id)
                #Publish the last published value
                client.receive(feed_id )

        def message(client, feed_id, payload):
            #print(payload)
            payload = json.loads(payload)
            value = payload['data']
            if(feed_id in self.tempHumid):
                value = value.split("-")

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
        self.publish_loop.start()
        self.client.loop_background()

    def send_feed_data(self, feed_id, value):
        self.client.publish(feed_id, value)

    def random_moisture(self, low, high):
        rd_group = randint(0,2)
        moisture_range = {0: [0, low], 1:[low+1, high-1], 2: [high, 1023]}
        return randint(moisture_range[rd_group][0], moisture_range[rd_group][1])

    def publish_random(self):
        while True:
            time.sleep(self.POOL_TIME)
            for feed_id in self.sensor.keys():
                print(feed_id)
                random_val = self.random_moisture(100, 500)
                
                value = {
                    "id": "9", 
                    "name": "SOIL", 
                    "data": f"{random_val}",
                    "unit": ""
                }
                self.send_feed_data(feed_id, json.dumps(value))

            for feed_id in self.tempHumid.keys():
                random_temp = randint(10, 40)
                random_humid = randint(0, 100)
                value = {
                    "id": "7", 
                    "name": "TEMP-HUMID", 
                    "data": f"{random_temp}-{random_humid}", 
                    "unit": "*C-%"
                }
                self.send_feed_data(feed_id, json.dumps(value))

mqtt = RandomPublisher()