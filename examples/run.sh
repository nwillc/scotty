#!/bin/sh

TEMPLATE=$1
CONTEXT=""
if [ ".$2" != "." ]; then
    CONTEXT="-c $2"
fi
DATABASES=database/application.xml,database/host.xml,database/environment.xml,database/datasource.xml,database/amazon.xml

case "$TEMPLATE" in
        print)
          java -jar ../target/scotty-1.0-SNAPSHOT-jar-with-dependencies.jar --print -d ${DATABASES}
          ;;
        alerts|argopub|argosub|aegir|amazon|bulkdata|universe|classifiertraining|clockadjustment|cs|datasifthandler|emailhandler|emailhandler|embargo)
          java -jar ../target/scotty-1.0-SNAPSHOT-jar-with-dependencies.jar ${CONTEXT} -d ${DATABASES} -t template/${TEMPLATE}.scotty
          ;;
        *)
          echo Unknown command ${TEMPLATE}
          ;;
esac

