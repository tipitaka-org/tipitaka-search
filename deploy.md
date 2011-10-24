deploy on search.tipitaka.org
==========

copy the newly build package for jetty6
----------

     mvn clean
     mvn -P package6
     scp target/tipitaka6.war search@saumya.de:tipitaka.war
     
restart the server (needed for new tipitaka.war)
----------

     ssh root@saumya.de
     /etc/init.d/hightide stop
     /etc/init.d/hightide start
     
sync velocity files
---------

     rsync -avz --exclude=tipitaka --exclude=data -e ssh solr search@saumya.de:

sync the search index
---------

     rsync -avz --exclude=tipitaka -e ssh solr search@saumya.de:

sync the search index, the mirror and the directory structure files
---------

     rsync -avz -e ssh solr search@saumya.de:
