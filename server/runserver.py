from flask import Flask
from flask import render_template, request, redirect, url_for, jsonify, make_response
from threading import Thread
from firebase.firebase import FirebaseApplication, FirebaseAuthentication

from mqtt import MQTT

app = Flask(__name__)

@app.route('/send_pump_signal', methods=['POST'])
def send_pump_signal():
    value = request.form['value']
    feed_id = request.form['feed_id']
    print(feed_id)
    mqtt.send_feed_data(feed_id, value)
    return 'OK'

if __name__ == '__main__':
    mqtt = MQTT()
    app.run(port = 5000, debug = True)