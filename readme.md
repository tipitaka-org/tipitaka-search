setup and other commands
==========

setup a clean clone
----------

     mvn -P setup -Dscript=romn

which will mirror a mirror of tipitkaka.org for the **romn** script and index them. the mirroring will look at the timestamps of the files and update only the changed ones but that needs to check the timestamp on the remote server too.

     mvn -P setup
     
will do the some for all scripts - **takes a long time**.

the mirror will be located in directory __solr/tipitaka__. now you can run the server with

     mvn -P jetty-run

and access through
    
     http://localhost:8080/solr/web
     http://localhost:8080/solr/ios
     http://localhost:8080/solr/browse

just run the indexer on the mirror
------------

     mvn -P index-run -Dscript=deva
     
or

     mvn -P index-run

create the directory structure for browsing
-------------

     mvn -P directory-structure
     
create transcription table of the directory structure
-----------

     mvn -P directory-transcribe -Dscript=deva
     
or

     mvn -P directory-transcribe
     
package the war archive
---------

the war-archive for jetty6 with the name **tipitaka6.war**

     mvn -P package6
     
 and for jetty7 with the name **tipitaka.war** (not sure if that works because of the gzip filter)
 
     mvn package
     
 either archive can be found inside the _target_ directory.
