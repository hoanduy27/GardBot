from firebase.firebase import FirebaseApplication, FirebaseAuthentication
import yaml

class LogApp:
    def __init__(self):
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
