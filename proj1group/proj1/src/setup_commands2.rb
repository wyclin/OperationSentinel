#! /usr/bin/env ruby

## Generates commands for a BenchmarkingChatClient. Writes to a file in commands/ ##

num_total_users = ARGV[0].to_i
start_id = ARGV[1].to_i
end_id = ARGV[2].to_i

def generate_command_file(id, num_total_users)
  command_string = "connect ec2-204-236-192-255.compute-1.amazonaws.com:4747\n"
  command_string += "login user#{id} password\n"
  command_string += "sleep 60000\n"
  
  command_string += "print DBT\n"
  
  File.open("commands/command#{id}", 'w') do |command_file|
    command_file.puts(command_string)
  end
end


for id in start_id..end_id
  generate_command_file(id, num_total_users)
end

