from firebase import firebase
from firebase.firebase import FirebaseApplication, FirebaseAuthentication
from firebase import jsonutil
import json
from datetime import date, datetime
import yaml

class LogApp:
    def __init__(self):
        self.TIME_FORMAT = "%d-%m-%y-%H:%M:%S"
        self.MAX_RECORD = 10

        with open('config.yml') as conf:
            config = yaml.safe_load(conf)
        self.secret = config['firebase']['secret']
        self.email = config['firebase']['email']
        self.db = config['firebase']['db']

        self.authentication = FirebaseAuthentication(
            secret=self.secret,
            email=self.email
        )
        self.app = FirebaseApplication(
            self.db, 
            authentication=self.authentication
        )        
        
    def changePumpStatus(self, feed_id, value):
        self.app.put('/pump/' + feed_id, 'waterLevel', value)

    def changeSoilMoisute(self, feed_id, value):
        self.app.put('/sensor/soilMoisture/' + feed_id, 'moisture', value)

    def writeSensorHistory(self, feed_id, value):
        path = 'history/moisture/'

        # Delete last record if number of records in this sensor > MAX_RECORD
        collection = self.app.get(path, feed_id)
        if(collection != None and len(collection) >= self.MAX_RECORD):
            self.deleteLastHistoryRecord('moisture', feed_id)

        # Create history 
        json_data = {'value' : value}
        current_time = datetime.now().strftime(self.TIME_FORMAT)
        self.app.put(path + feed_id, current_time, json_data)

    def deleteLastHistoryRecord(self, collection, feed_id):
        key = self.lastHistoryRecord(collection, feed_id)
        self.app.delete('/history/{}/{}'.format(collection, feed_id), key)
    
    def lastHistoryRecord(self, collection, feed_id):
        history = self.app.get('/history/' + collection, feed_id)
        time_records = list(map(lambda t_rec: \
            datetime.strptime(t_rec, self.TIME_FORMAT), 
            history.keys())
        )
        return min(time_records).strftime(self.TIME_FORMAT)