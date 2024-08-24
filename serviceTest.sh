#!/usr/bin/env bash
clear

echo "starting server in new process"
gradle runServerWithMockFinhubPreload &
serverPid=$!
echo "server pid is $serverPid"

echo "waiting for server to come up"
sleep 5

echo "starting server test"
gradle executeServerIntegrationTest

echo "stopping server"
kill -9 $serverPid