This directory holds external JAR dependencies referenced by
nbproject/project.properties and build.xml.

Required:
  mysql-connector-j.jar   (MySQL Connector/J 8.x JDBC driver)

Download it from https://dev.mysql.com/downloads/connector/j/ (or your
organization's Maven/Artifactory mirror) and place the .jar file directly
in this lib/ directory using the exact filename "mysql-connector-j.jar",
matching the reference in nbproject/project.properties
(file.reference.mysql-connector-j) and manifest.mf (Class-Path).
