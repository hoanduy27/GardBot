from firebase.firebase import FirebaseApplication, FirebaseAuthentication
from datetime import date, datetime

class LogApp:
    def __init__(self):
        self.TIME_FORMAT = "%d-%m-%y-%H:%M:%S"
        self.MAX_RECORD = 10

        self.authentication = FirebaseAuthentication(
            secret='fzXrGa22e78ybBS92CCNuUsxt3j9ciWJ3Jhu7WbS',
            email='hoanduy27@gmail.com'
        )
        self.app = FirebaseApplication(
            'https://garbbot-default-rtdb.asia-southeast1.firebasedatabase.app/', 
            authentication=self.authentication
        )
        # self.sensors = self.app.get('/sensor', 'soilMoisture').keys()
        # self.historyCount = {
        #     'moisture': {}
        # }
        # for sensor in self.sensors:
        #     def update(response):
        #         print(response)
        #     self.app.get_async('/history/moisture/', sensor, update)
            


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

    def lastHistoryRecord(self, collection, feed_id):
        history = self.app.get('/history/' + collection, feed_id)
        time_records = list(map(lambda t_rec: \
            datetime.strptime(t_rec, self.TIME_FORMAT), 
            history.keys())
        )
        return min(time_records).strftime(self.TIME_FORMAT)
        
    def deleteLastHistoryRecord(self, collection, feed_id):
        key = self.lastHistoryRecord(collection, feed_id)
        self.app.delete('/history/{}/{}'.format(collection, feed_id), key)
        