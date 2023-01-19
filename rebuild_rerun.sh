#!/bin/bash

docker-compose down \
&& ./gradlew clean build \
&& docker-compose build --no-cache \
&& docker-compose up -d
