start cmd /k java -Xmx1g -Dspring.jpa.database-platform=org.hibernate.dialect.SQLServer2012Dialect -Dspring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver -Dspring.datasource.url=jdbc:sqlserver://localhost:1433;DatabaseName=skipper-db -Dspring.datasource.username=sa -Dspring.datasource.password=p@ssw0rd -jar spring-cloud-skipper-server-2.6.2.jar
timeout 180
start cmd /k java -Xmx1g -Dspring.jpa.database-platform=org.hibernate.dialect.SQLServer2012Dialect -Dspring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver -Dspring.datasource.url=jdbc:sqlserver://localhost:1433;DatabaseName=dataflow-db -Dspring.datasource.username=sa -Dspring.datasource.password=p@ssw0rd -jar spring-cloud-dataflow-server-2.7.2.jar  --spring.cloud.skipper.client.serverUri=http://localhost:7577/api
timeout 180
start cmd /k java -jar spring-cloud-dataflow-shell-2.7.2.jar

