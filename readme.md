Start local server
==========

Checkout the heroku git repo: https://git.heroku.com/pure-wildwood-66742.git

To build the servdr jar:
```
mvn package
```
To execute the server run
```
java -jar target/dependency/webapp-runner.jar --port 8080 target/*.war
```

The web interface runs on http://localhost:8080/solr/web

The template files for the web interface under https://github.com/tipitaka-org/tipitaka-search/tree/main/solr/conf/velocity

See https://velocity.apache.org/ for details.

Any changes on `main` github.org needs a PR and once OK on main, there needs to be a merge into the heroku branch which can be used to deploy it the changes into heroku.
