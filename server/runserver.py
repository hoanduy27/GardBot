from flask import Flask
from flask import render_template, request, redirect, url_for, jsonify, make_response
import threading
import atexit
import json
from mqtt import MQTT

mqtt = MQTT()
app = Flask(__name__)
@app.route('/send_pump_signal', methods=['POST'])
def send_pump_signal():
    value = request.form['value']
    feed_id = request.form['feed_id']
    print(feed_id)
    value = {
        "id": "11", 
        "name": "RELAY", 
        "data": f"{value}",
        "unit": ""
    }

    mqtt.send_feed_data(feed_id, json.dumps(value))
    return 'OK'       

if __name__ == '__main__':
    app.run(port = 5000, debug = True)
