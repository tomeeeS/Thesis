import math

import pandas as pd
import numpy as np
import datetime as dt

from keras_visualizer import visualizer
from pandas import DataFrame, Series
from statsmodels.tsa.statespace.sarimax import SARIMAX
from tensorflow import keras
from tensorflow.python.keras import Input
from tensorflow.python.keras.engine.input_layer import InputLayer
from tensorflow.python.keras.layers import LSTM, Dense, GRU
from tensorflow.python.keras.models import load_model, Model, Sequential
from tensorflow.python.keras.optimizer_v2.adam import Adam
from tensorflow.python.keras.preprocessing.sequence import TimeseriesGenerator
from tensorflow.python.keras.preprocessing.timeseries import timeseries_dataset_from_array
import geopandas as gpd
import matplotlib.pyplot as plt
import dataframe_image as dfi
from sklearn.preprocessing import MinMaxScaler
from sklearn.metrics import mean_squared_error
import matplotlib.dates as mdates
from tensorflow.python.keras.utils.vis_utils import plot_model

from SubArea import SubArea
from source.search_best_arima import search_best_arima

EARLY_STOPPING_PATIENCE = 5

lat_slices_count = 20
lon_slices_count = 25
sub_areas = []


def process_by_longitudes(current_lon, next_lon, center_lat):
    for j in range(0, lon_slices_count):
        center_lon = (current_lon + next_lon) / 2.0

        sub_areas.append((SubArea(current_lat, next_lat, current_lon, next_lon), (center_lat, center_lon)))
        # print('%s %s %s %s' % (current_lat, next_lat, current_lon, next_lon))

        current_lon = next_lon
        next_lon = current_lon + lon_step


def compute_subareas():
    traffic['SubArea'] = traffic.apply(
        (lambda row: next((i for (i, v) in enumerate(sub_areas)
                           if v[0].contains(row[lat], row[lon])))),
        axis=1
    )
    traffic['Center latitude'] = traffic.apply(
        (lambda row: next((v[1][0] for (i, v) in enumerate(sub_areas)
                           if v[0].contains(row[lat], row[lon])))),
        axis=1
    )
    traffic['Center longitude'] = traffic.apply(
        (lambda row: next((v[1][1] for (i, v) in enumerate(sub_areas)
                           if v[0].contains(row[lat], row[lon])))),
        axis=1
    )
    traffic.to_csv('cells_with_subareas.csv')


def make_subareas():
    global lon_slices_count, lon_step, current_lat, next_lat, sub_areas, current_lon, next_lon
    lat_step = (max_lat - min_lat) / lat_slices_count
    lon_step = (max_lon - min_lon) / lon_slices_count

    current_lat = min_lat
    next_lat = min_lat + lat_step

    for i in range(0, lat_slices_count):
        current_lon = min_lon
        next_lon = current_lon + lon_step
        center_lat = (current_lat + next_lat) / 2.0

        process_by_longitudes(current_lon, next_lon, center_lat)

        current_lat = next_lat
        next_lat = current_lat + lat_step
    print(len(sub_areas))


def make_subarea_count():
    global traffic
    traffic['Count'] = traffic.groupby([year, hour, 'SubArea'], as_index=False).cumcount() + 1
    traffic = traffic[
        traffic.groupby([year, hour, 'SubArea']).Count.transform('max').eq(
            traffic.Count)]
    traffic.sort_values([year, hour, 'SubArea'], inplace=True)
    traffic = traffic.drop(columns=['Unnamed: 0', 'Unnamed: 0.1'])


def add_missing_zero_counts():
    global traffic
    traffic = traffic.drop(columns=[lat, lon])
    for y in range(2012, 2018):
        for h in range(0, 24):
            for i in range(0, lat_slices_count * lon_slices_count):
                if traffic.loc[(traffic[year] == y) &
                               (traffic[hour] == h) &
                               (traffic['SubArea'] == i)].empty:
                    traffic = traffic.append({
                        year: y,
                        hour: h,
                        'SubArea': i,
                        'Count': 0
                    }, ignore_index=True)
        print('%s done' % y)


def make_datetime(row): 
    day_number = row[year] - 2011
    return dt.datetime(2012, 1, day_number, row[hour], 0, 0)


def transform_date():
    global traffic
    date = 'Date'
    traffic[date] = traffic.apply(make_datetime, axis=1)
    traffic = traffic.drop(columns=[year, hour])
    traffic = traffic.set_index(keys=[date])


def transform_to_columns():
    global traffic, traffic_column_subareas
    dates = traffic.index.unique()
    for d in range(len(dates)):
        counts = {'date': dates[d]}
        for i in range(len(sub_areas)):
            counts[str(i)] = traffic.iloc[d * i,  1]
        traffic_column_subareas = traffic_column_subareas.append(counts, ignore_index=True)
    traffic_column_subareas = traffic_column_subareas.set_index(keys=['date'])


