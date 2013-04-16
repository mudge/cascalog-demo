require 'java'
require_relative "../model/target/model-1.0.0-standalone.jar"
require 'nokogiri'
require 'typhoeus'
require 'uri'

import "backtype.hadoop.pail.Pail"
import "backtype.hadoop.pail.PailSpec"
import "model.JsonPailStructure"

def resolve_url(url)
  resp = Typhoeus.get(url, followlocation: true)
  resp.effective_url
end

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
      links: external_links,
      resolved_links: external_links.map {|link| resolve_url(link) }
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

def process_user(screen_name)
  doc = get_doc(screen_name)
  tweets = get_tweets(doc, screen_name)
  user = get_user_data(doc, screen_name)

  puts tweets.inspect
  puts user.inspect
end

process_user("mudge")
