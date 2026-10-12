#!/bin/sh
cd "$(dirname "$0")" || exit 1
if [ ! -f biblioteca-escolar.jar ]; then
    echo "No se encontro biblioteca-escolar.jar en esta carpeta."
    echo "Genera el ejecutable con: mvn clean package  y copia target/biblioteca-escolar.jar aqui."
    exit 1
fi
java -jar biblioteca-escolar.jar
