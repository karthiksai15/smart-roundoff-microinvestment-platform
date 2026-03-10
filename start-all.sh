#!/bin/bash

ROOT_DIR=$(pwd)
BASE_DIR="/Users/karthiksai"
ML_DIR="$BASE_DIR/ml-fraud-service"

echo "======================================"
echo " STARTING SROMIP PLATFORM "
echo " EVENT-DRIVEN MICROSERVICES MODE "
echo "======================================"

# -------------------------------
# Start Docker Desktop
# -------------------------------
echo "🚀 Starting Docker Desktop..."
open -a Docker

echo "⏳ Waiting for Docker daemon..."
until docker info >/dev/null 2>&1; do
  sleep 3
done

echo "✅ Docker is running"

# -------------------------------
# Start Infrastructure
# -------------------------------
echo "🐳 Starting infrastructure containers..."
docker compose up -d

# -------------------------------
# Wait for PostgreSQL
# -------------------------------
echo "⏳ Waiting for PostgreSQL..."

until docker exec postgres pg_isready -U admin >/dev/null 2>&1; do
  sleep 3
done

echo "✅ PostgreSQL ready"

# -------------------------------
# Wait for Kafka
# -------------------------------
echo "⏳ Waiting for Kafka..."

until docker exec kafka bash -c "nc -z localhost 9092" >/dev/null 2>&1; do
  sleep 3
done

sleep 10
echo "✅ Kafka ready"

# -------------------------------
# Show Existing Kafka Topics
# -------------------------------
echo "📜 Existing Kafka topics..."

docker exec kafka /opt/kafka/bin/kafka-topics.sh \
--bootstrap-server localhost:9092 \
--list

# -------------------------------
# Ensure Kafka Topics
# -------------------------------
echo "📨 Ensuring Kafka topics exist..."

docker exec kafka bash -c "/opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --if-not-exists --topic payment-intent-topic --partitions 1 --replication-factor 1"

docker exec kafka bash -c "/opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --if-not-exists --topic trust-decision-topic --partitions 1 --replication-factor 1"

docker exec kafka bash -c "/opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --if-not-exists --topic payment-topic --partitions 1 --replication-factor 1"

docker exec kafka bash -c "/opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --if-not-exists --topic investment-completed-topic --partitions 1 --replication-factor 1"

docker exec kafka bash -c "/opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --if-not-exists --topic notification-topic --partitions 1 --replication-factor 1"

docker exec kafka bash -c "/opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --if-not-exists --topic otp-request-topic --partitions 1 --replication-factor 1"

docker exec kafka bash -c "/opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --if-not-exists --topic otp-verified-topic --partitions 1 --replication-factor 1"

echo "✅ Kafka topics ready"

# -------------------------------
# Start ML Fraud Model
# -------------------------------
echo "🤖 Starting ML Fraud Service (8000)..."

osascript -e "tell app \"Terminal\" to do script \"cd $ML_DIR && source venv/bin/activate && uvicorn app:app --host 0.0.0.0 --port 8000\""

echo "⏳ Waiting for ML service..."

until curl -s http://localhost:8000/docs >/dev/null; do
  sleep 3
done

echo "✅ ML service ready"

# -------------------------------
# Start Eureka Service Registry
# -------------------------------
echo "🧭 Starting Eureka Service Registry (8761)..."

osascript -e "tell app \"Terminal\" to do script \"cd $ROOT_DIR && ./gradlew :service-registry:bootRun\""

sleep 20

# -------------------------------
# Start API Gateway
# -------------------------------
echo "🌐 Starting API Gateway (9000)..."

osascript -e "tell app \"Terminal\" to do script \"cd $ROOT_DIR && ./gradlew :api-gateway:bootRun\""

sleep 10

# -------------------------------
# Start All Microservices in Parallel
# -------------------------------
echo "🚀 Starting all microservices in parallel..."

osascript -e "tell app \"Terminal\" to do script \"cd $ROOT_DIR && ./gradlew :auth-service:bootRun\""

osascript -e "tell app \"Terminal\" to do script \"cd $ROOT_DIR && ./gradlew :payment-intent-service:bootRun\""

osascript -e "tell app \"Terminal\" to do script \"cd $ROOT_DIR && ./gradlew :fraud-service:bootRun\""

osascript -e "tell app \"Terminal\" to do script \"cd $ROOT_DIR && ./gradlew :payment-service:bootRun\""

osascript -e "tell app \"Terminal\" to do script \"cd $ROOT_DIR && ./gradlew :otp-service:bootRun\""

osascript -e "tell app \"Terminal\" to do script \"cd $ROOT_DIR && ./gradlew :investment-service:bootRun\""

osascript -e "tell app \"Terminal\" to do script \"cd $ROOT_DIR && ./gradlew :notification-service:bootRun\""

osascript -e "tell app \"Terminal\" to do script \"cd $ROOT_DIR && ./gradlew :dashboard-service:bootRun\""

echo ""
echo "======================================"
echo " ALL SERVICES STARTED SUCCESSFULLY 🚀 "
echo "======================================"
echo ""

echo "Service Registry:"
echo "http://localhost:8761"
echo ""

echo "API Gateway:"
echo "http://localhost:9000"
echo ""

echo "ML Fraud API:"
echo "http://localhost:8000/docs"
echo ""

echo "Dashboard:"
echo "http://localhost:8095/dashboard/pipelines"