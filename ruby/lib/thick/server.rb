require 'stringio'

module Thick

  class RackAdapter < Java::CzWildwebRuby.RackAdapter
  end

  class Websocket

    def initialize(request, response)
      @request = request
      @response = response
    end

    def websocket?
      @request.websocket.valid
    end

    def accept
      @request.websocket.accept
    end

    def opened(&callback)
      @request.websocket.opened(callback)
    end

    def message(&callback)
      @request.websocket.message(callback)
    end

    def closed(&callback)
      @request.websocket.closed(callback)
    end

  end

  class Hijack

    def initialize(request, response)
      @request = request
      @response = response
      @hijacked = false
    end

    def hijacked?
      @hijacked
    end

    def call
      self
    end

    def read
    end

    def write
    end

    def read_nonblock
    end

    def write_nonblock
    end

    def flush
    end

    def close
    end

    def close_read
    end

    def close_write
    end

    def closed?
    end

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
      hash['rack.input'] = StringIO.new(hash['rack.input'].force_encoding('ascii-8bit'))
      hash['rack.errors'] = $stderr
      hash['wildweb.websocket'] = Websocket.new(env['wildweb.request'], env['wildweb.response'])
      hash['rack.hijack'] = Hijack.new(env['wildweb.request'], env['wildweb.response'])
      hash['rack.hijack_io'] = hash['rack.hijack']
      puts hash.inspect
      status, headers, content = @application.call(hash)
      env['wildweb.response'].status(status)
      headers.each_pair { |k,v| env['wildweb.response'].header(k.to_s, v.to_s) }
      content.each { |data| env['wildweb.response'].write(data.to_s) }
      env['wildweb.response'].close
    end

  end

end