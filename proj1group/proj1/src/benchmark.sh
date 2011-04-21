#!/bin/bash
cd ~/162/proj1group/proj1/src
ruby setup_commands.rb 10 1 10
for i in {1..10}
do
java edu/berkeley/cs/cs162/BenchmarkingChatClient < commands/command$i >screenoutput$i 2> client$i &
done

sleep 300
killall -9 java
cat client* > results/superclient
rm client* screenoutput*