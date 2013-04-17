require 'java'
require 'pry'
require_relative "../model/target/model-1.0.0-standalone.jar"

import "backtype.hadoop.pail.Pail"
import "backtype.hadoop.pail.PailSpec"
import "model.JsonPailStructure"

pail_spec = PailSpec.new("SequenceFile", {}, JsonPailStructure.new)


binding.pry