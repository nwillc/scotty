#!/bin/sh

case "$1" in
        amazon)
          java -jar ../target/scotty-1.0-SNAPSHOT-jar-with-dependencies.jar -c env=qa -d database/amazon.xml -t template/amazon.scotty -o amazon.conf
          ;;
        universe)
          java -jar ../target/scotty-1.0-SNAPSHOT-jar-with-dependencies.jar -d database/host.xml -t template/universe.scotty -o universe.properties
          ;;
esac

