import numpy as np
from sklearn import neighbors
from sklearn.preprocessing import MinMaxScaler
import pandas as pd

class PredictionModel:
    def __init__(self):
        data = pd.read_csv('data.csv')
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

# # read
# data = pd.read_csv('data.csv')


# # split
# X_train, X_test, y_train, y_test = train_test_split(X, y, test_size = 50)

# # preprocessing
# scaler = MinMaxScaler()
# scaler.fit(X_train)
# scaler.fit(X_test)
# X_train = scaler.transform(X_train)
# X_test= scaler.transform(X_test)

# # plotting
# counter = Counter(y_train)
# print(counter)
# for label, _ in counter.items():
#   row_ix = np.where(y_train == label)[0]
#   plt.scatter(X_train[row_ix, 0], X_train[row_ix, 1], label=str(label))
# plt.legend()


# # train
# clf = neighbors.KNeighborsClassifier(n_neighbors=3, p=2)
# clf.fit(X_train, y_train)
# y_pred = clf.predict(X_test)
# y_score = clf.predict_proba(X_test)

# fpr, tpr, _ = roc_curve(y_test, y_score[:, 1])
# auc_s = roc_auc_score(y_test, y_score[:, 1])

# print(f"Actual\n{y_test}")
# print(f'Predict\n{y_pred}')

# cm = confusion_matrix(y_test, y_pred)
# tn, fp, fn, tp = cm.ravel()
# print(f'Precision: {tp/(tp+fp)}')
# print(f'Recall: {tp/(tp+fn)}')
# err = 1 - accuracy_score(y_test, y_pred)

# plt.plot(fpr, tpr, 'b', label='AUC = %0.2f'%auc_s)
# plt.legend()
# plt.show()