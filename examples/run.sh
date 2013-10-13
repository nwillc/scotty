#!/bin/sh
SCRIPT_DIR=$(cd $(dirname ${0}) && pwd)
cd ${SCRIPT_DIR}

JAR='../target/scotty-*-dependencies.jar'
if [ ! -f ${JAR} ]; then
    echo Building...
	(cd .. ; mvn clean install -Dmaven.test.skip assembly:single > /dev/null)
	if [ ! -f ${JAR} ]; then
		echo Can not find ${JAR}
		exit 1
	fi
fi


TEMPLATE=${1}
CONTEXT=""
if [ ".${2}" != "." ]; then
    CONTEXT="-c ${2}"
fi
DATABASES=$( ls database/* | tr "\n" "," )

case "${TEMPLATE}" in
        print)
          java -Djava.awt.headless=true -jar ${JAR} --print -d ${DATABASES}
          ;;
        javascript)
          java -Djava.awt.headless=true -jar ${JAR} ${CONTEXT} -d ${DATABASES} -l javascript -t templates/${TEMPLATE}.scotty
          ;;
        *)
          if [ ! -f templates/${TEMPLATE}.scotty ]; then
             echo Unknown template ${TEMPLATE}
             exit 2
          fi
          java -Djava.awt.headless=true -jar ${JAR} ${CONTEXT} -d ${DATABASES} -t templates/${TEMPLATE}.scotty
          ;;
esac

