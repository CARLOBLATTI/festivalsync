import requests
import pandas as pd
import numpy as np  # Importa NumPy per gestire valori infiniti

# Configura l'endpoint Prometheus
PROMETHEUS_URL = "http://localhost:9090/api/v1/query_range"
QUERY = "rate(http_server_requests_seconds_sum[5m]) / rate(http_server_requests_seconds_count[5m])"
START_TIME = "2024-12-01T00:00:00Z"
END_TIME = "2024-12-04T23:59:59Z"
STEP = "60s"

def fetch_prometheus_data():
    """Esegue una richiesta a Prometheus per ottenere i dati."""
    params = {
        "query": QUERY,
        "start": START_TIME,
        "end": END_TIME,
        "step": STEP,
    }
    try:
        response = requests.get(PROMETHEUS_URL, params=params)
        response.raise_for_status()
        data = response.json()
        return data
    except requests.exceptions.RequestException as e:
        print(f"Errore nella richiesta a Prometheus: {e}")
        return None

def parse_data(raw_data):
    """Analizza i dati restituiti da Prometheus e li converte in un DataFrame."""
    if not raw_data or raw_data.get("status") != "success":
        print("Dati non validi o errore nella risposta.")
        return None

    results = raw_data.get("data", {}).get("result", [])
    all_data = []

    for result in results:
        values = result.get("values", [])
        for timestamp, value in values:
            try:
                # Converti valore in float e gestisci NaN/Infinity
                cleaned_value = float(value) if value not in ["NaN", "+Inf", "-Inf"] else None
                all_data.append({
                    "timestamp": int(timestamp),
                    "value": cleaned_value,
                })
            except ValueError:
                print(f"Errore nel parsing del valore: {value} al timestamp {timestamp}")

    # Crea un DataFrame
    df = pd.DataFrame(all_data)
    if not df.empty:
        df['timestamp'] = pd.to_datetime(df['timestamp'], unit='s')
    return df

def clean_data(df):
    """Rimuove righe con valori nulli, infiniti, bianchi o pari a zero."""
    if df is not None and not df.empty:
        # Rimuove righe con valori nulli
        df_cleaned = df.dropna(subset=["value"])

        # Rimuove valori infiniti
        df_cleaned = df_cleaned[df_cleaned["value"].apply(lambda x: not np.isinf(x))]

        # Rimuove valori bianchi o spazi
        df_cleaned = df_cleaned[df_cleaned["value"].apply(lambda x: str(x).strip() != "")]

        # Rimuove valori pari a 0 o 0.0
        df_cleaned = df_cleaned[df_cleaned["value"] != 0]

        return df_cleaned[["timestamp", "value"]]
    return None


def main():
    # Ottieni i dati da Prometheus
    raw_data = fetch_prometheus_data()

    # Analizza e pulisci i dati
    df = parse_data(raw_data)

    # Rimuovi righe non valide
    df_cleaned = clean_data(df)

    if df_cleaned is not None and not df_cleaned.empty:
        print("Dati ottenuti e puliti con successo!")
        print(df_cleaned.head())  # Mostra le prime righe

        # Formatta il timestamp nel formato richiesto
        df_cleaned['timestamp'] = df_cleaned['timestamp'].dt.strftime('%d/%m/%y %H:%M:%S')

        # Rinominare le colonne per corrispondere al file fornito
        df_cleaned.rename(columns={'timestamp': 'Time', 'value': 'Value'}, inplace=True)

        # Salva i dati in un file CSV
        output_file = "prometheus_data_latency.csv"
        df_cleaned.to_csv(output_file, index=False)
        print(f"Dati puliti salvati in {output_file}")
    else:
        print("Nessun dato valido disponibile o errore durante l'elaborazione.")

if __name__ == "__main__":
    main()