def prepare_data():
    global year, hour, lat, lon, min_lat, max_lat, min_lon, max_lon, traffic, traffic_column_subareas
    time = 'Time Of Stop'
    date = 'Date Of Stop'
    year = 'Year'
    hour = 'Hour'
    lat = 'Latitude'
    lon = 'Longitude'
    min_lat = 39.00415
    max_lat = 39.11688
    min_lon = -77.17801
    max_lon = -76.98474

    # tv = pd.read_csv('Traffic_Violations.csv', usecols=[date, time, lat, lon])
    # dfi.export(tv.head(), "original_data.png", max_cols=30)

    data = pd.read_csv('../traffic.csv', parse_dates=True)
    print(data.head())
    d2 = pd.DataFrame(columns=[lat, lon, 'wkt'])
    traffic = pd.read_csv('../aggregated_subareas.csv', index_col=0)
    make_subareas()
    print(data.count()[0] / float(len(sub_areas)) / 24.0 / 7 / 6 / 12)
    compute_subareas()
    traffic = traffic.drop(columns=[lat, lon])
    make_subarea_count()
    add_missing_zero_counts()
    transform_date()
    traffic_column_subareas = pd.DataFrame()
    transform_to_columns()
    traffic.to_csv('aggregated_subareas.csv')
    traffic_column_subareas.to_csv('aggregated_subareas_columns.csv')
    print(traffic_column_subareas.head())


def get_early_stopping():
    return keras.callbacks.EarlyStopping(
        monitor="val_loss", min_delta=0, patience=EARLY_STOPPING_PATIENCE)


def get_model_checkpointer(path_checkpoint):
    return keras.callbacks.ModelCheckpoint(
        monitor="val_loss",
        filepath=path_checkpoint,
        verbose=1,
        save_weights_only=True,
        save_best_only=True,
    )


def train(model_name: str):
    path_checkpoint = "../model_checkpoint.h5"
    es_callback = get_early_stopping()
    checkpointer = get_model_checkpointer(path_checkpoint)
    history = gru_model.fit(
        train_generator,
        epochs=epoch_count,
        validation_data=validation_generator,
        callbacks=[es_callback, checkpointer],
        # batch_size=batch_size
    )
    gru_model.save('trained_{}_model'.format(model_name))


def min_max_scale(x):
    return x.applymap(lambda i: i / scale_divider)


def inverse_scale(data):
    return [d * scale_divider for d in data]


def set_model_gru():
    model = Sequential(layers=[
        InputLayer(input_shape=(past_observation_count, feature_count)),
        GRU(past_observation_count * 2, activation='tanh', return_sequences=True),
        GRU(past_observation_count, activation='tanh'),
        Dense(feature_count)
    ])
    plot_model(model, to_file='gru_model.png', show_shapes=True, show_layer_names=False)
    model.compile(optimizer=Adam(learning_rate=learning_rate), loss="mse")
    return model


# use tf.compat.v1.keras.layers.CuDNNLSTM for GPU
def set_model_lstm():
    model = Sequential(layers=[
        InputLayer(input_shape=(past_observation_count, feature_count)),
        LSTM(past_observation_count * 2, activation='tanh', return_sequences=True),
        LSTM(past_observation_count, activation='tanh'),
        Dense(feature_count)
    ])
    plot_model(model, to_file='lstm_model.png', show_shapes=True, show_layer_names=False)
    model.compile(optimizer=Adam(learning_rate=learning_rate), loss="mse")
    return model


def plot_seasonal_decompose():
    # Extract and plot trend, seasonal and residuals.
    from statsmodels.tsa.seasonal import seasonal_decompose

    decomposed = seasonal_decompose(univariate_df,
                                    model='additive')
    trend = decomposed.trend
    seasonal = decomposed.seasonal
    residual = decomposed.resid
    plt.figure(figsize=(12, 8))
    plt.subplot(411)
    plt.plot(univariate_df, label='Original')
    plt.legend(loc='upper left')
    plt.subplot(412)
    plt.plot(trend, label='Trend')
    plt.legend(loc='upper left')
    plt.subplot(413)
    plt.plot(seasonal, label='Seasonal')
    plt.legend(loc='upper left')
    plt.subplot(414)
    plt.plot(residual, label='Residual')
    plt.legend(loc='upper left')
    plt.show()


def stationarity_test():
    # Is the data stationary?
    from pmdarima.arima import ADFTest
    adf_threshold = 0.05
    adf_test = ADFTest(alpha=adf_threshold)
    adf_test.should_diff(univariate_df)
    # Not stationary...
    # Dickey-Fuller test
    from statsmodels.tsa.stattools import adfuller
    adf, pvalue, usedlag_, nobs_, critical_values_, icbest_ = adfuller(univariate_df)
    print("pvalue = ", pvalue, " if above %.2f, data is not stationary" % adf_threshold)
    # Since data is not stationary, we may need SARIMA and not just ARIMA


