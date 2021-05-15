import yaml
from Adafruit_IO import MQTTClient
import json
import sys
import time
import random

class MQTT:
    def __init__(self):

        with open('config.yml') as f:
            config = yaml.load(f, Loader = yaml.FullLoader)

        self.client = MQTTClient(config['IO_USERNAME'], config['IO_KEY'])
        self.feed = config['feed']
        self.is_open = False

        def connected(client):
            print('Connected to {0}'.format(self.feed))
            client.subscribe(self.feed)

        def disconnected(client):
            print('Disconnected from Adafruit IO!')
            sys.exit(1)

        def message(client, feed_id, payload):
            print('Feed {0} received new value: {1}'.format(feed_id, payload))

        def subscribe(client, userdata, mid, granted_qos):
            # This method is called when the client subscribes to a new feed.
            print('Subscribed to {0} with QoS {1}'.format(self.feed, granted_qos[0]))

        self.client.on_connect = connected
        self.client.on_disconnect= disconnected
        self.client.on_message = message
        self.client.on_subscribe = subscribe

        self.client.connect()
        self.client.loop_background()

    def get_feed_format(self, value):
        #return json.dumps({
        #    'id': '3',
        #    'name': 'SPEAKER',
        #    'data': str(value),
        #    'unit': ''
        #})
        return value

    def send_feed_data(self, value):
        """
            send value to MQTT server
            Arguments:
                - value: integer, range from 0 to 1023
            Return: None
        """
        self.client.publish(self.feed, self.get_feed_format(value))

    def receive_door_state(self):
        """
            receive the state of door
            Arguments: None
            Return: True if the door is open, otherwise return False
        """
        return self.is_open

mqtt = MQTT()

from flask import Flask
from flask import render_template, request, redirect, url_for, jsonify, make_response

app = Flask(__name__)

@app.route('/test', methods=['POST'])
def test():
    value = request.form['value']
    mqtt.send_feed_data(value)

if __name__ == '__main__':
    app.run(port = 5000, debug = True)


#test = MQTT()
#current = 0
#while(True):
#    value = 1 if current == 0 else 0
#    test.send_feed_data(value)
#    current = value
#    time.sleep(1)