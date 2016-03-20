require 'stringio'

module Thick

  class RackAdapter < Java::CzWildwebRuby.RackAdapter

  end

  class Server

    def self.create(options = {})
      options = {
          :address => '0.0.0.0',
          :port => 9292,
          :environment => 'development',
          :directory => Dir.getwd,
          :file => 'config.ru'
      }.merge(options)

      Thick::Server.new(options)
    end

    def initialize(options = {})
      @options = options
      ENV['RACK_ENV'] ||= ENV['RAILS_ENV'] ||= @options[:environment]
      if @options[:application]
        @application = @options[:application]
      else
        @application ||= Rack::Builder.parse_file(File.expand_path(@options[:file], @options[:directory]))[0]
      end

      @application = Rack::Lint.new(@application)

      @adapter = RackAdapter.new(@options[:address], @options[:port], '', false, JRuby.runtime, self)
      @server = Java::CzWildwebServer::HttpServerImpl.new
      @server.register('/', @adapter)
      @server.register('/*', @adapter)
      @server.start(@options[:address], @options[:port])
    end

    def call(env)
      hash = {}
      env.each_pair { |k,v| hash[k] = v }
      hash['rack.version'] = [1,3]
      hash['rack.input'] = StringIO.new(hash['rack.input'].force_encoding("ascii-8bit"))
      hash['rack.errors'] = $stderr
      puts hash.inspect
      status, headers, content = @application.call(hash)
      env['wildweb.response'].status(status)
      headers.each_pair { |k,v| env['wildweb.response'].header(k.to_s, v.to_s) }
      content.each { |data| env['wildweb.response'].write(data.to_s) }
      env['wildweb.response'].close
    end

  end

end