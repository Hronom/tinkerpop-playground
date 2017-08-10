### 1. Build project
Execute:
```
mvn clean install
```

### 2. Run Neo4j
To launch Neo4j use docker
```
docker-compose up -d
```
Web interface will be available here [http://localhost:7474/browser/](http://localhost:7474/browser/)

### 3. Run App
```
java -jar ./bin/tinkerpop-playground-1.0.0.jar
```

### 4. Check results
Run program, after it complete, execute in web browser [http://localhost:7474/browser/](http://localhost:7474/browser/):
```
MATCH (n1)-[r]->(n2) RETURN r, n1, n2 LIMIT 25
```
to see some data.