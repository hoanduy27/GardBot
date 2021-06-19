import pyrebase
from datetime import datetime
import yaml
import os

class LogApp:
    def __init__(self):
        self.TIME_FORMAT = "%d-%m-%Y-%H:%M:%S"
        self.MAX_RECORD = 20

        cur_dir = os.path.abspath(__file__)
        cur_dir = os.path.dirname(cur_dir)
        with open(f'{cur_dir}/../config.yml') as conf:
            self.config = yaml.safe_load(conf)['firebase']
        
        self.app = pyrebase.initialize_app(self.config)
        self.db = self.app.database()

        # Fix double quotes bug
        def noquote(s):
            return s
        pyrebase.pyrebase.quote = noquote
        
    def changePumpStatus(self, feed_id, value):
        self.db.child('pump/' + feed_id + '/waterLevel').set(value)

    def changeSoilMoisute(self, feed_id, value):
        self.db.child('sensor/soilMoisture/' + feed_id + '/moisture').set(value)

    def changeTempHumid(self, feed_id, temperature, humidity):
        self.db.child('sensor/dht/' + feed_id + '/temperature').set(temperature)
        self.db.child('sensor/dht/' + feed_id + '/humidity').set(humidity)

    def writeWateringHistory(self, feed_id, data, timestamp):
        path = f'history/watering/{feed_id}'

        # Delete last record if number of records > MAX_RECORD
        records = self.db.child(path).get().val()
        if(records != None and len(records) >= self.MAX_RECORD):
            self.deleteLastHistoryRecord('watering', feed_id)

        # Create history 
        self.db.child(f'{path}/{timestamp}').set(data)

    def writeSensorHistory(self, collection, feed_id, value, timestamp):
        path = f'history/{collection}/{feed_id}'

        # Delete last record if number of records in this sensor > MAX_RECORD
        records = self.db.child(path).get().val()
        if(records != None and len(records) >= self.MAX_RECORD):
            # Find last history record 
            time_records = list(map(lambda t_rec:\
                datetime.strptime(t_rec, self.TIME_FORMAT),
                records.keys()
            ))
            last_record = min(time_records).strftime(self.TIME_FORMAT)
            # Delete it 
            self.db.child(path).child(last_record).remove()
        # Create history 
        json_data = {'value' : value}
        self.db.child(f'{path}/{timestamp}').set(json_data)

    def deleteLastHistoryRecord(self, collection, feed_id):
        path = f'history/{collection}/{feed_id}/'
        key = self.lastHistoryRecord(collection, feed_id)
        self.db.child(path).child(key).remove()
    
    def lastHistoryRecord(self, collection, feed_id):
        path = f'history/{collection}/{feed_id}'
        history = self.db.child(path).get().val()
        time_records = list(map(lambda t_rec: \
            datetime.strptime(t_rec, self.TIME_FORMAT), 
            history.keys())
        )
        return min(time_records).strftime(self.TIME_FORMAT)

    def getPumpBySoilMoistureID(self, sensor_id):
        status = self.db.child('pump').order_by_child("soilMoistureID").equal_to(sensor_id).get().val()
        return status

    def getPumpStatus(self, pump_id):
        return self.db.child('pump').child(pump_id).get().val()

    def getSoilMoistureValue(self, sensor_id):
        return self.db.child('sensor/soilMoisture').child(sensor_id).get().val()['moisture']

    def getHumidValue(self, sensor_id):
        return self.db.child('sensor/dht').child(sensor_id).get().val()['humidity']

    def getTempValue(self, sensor_id):
        return self.db.child('sensor/dht').child(sensor_id).get().val()['temperature']

    def getTempHumidID(self, sensor_id):
        sysID = self.db.child(f'sensor/soilMoisture/{sensor_id}').get().val()['sysID']
        return list(self.db.child(f'sensor/dht').order_by_child('sysID').equal_to(sysID).get().val())[0]

    def getSensors(self):
        return self.db.child(f'sensor').get().val()
