#!/bin/bash
cd ~/162/proj1group/proj1/src
ruby edu/berkeley/cs/cs162/benchmark_script.rb 50
for i in {1..50}
do
java edu/berkeley/cs/cs162/BenchmarkingChatClient < commands/command$i >screenouput$i 2> client$i &
done