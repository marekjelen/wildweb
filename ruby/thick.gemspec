lib = File.expand_path('../lib/', __FILE__)
$: << lib unless $:.include?(lib)

require 'thick/version'

Gem::Specification.new do |s|

  s.name        = 'thick'
  s.version     = Thick::VERSION
  s.platform    = 'java'
  s.authors     = ['Marek Jelen']
  s.email       = ['marek@jelen.biz']
  s.homepage    = 'http://github.com/marekjelen/wildweb'
  s.summary     = 'Very lightweight web server for JRuby'
  s.description = 'Very lightweight web server for JRuby based on Netty library.'
  s.license     = 'MIT'

  s.files        = Dir.glob('{bin,lib}/**/*') + %w(LICENSE README.md)
  s.executables  = ['thick']
  s.require_path = 'lib'

  s.add_dependency 'rack', '~> 1.6'
  s.add_development_dependency 'rspec', '~> 2'
  s.add_development_dependency 'rake', '~> 10'
end