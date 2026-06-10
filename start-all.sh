#!/bin/bash

ROOT_DIR=$(pwd)
BASE_DIR="/Users/karthiksai"
ML_DIR="$BASE_DIR/ml-fraud-service"

echo "======================================"
echo " STARTING SROMIP PLATFORM "
echo "======================================"

# -------------------------------
# Start Docker
# -------------------------------
echo "🚀 Starting Docker..."
open -a Docker

echo "⏳ Waiting for Docker..."
until docker info >/dev/null 2>&1; do sleep 3; done
echo "✅ Docker ready"

# -------------------------------
# Start Infra
# -------------------------------
echo "🐳 Starting containers..."
docker compose up -d

# -------------------------------
# Wait PostgreSQL
# -------------------------------
echo "⏳ Waiting PostgreSQL..."
until docker exec postgres pg_isready -U admin >/dev/null 2>&1; do sleep 3; done
echo "✅ PostgreSQL ready"

# -------------------------------
# Wait Redis
# -------------------------------
echo "⏳ Waiting Redis..."
until docker exec redis redis-cli ping | grep PONG >/dev/null; do sleep 2; done
echo "✅ Redis ready"
# -------------------------------
# CLEAN OLD TEST DATA
# -------------------------------
echo "🧹 Cleaning old test data..."

docker exec -i postgres psql -U admin -d auth_db -c \
"TRUNCATE TABLE users RESTART IDENTITY CASCADE;" 2>/dev/null

docker exec -i postgres psql -U admin -d payment_intent_db -c \
"TRUNCATE TABLE payment_intents,idempotency_keys RESTART IDENTITY CASCADE;" 2>/dev/null

docker exec -i postgres psql -U admin -d fraud_db -c \
"TRUNCATE TABLE fraud_checks,risk_scores RESTART IDENTITY CASCADE;" 2>/dev/null

docker exec -i postgres psql -U admin -d payment_db -c \
"TRUNCATE TABLE payments,idempotency_keys,user_preferences RESTART IDENTITY CASCADE;" 2>/dev/null

docker exec -i postgres psql -U admin -d payment_db -c \
"TRUNCATE TABLE dlq_events CASCADE;" 2>/dev/null

docker exec -i postgres psql -U admin -d investment_db -c \
"TRUNCATE TABLE investments RESTART IDENTITY CASCADE;" 2>/dev/null

docker exec -i postgres psql -U admin -d notification_db -c \
"TRUNCATE TABLE notifications RESTART IDENTITY CASCADE;" 2>/dev/null

docker exec -i postgres psql -U admin -d dashboard_db -c \
"TRUNCATE TABLE dashboard_transactions RESTART IDENTITY CASCADE;" 2>/dev/null

docker exec -i redis redis-cli FLUSHALL >/dev/null 2>&1

echo "✅ Test data cleaned"
# -------------------------------
# Wait Kafka (FIXED 🔥)
# -------------------------------
echo "⏳ Waiting Kafka..."
until docker exec kafka bash -c "/opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list" >/dev/null 2>&1; do sleep 3; done
echo "✅ Kafka ready"

sleep 5

# -------------------------------
# Create Topics
# -------------------------------
echo "📨 Creating topics..."

create_topic() {
  docker exec kafka bash -c "/opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create --if-not-exists \
  --topic $1 \
  --partitions 1 \
  --replication-factor 1"
}

create_topic payment-topic
create_topic otp-request-topic
create_topic otp-verified-topic
create_topic payment-decision-retry-topic
create_topic investment-completed-topic
create_topic notification-topic
create_topic otp-generated-topic

echo "✅ Topics ready"

# -------------------------------
# Start ML Service
# -------------------------------
echo "🤖 Starting ML..."

osascript -e "tell app \"Terminal\" to do script \"cd $ML_DIR && source venv/bin/activate && uvicorn app:app --host 0.0.0.0 --port 8000\""

until curl -s http://localhost:8000/docs >/dev/null; do sleep 3; done
echo "✅ ML ready"

# -------------------------------
# Start Eureka (FIXED 🔥)
# -------------------------------
echo "🧭 Starting Eureka..."
osascript -e "tell app \"Terminal\" to do script \"cd $ROOT_DIR && ./gradlew :service-registry:bootRun\""

echo "⏳ Waiting Eureka..."
until curl -s http://localhost:8761 >/dev/null; do sleep 3; done
echo "✅ Eureka ready"

# -------------------------------
# Start Services
# -------------------------------
echo "🚀 Starting services..."

SERVICES=(
auth-service
payment-intent-service
fraud-service
payment-service
otp-service
investment-service
notification-service
dashboard-service
)

for SERVICE in "${SERVICES[@]}"
do
  osascript -e "tell app \"Terminal\" to do script \"cd $ROOT_DIR && ./gradlew :$SERVICE:bootRun\""
done

echo "⏳ Waiting services..."
sleep 25

# -------------------------------
# Start Gateway LAST
# -------------------------------
echo "🌐 Starting API Gateway..."
osascript -e "tell app \"Terminal\" to do script \"cd $ROOT_DIR && ./gradlew :api-gateway:bootRun\""

echo ""
echo "======================================"
echo " ALL SERVICES STARTED 🚀 "
echo "======================================"

echo "Service Registry: http://localhost:8761"
echo "API Gateway: http://localhost:9000"
echo "ML Fraud API: http://localhost:8000/docs"
echo "Dashboard: http://localhost:8095/dashboard/pipelines"