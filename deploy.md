deploy on search.tipitaka.org
==========

copy the newly build package for jetty6
----------

     mvn clean
     mvn -P package6
     scp target/tipitaka.war search@saumya.de:
     
restart the server
----------

     ssh root@saumya.de
     /etc/init.d/hightide stop
     /etc/init.d/hightide start
     
sync the search index
---------

     rsync -avz --exclude=tipitaka -e ssh solr/ search@saumya.de:

sync the search index, the mirror and the directory structure files
---------

     rsync -avz -e ssh solr/ search@saumya.de:
