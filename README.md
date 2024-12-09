# festivalsync
Architettura basata su 3 microservizi con distribuzione Docker e Kubernetes,
monitoraggio implementato tramite Prometheus e comunicazione broker-based con
l'ausilio di Kafka.

Progetto Spring
Java 17

## configurazioni run (maven)
- Intellij: /.run

### Dipendenze e Installazioni
1. Java Development Kit (JDK) 17
   Su Windows e Mac: Scaricare da Oracle JDK o OpenJDK.
2. Maven
   Scaricare Apache Maven e aggiungerlo al PATH del sistema.
3. Docker e Docker Compose
   Scaricare e installare Docker Desktop da docker.com.
   Verificare l'installazione con docker --version e docker-compose --version.
4. Kubernetes e Minikube
   Minikube: Per eseguire Kubernetes in locale, scaricare Minikube da Minikube.
   kubectl: Scaricare kubectl per gestire i cluster Kubernetes. Verificare l’installazione con kubectl version.
5. Apache Kafka
   Da Docker.
6. MySQL o MariaDB
   Installare MySQL localmente, oppure eseguirlo in un container Docker con docker run -d -p 3306:3306 --name mysql -e MYSQL_ROOT_PASSWORD=root mysql.
7. Prometheus e Grafana
   Possono essere eseguiti entrambi tramite Docker per semplificare l’installazione (docker run per Prometheus e Grafana).
8. IDE per lo sviluppo (IntelliJ IDEA o VS Code)
9. Postman o Insomnia
   Strumento per testare le API REST.
10. Helm 
    Scaricare da helm.sh.

Il progetto è costituito da 3 microservizi che sono realizzati come moduli in Spring
a partire da un modulo padre

### Build
Lanciare i comandi maven clean install prima sul progetto padre e poi sui singoli
microservizi
Lanciare i comandi maven clean install anche sul modulo apigateway in cui vi è
la configurazione per l'apigateway che viene utilizzato sulla distribuzione Docker

## Configurazioni
Le dipendenze dei microservizi vengono risolte tramite il file pom.xml
Le configurazioni interne degli stessi sono disponibili nei relativi file application.properties
Le configurazioni per kubernetes sono disponibile sotto al path: festivalsync/K8s
Le configurazioni docker sono contenute nei docker file dei singoli micorservizi
e nel compose.yml

### run Docker
accedere da terminale alla root di progetto e lanciare il comando:
da terminale: docker-compose up -d

### Controllare i contenitori attivi con:
da terminale: docker ps
I microservizi dovrebbero andare in stop dopo poco tempo perchè manca il database

### Verifica i log di Kafka o Zookeeper (opzionale):
da terminale: docker-compose logs kafka
da terminale: docker-compose logs zookeeper

### Creazione database su Docker
Se il container mariadb è in running ci si deve collegare al database
all'host name 127.0.0.1 alla porta 3360 ad esempio utilizzando MySqlWorkbench
e creare un database chiamato festivalsyncDB, poi utilizzare il file sql
presente al path: src/main/resources/festivalsyncDB.sql per creare le tabelle di 
interesse

### Rilascio nuovi update
Per rilasciare eventuali modifiche, a seguito della creazione dei nuovi pacchetti,
lanciare il comando da terminale: docker-compose up -d --build

Se tutti i container sono up and running i microservizi sono raggiungibili
in localhost alle porte 8081 80821 e 8083 o tramite apigateway alla porta 8080

#### run Kubernetes
Per distribuire i microservizi su kubernetes lanciare i seguenti comandi da terminale:

minikube start --driver=docker

kubectl config set-context --current --namespace=festivalsync

kubectl apply -f k8s/config/db-secret.yaml
kubectl apply -f k8s/config/configmap.yaml

kubectl apply -f k8s/storage/mariadb-pv.yaml
kubectl apply -f k8s/storage/mariadb-pvc.yaml

kubectl apply -f k8s/database/mariadb-deployment.yaml
kubectl apply -f k8s/database/mariadb-service.yaml

kubectl apply -f k8s/services/artist-service.yaml
kubectl apply -f k8s/services/event-service.yaml
kubectl apply -f k8s/services/ticket-service.yaml

kubectl apply -f k8s/ingress/ingress.yaml

kubectl apply -f k8s/kafka/zookeeper-deployment.yaml
kubectl apply -f k8s/kafka/zookeeper-service.yaml
kubectl apply -f k8s/kafka/kafka-deployment.yaml
kubectl apply -f k8s/kafka/kafka-service.yaml

#### Database
lanciare da terminale il comando per aprire la connessione verso l'esternO:
kubectl port-forward svc/mariadb-service 3307:3306 -n festivalsync
Poi collegarsi al database all'host name 127.0.0.1 alla porta 3307
(ad esempio utilizzando MySqlWorkbench) e creare un database 
chiamato festivalsyncDB, poi utilizzare il file sql presente 
al path: src/main/resources/festivalsyncDB.sql per creare le tabelle di interesse

minikube docker-env | Invoke-Expression

docker build -t artist-service:latest ./artist-service
docker build -t event-service:latest ./event-service
docker build -t ticket-service:latest ./ticket-service

#### per riavviare i pod
kubectl delete pod -l app=artist-service -n festivalsync
kubectl delete pod -l app=event-service -n festivalsync
kubectl delete pod -l app=ticket-service -n festivalsync

#### Per testare in locale serve aprire un tunnel
minikube tunnel
kubectl get svc -n ingress-nginx //verifica ip
Poi si possono richiamare la API mediante l'ingress all' host
http://unict.festivalsync.it/ seguendo i base path che possono essere
trovati nel file ingress.yaml

#### Monitoraggio
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm install prometheus prometheus-community/kube-prometheus-stack
kubectl port-forward deployment/prometheus-grafana 3001
kubectl port-forward pod/prometheus-prometheus-kube-prometheus-prometheus-0 9090

#### grafana su kubernetes
kubectl port-forward -n festivalsync svc/prometheus-grafana 3001:80
poi collegarsi in localhost, potrebbe essere necessario cambiare la password di accesso

##### Predittore 
Installare le librerie necessarie pandas, matplotlib, requests, pmsarima, numpy 
con pip install.
poi lanciare con comando python gli script per collezionare i dati relativi a latenza e CPU
e poi gli script per il predittore ARIMA,gli script sono sotto alla cartella monitoring.


