#!/bin/sh
SCRIPT_DIR=$(cd $(dirname $0) > /dev/null && pwd)
cd ${SCRIPT_DIR}

JAR='../target/scotty-*-dependencies.jar'
if [ ! -f $JAR ]; then
	(cd .. ; mvn clean install -Dmaven.test.skip assembly:single)
	if [ ! -f $JAR ]; then
		echo Can not find ${JAR}
		exit 1
	fi
fi


TEMPLATE=$1
CONTEXT=""
if [ ".$2" != "." ]; then
    CONTEXT="-c $2"
fi
DATABASES=$( ls database | tr "\n" "," )

case "$TEMPLATE" in
        print)
          java -jar ${JAR} --print -d ${DATABASES}
          ;;
        *)
          if [ ! -f template/${TEMPLATE}.scotty ]; then
             echo Unknown template ${TEMPLATE}
             exit 2
          fi
          java -jar ${JAR} ${CONTEXT} -d ${DATABASES} -t template/${TEMPLATE}.scotty
          ;;
esac

