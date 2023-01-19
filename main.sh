#!/bin/sh
set -e

case $APP in

  front)
    exec java -jar front-app.jar
    ;;

  back)
    exec java -jar back-app.jar
    ;;

  client)
    exec java -jar client.jar
    ;;

  calculator)
    exec java -jar calculator.jar
    ;;

  *)
     echo "Invalid 'APP' variable - '$APP'" ; exit -1
    ;;
esac
