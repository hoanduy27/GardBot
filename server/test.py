import yaml

class MQTT:
    def __init__(self):
        self.POOL_TIME = 10
        self.TIME_FORMAT = "%d-%m-%y-%H:%M:%S"

        with open('config.yml') as conf:
            config = yaml.safe_load(conf)

        pump = config['feed']['output']
        sensor = config['feed']['input']['sensor']
        tempHumid = config['feed']['input']['tempHumid']
        
        bbc = config['realServer']
        pump_bbc = bbc['relay']
        sensor_bbc = bbc['soil']
        tempHumid_bbc = bbc['dht']

        # Change when we have official announcement about json format
        username = config['IO_USERNAME']
        key = config['IO_KEY']

        self.pump = {key: {'owner': username, 'value': None} for key in pump}
        self.pump.update({pump_bbc['feedId']: {'owner': pump_bbc['IO_OWNER'], 'value': None}})

        self.sensor = {key: {'owner': username, 'value': None} for key in sensor}
        self.sensor.update({sensor_bbc['feedId']: {'owner': sensor_bbc['IO_OWNER'], 'value': None}})

        self.tempHumid = {key: {'owner': username, 'temp': None, 'humid': None} for key in tempHumid}
        self.tempHumid.update({tempHumid_bbc['feedId']: {'owner': sensor_bbc['IO_OWNER'], 'temp': None, 'humid':None}})