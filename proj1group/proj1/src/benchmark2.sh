#!/bin/bash
cd ~/162/proj1group/proj1/src

TOTAL=80
START=1
FINISH=40

ruby setup_commands2.rb $TOTAL $START $FINISH

for i in $(seq $START $FINISH)
do
java edu/berkeley/cs/cs162/BenchmarkingChatClient < commands/command$i >screenoutput$i 2> client$i &
done

#sleep 300
#killall -9 java
#cat client* > results/superclient
#rm client* screenoutput*