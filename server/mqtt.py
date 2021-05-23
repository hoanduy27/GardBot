import sys
import json
import yaml
from Adafruit_IO import MQTTClient, Client
from firebaseLog import LogApp

class MQTT:
    def __init__(self):
        with open('config.yml') as conf:
            config = yaml.safe_load(conf)

        self.pump = config['feed']['output']
        self.sensor = config['feed']['input']['sensor']

        # Change when we have official announcement about json format
        self.client = MQTTClient(config['IO_USERNAME'], config['IO_KEY'])

        self.data = {key: '0' for key in self.pump}
        self.data.update({key: '0' for key in self.sensor})

        self.logApp = LogApp()
        
        def connected(client):
            for item in self.data.keys():
                client.subscribe(item + '/json')
                #Publish the last published value
                client.receive(item + '/json')

        def message(client, feed_id, payload):
            #print(payload)
            payload = json.loads(payload)
            try:
                value = payload['last_value']
            except:
                value = payload['value']

            if(feed_id in self.pump):
                # Depends on json format
                self.data[feed_id] = value
                self.logApp.changePumpStatus(feed_id, value)

            elif(feed_id in self.sensor):   
                # Depends on json format
                self.data[feed_id] = value
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
        self.client.loop_background()

    def send_feed_data(self, feed_id, value):
        value_json = '{"value: {}"}'.format(value)
        self.client.publish(feed_id + '/json', value_json)

    def listen(self):
        self.client.loop_blocking()


#mqtt = MQTT()
