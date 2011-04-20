#!/usr/bin/env ruby

## Script that spawns multiple BenchmarkingChatClients to use in benchmarking. ##

#Spawn Clients
client_path = "edu/berkeley/cs/cs162/"
client_filename = "BenchmarkingChatClient"

for i in 1..1
    `ruby generate_commands.rb #{i}`
     command_file = File.open("commands/command#{i}", "r")
     command_string = command_file.read
    #`java #{client_path}#{client_filename} << #{command_string}`
    `cat << commands/command#{i}`
end
