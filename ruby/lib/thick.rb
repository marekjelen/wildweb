require 'rack'
require 'java'

$: << File.expand_path('..', __FILE__)

Dir[File.expand_path('../jar/*', __FILE__)].each { |path| require(path) }

require 'thick/version'
require 'thick/server'

require 'rack/handler/thick'