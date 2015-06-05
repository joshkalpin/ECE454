#!/bin/bash
ant -Dhostname=localhost -Dpport=18726 -Dmport=8726 -Dncores=8 -Dseeds=localhost:8726 fe &
sleep 3
ant -Dhostname=localhost -Dpport=18730 -Dmport=8730 -Dncores=8 -Dseeds=localhost:8726 fe &
sleep 3
ant -Dhostname=localhost -Dpport=18728 -Dmport=8728 -Dncores=2 -Dseeds=localhost:8726 be &
ant -Dhostname=localhost -Dpport=18729 -Dmport=8729 -Dncores=4 -Dseeds=localhost:8726 be &
