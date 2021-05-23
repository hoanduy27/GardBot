import sys
import json
import yaml
from Adafruit_IO import MQTTClient

class MQTT:
    def __init__(self):
        with open('config_demo.yml') as conf:
            config = yaml.safe_load(conf)

        self.client = MQTTClient(config['IO_USERNAME'], config['IO_KEY'])

        self.data = {key: 0 for key in config['feed']['input']}
        self.data.update({key: 0 for key in config['feed']['output']})

        def connected(client):
            for item in self.data.keys():
                self.client.subscribe(item)
                #Publish the last published value
                self.client.receive(item)

        def message(client, feed_id, payload):
            self.data[feed_id] = payload
            print('Feed {0} received new value: {1}'.format(feed_id, payload))

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
        self.client.publish(feed_id, value)

    def listen(self):
        self.client.loop_blocking()

mqtt = MQTT()

from flask import Flask
from flask import render_template, request, redirect, url_for, jsonify, make_response

app = Flask(__name__)

@app.route('/test', methods=['POST'])
def test():
    value = request.form['value']
    feed_id = request.form['feed_id']
    print(feed_id)
    mqtt.send_feed_data(feed_id, value)
    return 'OK'

@app.route('/read_sensor', methods=['POST'])
def read_sensor():
    feed_id = request.form.get('feed_id')
    return str(mqtt.data[feed_id])

if __name__ == '__main__':
    app.run(port = 5000, debug = True)


