#!/usr/bin/env ruby

## Script that spawns multiple BenchmarkingChatClients to use in benchmarking. ##

#Spawn Clients
client_path = "edu/berkeley/cs/cs162/"
client_filename = "BenchmarkingChatClient"
n=ARGV[0].to_i
for i in 1..n
  `ruby generate_commands.rb #{i} #{n}`
     command_file = File.open("commands/command#{i}", "r")
     command_string = command_file.read
<<<<<<< HEAD
  #`java #{client_path}#{client_filename} << #{command_string} >/dev/null 2> client#{i} &`
  #`cat << commands/command#{i}`
=======
    `java #{client_path}#{client_filename} << #{command_string}`
    #`cat << commands/command#{i}`
>>>>>>> a05c77b79aaba2e00464373a05e2a789046fd56b
end
