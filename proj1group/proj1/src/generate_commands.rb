#! /usr/bin/env ruby

## Generates commands for a BenchmarkingChatClient. Writes to a file in commands/ ##
#
string_id = ARGV[0]
value = ARGV[1].to_i
command_string = "connect ec2-204-236-192-255.compute-1.amazonaws.com:4747\n"
command_string += "adduser user#{string_id} password\n"
command_string += "login user#{string_id} password\n"
command_string += "sleep 20000\n"
50.times { |i| command_string += "send user#{(string_id.to_i)%value + 1} #{i+1} \"hello user#{(string_id.to_i)%value + 1}\"\n" } 
command_string += "print RTT\n" #ADD COMMAND STRING HERE

File.open("commands/command#{string_id}", 'w') do |command_file|
	command_file.puts(command_string)
end
