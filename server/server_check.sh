#!/bin/bash
while true
do
	while [ "$(ps -a | grep "node")" != "" ]
	do
		echo "$(ps -a | grep "node")"
		sleep 5
	done
	date >> public/crashes.txt
	screen -dm bash -c "npm start"
	sleep 5
done
