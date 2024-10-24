#!/usr/bin/env bash
clear

echo "Starting Queue Service in a new process"
gradle queue-service:runService &
queueServicePid=$!
echo "Queue Service PID is $queueServicePid"

sleep 5

echo "Starting Finnhub Service in a new process"
gradle finhub-service:runServerWithMockFinhubPreload &
finnhubServicePid=$!
echo "Finnhub Service PID is $finnhubServicePid"

sleep 5

echo "Starting Monolith Service in a new process"
gradle monolith-service:runService &
monolithServicePid=$!
echo "Monolith Service PID is $monolithServicePid"
sleep 80

echo "Waiting for services to come up"

echo "Starting server integration tests"
gradle executeServerIntegrationTest

sleep 5

echo "Stopping all services"
kill -9 $queueServicePid
kill -9 $finnhubServicePid
kill -9 $monolithServicePid