def set_model_arima():
    return SARIMAX(
        univariate_df,
        order=(5, 1, 0),
        seasonal_order=(5, 1, 0, 12)
    )


def train_arima():
    result = arima_model.fit()
    return result


def evaluate():
    global gru_rmse, arima_rmse, lstm_rmse
    # average rmse of an element-pair
    gru_rmse = mean_squared_error(
        gru_test_predict,
        test_y_inverse[past_observation_count:],
        squared=False
    )
    print('gru_rmse: %.2f' % gru_rmse)
    lstm_rmse = mean_squared_error(
        lstm_test_predict,
        test_y_inverse[past_observation_count:],
        squared=False
    )
    print('lstm_rmse: %.2f' % lstm_rmse)
    arima_rmse = mean_squared_error(
        arima_test_predict,
        test_y_inverse,
        squared=False
    )
    print('arima_rmse: %.2f' % arima_rmse)


def plot():
    f = plt.figure()
    f.set_figwidth(14)

    plt.plot(validation_set.iloc[:, subarea_index], label='validation set (ground truth)')

    idx = pd.date_range(traffic.index[-len(gru_test_predict)], periods=len(gru_test_predict), freq='H')
    plt.plot(Series(gru_test_predict, index=idx), label='GRU, rmse: %.2f' % gru_rmse)
    plt.plot(Series(lstm_test_predict, index=idx), label='LSTM, rmse: %.2f' % lstm_rmse)
    plt.plot(Series(arima_test_predict, index=idx), label='SARIMA, rmse: %.2f' % arima_rmse)

    plt.legend()
    plt.show()


def train_models():
    train('gru')
    train('lstm')


prepare_data()

# prepare for training:
traffic = pd.read_csv('aggregated_subareas_columns.csv', index_col=0, parse_dates=True)

idx = pd.date_range(traffic.index[0], periods=traffic.shape[0], freq='H')
traffic.set_index(idx, inplace=True)

# l = [str(x) for x in range(500)]
# dfi.export(traffic.head(), "transformed_data.png", max_cols=6)
# traffic = traffic['2']
print(traffic.shape)

past_observation_count = 24
future_observation_count = 2
learning_rate = 0.001
epoch_count = 30
feature_count = 500

subarea_index = 1

split_ratio = 0.6
training_data_size = int(split_ratio * int(traffic.shape[0]))
test_data_size = int(int(traffic.shape[0]) - training_data_size)

sequence_length = int(past_observation_count)
# batch_size = 128

train_set = traffic.iloc[0:training_data_size]
validation_set = traffic.iloc[training_data_size - 1:len(traffic)]

scale_divider = int(max(traffic.values.flatten()))
# reshape input to be [samples, (time steps if not 1,) features] & min-max scale them
train_set_ndarray = np.reshape(
    min_max_scale(train_set).values.reshape(-1),
    (train_set.shape[0], feature_count)
)
validation_set_ndarray = np.reshape(
    min_max_scale(validation_set).values.reshape(-1),
    (validation_set.shape[0], feature_count)
)


train_generator = TimeseriesGenerator(
    train_set_ndarray,
    train_set_ndarray,
    length=sequence_length,
    # batch_size=batch_size
)
print("Total number of samples in the generated data = ", len(train_generator))

validation_generator = TimeseriesGenerator(
    validation_set_ndarray,
    validation_set_ndarray,
    length=sequence_length,
    # batch_size=batch_size
)

univariate_df = DataFrame(traffic[str(subarea_index)]).asfreq('H')

# stationarity_test()
# plot_seasonal_decompose()
# search_best_arima(univariate_df)

gru_model = set_model_gru()
lstm_model = set_model_lstm()
arima_model = set_model_arima()


# train_models()  # <<<< ------- Comment out if we only want predictions  ------- >>>>

trained_gru = load_model('trained_gru_model')
trained_lstm = load_model('trained_lstm_model')

# gru predict
# gru_train_predict = trained_gru.predict(train_generator)
gru_test_predict = trained_gru.predict(validation_generator)

# lstm predict
# lstm_train_predict = trained_lstm.predict(train_generator)
lstm_test_predict = trained_lstm.predict(validation_generator)

# fit arima:
trained_arima = train_arima()
# arima predict
arima_test_predict = trained_arima.predict(int(univariate_df.shape[0] * split_ratio), univariate_df.shape[0])\
    .rename('Predicted traffic')

# gru_train_predict = inverse_scale(gru_train_predict[-plot_test_size:])
# train_y_inverse = inverse_scale(train_set_ndarray)  # list, 201
gru_test_predict = np.array(inverse_scale(gru_test_predict))[:, subarea_index]
lstm_test_predict = np.array(inverse_scale(lstm_test_predict))[:, subarea_index]
# test_y_inverse has past_observation_count more elements, so we start to
test_y_inverse = np.array(inverse_scale(validation_set_ndarray))[:, subarea_index]

lstm_rmse = gru_rmse = arima_rmse = None
evaluate()
plot()
