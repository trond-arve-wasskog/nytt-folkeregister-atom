#!/bin/bash
echo "Noen av kommandoene kjører med sudo - så du må kanskje oppgi passord :)"
sudo mkdir -v -p /etc/atomhopper
sudo cp -v conf/*.xml /etc/atomhopper/
sudo mkdir -v -m 777 -p /var/log/atomhopper
mvn jetty:run-war
