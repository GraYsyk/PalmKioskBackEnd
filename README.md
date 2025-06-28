# HOW TO SETUP?
In order to start this project you need to install

### MAVEN & JAVA 21 | Build Tool & Lang

https://maven.apache.org/download.cgi
https://www.oracle.com/de/java/technologies/downloads/

## POSTGRESQL 7 and PGADMIN 4 | DB

https://www.postgresql.org/download/
https://www.pgadmin.org/download/pgadmin-4-windows/

---

## After downloading everything. 

Open the project directory (Just a directory with project)

- Open terminal (admin) at this directory and run
  ```
  mvn clean package
  ```
- After finishing the compilation, go to the ../target and find the .jar file
- Copy .jar file name
- Run in terminal (./target directory)
```
java -jar [name of the jar file].jar
```

Then go to **localhost:8080/swagger-ui.html**

Done.
