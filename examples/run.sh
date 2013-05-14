#!/bin/sh

case "$1" in
        argopub)
          java -jar ../target/scotty-1.0-SNAPSHOT-jar-with-dependencies.jar -c $2 -d database/application.xml,database/environment.xml -t template/argopub.scotty -o argopub.properties
          ;;
        alerts)
          java -jar ../target/scotty-1.0-SNAPSHOT-jar-with-dependencies.jar -c $2 -d database/application.xml -t template/alerts.scotty -o alerts.json
          ;;
        aegir)
          java -jar ../target/scotty-1.0-SNAPSHOT-jar-with-dependencies.jar -c $2 -d database/application.xml,database/environment.xml -t template/aegir.scotty -o aegir.properties
          ;;
        amazon)
          java -jar ../target/scotty-1.0-SNAPSHOT-jar-with-dependencies.jar -c $2 -d database/amazon.xml -t template/amazon.scotty -o amazon.conf
          ;;
        universe)
          java -jar ../target/scotty-1.0-SNAPSHOT-jar-with-dependencies.jar -d database/host.xml -t template/universe.scotty -o universe.properties
          ;;
esac

