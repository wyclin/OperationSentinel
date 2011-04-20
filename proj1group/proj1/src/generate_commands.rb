#! /usr/bin/env ruby

## Generates commands for a BenchmarkingChatClient. Writes to a file in commands/ ##
#
string_id = ARGV[0]

command_string = "" #ADD COMMAND STRING HERE

File.open("commands/command#{string_id}", 'w') do |command_file|
	command_file.puts(command_string)
end
