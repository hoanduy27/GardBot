from random import randint
from log.firebaseLog import LogApp

class Predict:
    def __init__(self):
        pass

    def random_moisture(self, low, high):
        rd_group = randint(0,2)

        moisture_range = {0: [0, low], 1: [low+1, high-1], 2: [high, 1023]}
        return randint(moisture_range[rd_group][0], moisture_range[rd_group][1])
a = LogApp()
status = a.getPumpStatus('p0001')
s = status['auto']
print(s)