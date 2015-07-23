#!/usr/bin/ruby
ARGV.each do |arg|
    contents = File.readlines("#{arg}")
    contents.sort! { |a, b| a.split(",").first.split("_").last.to_i <=> b.split(",").first.split("_").last.to_i }
    File.open(arg.split(".").first + ".sort", "w+") { |f| contents.each { |e| f.puts(e) } }
end