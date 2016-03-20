require File.expand_path('../../../thick', __FILE__)

module Rack
  module Handler

    class Thick

      def self.run(app, options={})
        options[:application] = app
        server = ::Thick::Server.create(options)
      end

    end

    def self.valid_options
    end

    register 'thick', 'Rack::Handler::Thick'

  end
end