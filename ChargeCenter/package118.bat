set MAVEN_OPTS= -Xms128m -Xmx512m
mvn clean package -P118 -Dmaven.test.skip=true && pause