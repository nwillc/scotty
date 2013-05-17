#!/bin/sh

TEMPLATE=$1
CONTEXT=""
if [ ".$2" != "." ]; then
    CONTEXT="-c $2"
fi

case "$TEMPLATE" in
        print)
          java -jar ../target/scotty-1.0-SNAPSHOT-jar-with-dependencies.jar --print -d database/application.xml,database/environment.xml,database/host.xml,database/amazon.xml,database/datasource.xml
          ;;
        alerts)
          java -jar ../target/scotty-1.0-SNAPSHOT-jar-with-dependencies.jar $CONTEXT -d database/application.xml -t template/alerts.scotty
          ;;
        argopub)
          java -jar ../target/scotty-1.0-SNAPSHOT-jar-with-dependencies.jar $CONTEXT -d database/application.xml,database/environment.xml,database/datasource.xml -t template/argopub.scotty
          ;;
        argosub)
          java -jar ../target/scotty-1.0-SNAPSHOT-jar-with-dependencies.jar $CONTEXT -d database/application.xml,database/environment.xml,database/datasource.xml -t template/argosub.scotty
          ;;
        aegir)
          java -jar ../target/scotty-1.0-SNAPSHOT-jar-with-dependencies.jar $CONTEXT -d database/application.xml,database/environment.xml -t template/aegir.scotty
          ;;
        amazon)
          java -jar ../target/scotty-1.0-SNAPSHOT-jar-with-dependencies.jar $CONTEXT -d database/amazon.xml -t template/amazon.scotty
          ;;
        bulkdata)
          java -jar ../target/scotty-1.0-SNAPSHOT-jar-with-dependencies.jar $CONTEXT -d database/application.xml,database/environment.xml,database/datasource.xml -t template/bulkdata.scotty
          ;;
        universe)
          java -jar ../target/scotty-1.0-SNAPSHOT-jar-with-dependencies.jar $CONTEXT -d database/application.xml,database/host.xml,database/environment.xml -t template/universe.scotty
          ;;
esac

