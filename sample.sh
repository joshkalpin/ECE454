#!/bin/bash
ant -Dhostname=localhost -Dpport=18726 -Dmport=8726 -Dncores=8 -Dseeds=localhost:8726,localhost:8727 fe &
ant -Dhostname=localhost -Dpport=18727 -Dmport=8727 -Dncores=8 -Dseeds=localhost:8726,localhost:8727 fe &
sleep 5
ant -Dhostname=localhost -Dpport=18728 -Dmport=8728 -Dncores=2 -Dseeds=localhost:8726,localhost:8727 be &
ant -Dhostname=localhost -Dpport=18729 -Dmport=8729 -Dncores=4 -Dseeds=localhost:8726,localhost:8727 be &
sleep 5
ant -Dhostname=localhost -Dpport=18730 -Dmport=8730 -Dncores=8 -Dseeds=localhost:8726,localhost:8727 fe &
sleep 5
ant -Dhostname=localhost -Dpport=18731 -Dmport=8731 -Dncores=8 -Dseeds=localhost:8726,localhost:8727 fe &
sleep 3
ant -Dhostname=localhost -Dpport=18732 -Dmport=8732 -Dncores=8 -Dseeds=localhost:8726,localhost:8727 be &