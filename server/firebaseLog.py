
import pyrebase
from datetime import datetime
import yaml

class LogApp:
    def __init__(self):
        self.TIME_FORMAT = "%d-%m-%y-%H:%M:%S"
        self.MAX_RECORD = 10

        with open('config.yml') as conf:
            self.config = yaml.safe_load(conf)['firebase']
        
        self.app = pyrebase.initialize_app(self.config)
        self.db = self.app.database()
        """
        self.authentication = FirebaseAuthentication(
            secret=self.secret,
            email=self.email
        )
        self.app = FirebaseApplication(
            self.db, 
            authentication=self.authentication
        )"""    
        
    def changePumpStatus(self, feed_id, value):
        self.db.child('pump/' + feed_id + '/waterLevel').update(value)

    def changeSoilMoisute(self, feed_id, value):
        self.db.child('sensor/soilMoisture/' + feed_id + '/moisture').update(value)

    def changeTempHumid(self, feed_id, temperature, humidity):
        self.db.child('sensor/dht/' + feed_id + '/temperature').update(temperature)
        self.db.child('sensor/dht/' + feed_id + '/humidity').update(humidity)

    def writeWateringHistory(self, feed_id, data, timestamp):
        path = f'history/watering/{feed_id}'

        # Delete last record if number of records > MAX_RECORD
        records = self.db.child(path).get().val()
        if(records != None and len(records) >= self.MAX_RECORD):
            self.deleteLastHistoryRecord('watering', feed_id)

        # Create history 
        self.db.child(path).set({timestamp : data})

    def writeSensorHistory(self, collection, feed_id, value, timestamp):
        path = f'history/{collection}/{feed_id}'

        # Delete last record if number of records in this sensor > MAX_RECORD
        records = self.db.child(path).get().val()
        if(records != None and len(records) >= self.MAX_RECORD):
            self.deleteLastHistoryRecord(collection, feed_id)

        # Create history 
        json_data = {'value' : value}
        self.db.child(path).set({timestamp : json_data})

    def deleteLastHistoryRecord(self, collection, feed_id):
        path = f'history/{collection}/{feed_id}/'
        key = self.lastHistoryRecord(collection, feed_id)
        self.db.child(path).child(key).remove()
    
    def lastHistoryRecord(self, collection, feed_id):
        history = self.db.child('history/' + collection + '/' + feed_id).get().val()
        time_records = list(map(lambda t_rec: \
            datetime.strptime(t_rec, self.TIME_FORMAT), 
            history.keys())
        )
        return min(time_records).strftime(self.TIME_FORMAT)

    def getPumpBySoilMoistureID(self, sensor_id):
        status = self.db.child('pump').order_by_child('soilMoistureID').equal_to(sensor_id).val()
        return status

    def getPumpStatus(self, pump_id):
        return self.db.child('pump').child(pump_id).val()

    def getSoilMoistureValue(self, sensor_id):
        return self.db.child('sensor/soilMoisture').child(sensor_id).val()['moisture']

    def getHumidValue(self, sensor_id):
        return self.db.child('sensor/dht').child(sensor_id).val()['humidity']

    def getTempValue(self, sensor_id):
        return self.db.child('sensor/dht').child(sensor_id).val()['temperature']