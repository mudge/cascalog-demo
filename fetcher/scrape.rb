require 'java'
require_relative "../model/target/model-1.0.0-standalone.jar"
require 'nokogiri'
require 'typhoeus'
require 'uri'
require 'set'

import "backtype.hadoop.pail.Pail"
import "backtype.hadoop.pail.PailSpec"
import "model.JsonPailStructure"

USER_PAIL_ROOT   = File.expand_path("../pails/users")
FOLLOW_PAIL_ROOT = File.expand_path("../pails/follows")
TWEET_PAIL_ROOT  = File.expand_path("../pails/tweets")

def get_tweets(doc, screen_name)
  doc.css('.tweet').map do |tweet_node|
    node = tweet_node.css('p.js-tweet-text')

    links = node.xpath('a/@href').map(&:value)
    external_links = links.select do |link|
      uri = URI(link)
      uri.host && uri.host != "twitter.com"
    end

    {
      id: tweet_node.attributes["data-tweet-id"].value.to_i,
      user_id: screen_name,
      text: node.inner_text,
      links: external_links
    }
  end
end

def get_user_data(doc, screen_name)
  {
    id: screen_name,
    name: doc.css('.fullname .profile-field').inner_text,
    description: doc.css('.bio-container .bio.profile-field').inner_text
  }
end

def get_doc(screen_name)
  response = Typhoeus.get "https://twitter.com/#{screen_name}"
  Nokogiri::HTML(response.body)
end

def get_or_create_json_pail(pail_root)
  Pail.new(pail_root)
rescue
  pail_spec = PailSpec.new("SequenceFile", {}, JsonPailStructure.new)
  Pail.create(pail_root, pail_spec)
end

def save(pail_root, records)
  pail = get_or_create_json_pail(pail_root)
  writer = pail.openWrite
  records.each do |record|
    writer.writeObject record
  end
  writer.close
end

def process_user(screen_name)
  puts "Srcaping data for #{screen_name}"
  doc = get_doc(screen_name)
  tweets = get_tweets(doc, screen_name)
  user = get_user_data(doc, screen_name)

  puts tweets.inspect

  puts "Saving user & #{tweets.count} tweets for #{screen_name}"
  puts tweets
  save(USER_PAIL_ROOT, [user])
  save(TWEET_PAIL_ROOT, tweets)
end


## script ##

follows_file = ARGV.first
follows_data = File.readlines(follows_file)

all_users = Set.new
follows_data.each do |line|
  user1, user2 = line.split(" ")
  all_users << user1
  all_users << user2

  puts "Saving follow data: #{user1} => #{user2}"
  save(FOLLOW_PAIL_ROOT, [{
    user_id: user1,
    target_id: user2
  }])
end

all_users.each_with_index do |user, i|
  puts "Processing user #{i + 1} of #{all_users.size}"
  process_user(user)
end

puts "Consolidating pails"
[USER_PAIL_ROOT, FOLLOW_PAIL_ROOT, TWEET_PAIL_ROOT].each do |root|
  pail = Pail.new(root)
  pail.consolidate
end