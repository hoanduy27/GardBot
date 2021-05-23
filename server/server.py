from flask import Flask
from flask import render_template, request, redirect, url_for, jsonify, make_response
import threading
import atexit
from mqtt import MQTT

POOL_TIME = 5 #seconds
data_lock = threading.Lock()
app_thread = threading.Thread()
mqtt = MQTT()

def create_app():
    app = Flask(__name__)

    def interrupt():
        global app_thread
        app_thread.cancel()

    def log_data():
        global mqtt
        global app_thread
        with data_lock:
            for sensor in mqtt.sensor:
                # Write history for each POOL_TIME seconds
                mqtt.logApp.writeSensorHistory(sensor, mqtt.data[sensor])

        # Set the next thread to happen
        app_thread = threading.Timer(POOL_TIME, log_data, ())
        app_thread.start()   

    def start_log():
        # Do initialisation stuff here
        global app_thread
        # Create your thread
        app_thread = threading.Timer(POOL_TIME, log_data, ())
        app_thread.start()

    start_log()
    atexit.register(interrupt)
    return app

app = create_app()


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
