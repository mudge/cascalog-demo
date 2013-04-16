require 'java'
require 'twitter'

SLEEP_TIME = 1000 * 500

Twitter.configure do |config|
  config.consumer_key = ENV["TWITTER_CONSUMER_KEY"]
  config.consumer_secret = ENV["TWITTER_CONSUMER_SECRET"]
  config.oauth_token = ENV["TWITTER_OAUTH_TOKEN"]
  config.oauth_token_secret = ENV["TWITTER_OAUTH_SECRET"]
end

def output_friends_data(screen_name)
  java.lang.Thread.sleep SLEEP_TIME
  Twitter.friends(screen_name).each_slice(100) do |users|
    users.each do |user|
      puts "#{screen_name} #{user.screen_name}"
    end
    java.lang.Thread.sleep SLEEP_TIME
  end
end

user = ARGV.first
output_friends_data user
