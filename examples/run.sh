#!/bin/sh
SCRIPT_DIR=$(cd $(dirname $0) > /dev/null && pwd)
cd ${SCRIPT_DIR}

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
        *)
          if [ ! -f template/${TEMPLATE}.scotty ]; then
             echo Unknown template ${TEMPLATE}
             exit 2
          fi
          java -jar ../target/scotty-1.0-SNAPSHOT-jar-with-dependencies.jar ${CONTEXT} -d ${DATABASES} -t template/${TEMPLATE}.scotty
          ;;
esac

