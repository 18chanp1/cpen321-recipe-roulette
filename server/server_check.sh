#!/bin/bash
a=$(ps -a | grep "npm start")
echo ${a}
while true
do
	while [ "$(ps -a | grep "node")" != "" ]
	do
		echo "$(ps -a | grep "node")"
		echo "We gucci"
		sleep 5
	done
	screen -dm bash -c "npm start"
	sleep 5
done
