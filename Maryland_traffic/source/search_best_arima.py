# the code (and comments) in this file is taken from
#   https://github.com/bnsreenu/python_for_microscopists/blob/master/
#   163-Intro_to_time_series_Forecasting_using_ARIMA.py


def search_best_arima(data_frame):
    # We can go through the exercise of making the data stationary and performing ARIMA
    # Or let auto_arima provide the best model (e.g. SARIMA) and parameters.
    # Auto arima suggests best model and parameters based on
    # AIC metric (relative quality of statistical models)
    from pmdarima.arima import auto_arima
    # Autoarima gives us bet model suited for the data
    # p - number of autoregressive terms (AR)
    # q - Number of moving avergae terms (MA)
    # d - number of non-seasonal differences
    # p, d, q represent non-seasonal components
    # P, D, Q represent seasonal components
    arima_model = auto_arima(data_frame, start_p=1, d=1, start_q=1,
                             max_p=5, max_q=5, max_d=5, m=12,
                             start_P=0, D=1, start_Q=0, max_P=5, max_D=5, max_Q=5,
                             seasonal=True,
                             trace=True,
                             error_action='ignore',
                             suppress_warnings=True,
                             stepwise=True, n_fits=50)
    # To print the summary
    print(arima_model.summary())  # Note down the Model and details.
    # Model: SARIMAX(0, 1, 1)x(2, 1, [], 12)
