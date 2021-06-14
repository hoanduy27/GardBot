import numpy as np
from sklearn import neighbors
from sklearn.preprocessing import MinMaxScaler
import pandas as pd
import os


class PredictionModel:
    def __init__(self):
        cur_dir = os.path.abspath(__file__)
        cur_dir = os.path.dirname(cur_dir)
        data = pd.read_csv(f'{cur_dir}/data.csv')

        self.X = data.iloc[1:, 1:-1].values
        self.y = -(data.iloc[1:, -1].values - 1)
        self.clf = neighbors.KNeighborsClassifier(n_neighbors=3, p=2)
        self.__preprocessing()
        self.__train()

    def __preprocessing(self):
        self.X = self.__scale(self.X)

    def __train(self):
        self.clf.fit(self.X, self.y)
    
    def __scale(self, data):
        scaler = MinMaxScaler()
        scaler.fit(np.array([[0, 0], [1023, 50]]))
        return scaler.transform(data) 

    def predict(self, moisture, temp):
        data = np.array([[moisture, temp]])
        return self.clf.predict(self.__scale(data))[0]