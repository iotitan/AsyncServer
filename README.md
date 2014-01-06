AsyncServer
===========

Java 7 nio asynchronous web server


### How to build:
1. Import all source files into an eclipse project and run.
 + __OR__
2. javac ASock.java
  
### Zero configuration:
##### The server runs with optional configuration. Namely:
* Default to port 8080
* Document root is CLASS_DIRECTORY/web
* Directory index is "index.html"

##### If configuration is needed:
* web.conf must be created in the same directory as the server
* Settings are in the format of: category.subcategory.name = value
* One setting per line
* Common settings:
  * server.port
  * server.root
  * server.index
