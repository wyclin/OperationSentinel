#!/bin/bash
cd ~/162/proj1group/proj1/src
ruby setup_commands.rb 100 51 100
for i in {1..50}
do
java edu/berkeley/cs/cs162/BenchmarkingChatClient < commands/command$i >screenouput$i 2> client$i &
done