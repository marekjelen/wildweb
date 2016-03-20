#!/usr/bin/env bash


rm lib/jar/*.jar

cd ..

mvn clean package

mvn dependency:copy-dependencies -DoutputDirectory="\${project.build.directory}/../ruby/lib/jar" -DincludeScope=compile

cd ruby

rm lib/jar/jruby-complete-*
rm lib/jar/org.osgi.core-*

cp ../target/*.jar lib/jar

gem build thick.gemspec

gem install *.gem