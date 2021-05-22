from flask import Flask
from flask import render_template, request, redirect, url_for, jsonify, make_response
from threading import Thread
from firebase.firebase import FirebaseApplication, FirebaseAuthentication

from mqtt import mqtt

def read_sensor():
    pass

def read_write_firebase():
    while True:
        pass

app = Flask(__name__)

@app.route('/send_pump_signal', methods=['POST'])
def send_pump_signal():
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