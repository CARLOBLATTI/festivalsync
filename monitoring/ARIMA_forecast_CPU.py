import pandas as pd
import matplotlib.pyplot as plt
from statsmodels.graphics.tsaplots import plot_acf, plot_pacf
from statsmodels.tsa.arima.model import ARIMA
from statsmodels.tsa.seasonal import seasonal_decompose
from statsmodels.tsa.stattools import adfuller
from sklearn.metrics import mean_squared_error
from statsmodels.tools.eval_measures import rmse
import statsmodels.api
import warnings

# Ignora i warning non critici
warnings.filterwarnings("ignore")

# Caricamento del file CSV
ts = pd.read_csv('prometheus_data_cpu.csv', header=0, parse_dates=[0], dayfirst=True, index_col=0)
tsr = ts.resample(rule='1T').mean()

# Rimuovi valori mancanti da tsr
#tsr_cleaned = tsr.dropna()

tsr_cleaned = tsr.interpolate(method='linear')

tsr.plot(title='Serie Temporale Originale')
tsr_cleaned.plot(title='Serie Temporale Pulita')

# Decomposizione stagionale per estrarre il trend
result = seasonal_decompose(tsr_cleaned, model='additive', period=310)
trend = result.trend.dropna()

# Plot del trend
plt.figure(figsize=(20, 8))
plt.plot(trend, label="Trend")
plt.title("Trend estratto dalla serie temporale")
plt.legend()
plt.show()

# Test di stazionarietà (ADF test)
adft = adfuller(trend, autolag='AIC')
print("\nAugmented Dickey-Fuller Test Results")
out = pd.Series(adft[0:4], index=['ADF test statistic', 'p-value', '# lags used', '# observations'])
for key, val in adft[4].items():
    out[f'critical value ({key})'] = val
print(out)

# Grafici ACF e PACF per determinare i parametri (p, q)
plot_acf(trend, title='Autocorrelation Function (ACF)', lags=40)
plt.show()

plot_pacf(trend, title='Partial Autocorrelation Function (PACF)', lags=40, method='ywm')
plt.show()

split_ratio = 0.8  # 80% per il train, 20% per il test
split_index = int(len(trend) * split_ratio)
train = trend.iloc[:split_index]
test = trend.iloc[split_index:]

# Modello ARIMA con parametri scelti manualmente (esempio: (3, 2, 5))
# I parametri possono essere modificati in base all'analisi ACF/PACF
model = statsmodels.api.tsa.ARIMA(train,order=(2,0,1))
results = model.fit()

# Sommario del modello
print("\nSommario del modello ARIMA:")
print(results.summary())

# Previsioni sui dati di test
start = len(train)
end = len(train) + len(test) - 1
predictions = results.predict(start=start, end=end, dynamic=False, typ='levels')

# Visualizzazione delle previsioni
#define size
plt.figure(figsize=(24,10))
#add axes labels and a title
plt.ylabel('Values', fontsize=14)
plt.xlabel('Time', fontsize=14)
plt.title('Values over time', fontsize=16)
plt.plot(train,"-", label = 'train')
plt.plot(test,"-", label = 'test')
plt.plot(predictions,"--", label = 'pred')
#add legend
plt.legend(title='Series')
plt.show()

# Calcolo degli errori
mse_error = mean_squared_error(test, predictions)
rmse_error = rmse(test, predictions)

print(f"\nMean Squared Error: {mse_error}")
print(f"Root Mean Squared Error: {rmse_error}")

print(trend.describe())

# Previsioni future per il periodo di osservazione
model = statsmodels.api.tsa.ARIMA(trend,order=(2,0,1))
results = model.fit()
fcast = results.predict(len(trend),len(trend)+72,typ='levels')

# Visualizzazione delle previsioni future
#define size
plt.figure(figsize=(24,10))
#add axes labels and a title
plt.ylabel('Values', fontsize=14)
plt.xlabel('Time', fontsize=14)
plt.title('Values over time', fontsize=16)
plt.plot(trend.iloc[-250:],"-", label = 'test')
plt.plot(fcast,"--", label = 'fcast')
#add legend
plt.legend(title='Series')
plt.show()
