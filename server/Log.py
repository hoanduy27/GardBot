import pyrebase
from datetime import datetime
import yaml

class Log:
    def __init__(self):
        self.TIME_FORMAT = "%d-%m-%y-%H:%M:%S"
        self.MAX_RECORD = 10

        with open('config.yml') as conf:
            config = yaml.safe_load(conf)
        self.config = config['firebase']['firebaseConfig']
        self.firebase = pyrebase.initialize_app(self.config)
        self.db = self.firebase.database()

    def test(self):
        return self.db.child("pump/p0001").get().val()
log = Log()
print(log.test())

        
    # def changePumpStatus(self, feed_id, value):
    #     self.app.put('/pump/' + feed_id, 'waterLevel', value)

    # def changeSoilMoisute(self, feed_id, value):
    #     self.app.put('/sensor/soilMoisture/' + feed_id, 'moisture', value)
    # def changeTempHumid(self, feed_id, temperature, humidity):
    #     self.app.put('/sensor/dht/' + feed_id, 'temperature', temperature)
    #     self.app.put('/sensor/dht/' + feed_id, 'humidity', humidity)

    # def writeSensorHistory(self, collection_name, feed_id, value, timestamp):
    #     path = f'history/{collection_name}/'

    #     # Delete last record if number of records in this sensor > MAX_RECORD
    #     collection = self.app.get(path, feed_id)
    #     if(collection != None and len(collection) >= self.MAX_RECORD):
    #         self.deleteLastHistoryRecord(collection_name, feed_id)

    #     # Create history 
    #     json_data = {'value' : value}
    #     self.app.put(path + feed_id, timestamp, json_data)

    # def deleteLastHistoryRecord(self, collection, feed_id):
    #     key = self.lastHistoryRecord(collection, feed_id)
    #     self.app.delete('/history/{}/{}'.format(collection, feed_id), key)
    
    # def lastHistoryRecord(self, collection, feed_id):
    #     history = self.app.get('/history/' + collection, feed_id)
    #     time_records = list(map(lambda t_rec: \
    #         datetime.strptime(t_rec, self.TIME_FORMAT), 
    #         history.keys())
    #     )
    #     return min(time_records).strftime(self.TIME_FORMAT)

    # def getPump(self, sensor_id):
    #     pass

    # def isAuto(self, pump_id):
    #     pass 
    
    # def writePumpHistory(self, feed_id, value, timestamp):
    #     pass


