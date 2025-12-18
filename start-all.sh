#!/bin/bash

echo "======================================"
echo " STARTING SMART ROUND-OFF PLATFORM "
echo "======================================"

echo "🚀 Starting Docker Desktop..."
open -a Docker

echo "⏳ Waiting for Docker..."
while ! docker info > /dev/null 2>&1; do
  sleep 2
done

echo "✅ Docker is running"

echo "🐳 Starting containers (Postgres, Redis, Mongo)..."
docker compose up -d

echo "⏳ Waiting for databases..."
sleep 8

echo "🧭 Starting Eureka Service Registry (8761)..."
osascript -e 'tell app "Terminal" to do script "cd '$PWD'/service-registry && ./gradlew bootRun"'
sleep 10

echo "🔐 Starting Auth Service (8081)..."
osascript -e 'tell app "Terminal" to do script "cd '$PWD'/auth-service && ./gradlew bootRun"'
sleep 6

echo "💳 Starting Payment Service (8082)..."
osascript -e 'tell app "Terminal" to do script "cd '$PWD'/payment-service && ./gradlew bootRun"'
sleep 4

echo "📊 Starting Investment Service (8083)..."
osascript -e 'tell app "Terminal" to do script "cd '$PWD'/investment-service && ./gradlew bootRun"'
sleep 4

echo "🚨 Starting Fraud Service (8084)..."
osascript -e 'tell app "Terminal" to do script "cd '$PWD'/fraud-service && ./gradlew bootRun"'
sleep 4

echo "🔔 Starting Notification Service (8085)..."
osascript -e 'tell app "Terminal" to do script "cd '$PWD'/notification-service && ./gradlew bootRun"'
sleep 4

echo "🌐 Starting API Gateway (9000)..."
osascript -e 'tell app "Terminal" to do script "cd '$PWD'/api-gateway && ./gradlew bootRun"'

echo "======================================"
echo " ALL SERVICES STARTED 🚀 "
echo "======================================"

