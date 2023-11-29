#!/bin/bash
a=$(ps -a | grep "npm start")
echo ${a}
while true
do
	while [ "$(ps -a | grep "node")" != "" ]
	do
		echo "$(ps -a | grep "node")"
		sleep 5
	done
	date >> crashes.txt
	node nf_check/nf_check.js
	screen -dm bash -c "npm start"
	sleep 5
done
