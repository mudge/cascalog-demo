# Cascalog Demo

## Getting Started

You'll need [Leiningen](https://github.com/technomancy/leiningen) in order to
use these examples. Luckily, it is easily installed via
[Homebrew](http://mxcl.github.io/homebrew/):

```console
$ brew install leiningen
```

If you want to scrape your own data, you will need [JRuby](http://jruby.org/)
and a [Twitter
developer account](https://dev.twitter.com/docs/auth/tokens-devtwittercom).

First, compile and install the `model` JAR like so:

```console
$ cd model
$ lein install
```

Then you can start experimenting with the `processor`:

```console
$ cd ../processor
$ lein repl
```

## Downloading Your Own Twitter Data

The `fetcher` application consists of two JRuby scripts to first download
follower information for a given Twitter username and then scrape tweets for
those users.

You must first have valid Twitter credentials from `dev.twitter.com`, namely:

* A consumer key;
* A consumer secret;
* An OAuth token;
* An OAuth secret.

Set these in your environment like so:

```console
$ export TWITTER_CONSUMER_KEY=foobar TWITTER_CONSUMER_SECRET=foobar TWITTER_OAUTH_TOKEN=foobar TWITTER_OAUTH_SECRET=foobar
```

Then, using JRuby and [Bundler](http://gembundler.com/), install any required
dependencies:

```console
$ cd fetcher
$ bundle install
```

Then you can download the follower information for a given user:

```console
$ bundle exec ruby get_friends.rb some_user > data/some_user
```

Then you can store the associated tweets for that user in your Pail like so:

```console
$ bundle exec ruby scrape.rb data/some_user
```
