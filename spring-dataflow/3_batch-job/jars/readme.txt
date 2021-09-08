Run below command to build jar file:
mvn clean package

Download Spring Cloud Data Flow artifacts:
curl -k "https://repo.spring.io/release/org/springframework/cloud/spring-cloud-dataflow-server/2.7.2/spring-cloud-dataflow-server-2.7.2.jar" --output spring-cloud-dataflow-server-2.7.2.jar
curl -k "https://repo.spring.io/release/org/springframework/cloud/spring-cloud-dataflow-shell/2.7.2/spring-cloud-dataflow-shell-2.7.2.jar" --output spring-cloud-dataflow-shell-2.7.2.jar
curl -k "https://repo.spring.io/release/org/springframework/cloud/spring-cloud-skipper-server/2.6.2/spring-cloud-skipper-server-2.6.2.jar" --output spring-cloud-skipper-server-2.6.2.jar

Run below command to start SCDF on local
java -Xmx1g -Dspring.jpa.database-platform=org.hibernate.dialect.SQLServer2012Dialect -Dspring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver -Dspring.datasource.url=jdbc:sqlserver://localhost:1433;DatabaseName=skipper-db -Dspring.datasource.username=sa -Dspring.datasource.password=p@ssw0rd -jar spring-cloud-skipper-server-2.6.2.jar
java -Xmx1g -Dspring.jpa.database-platform=org.hibernate.dialect.SQLServer2012Dialect -Dspring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver -Dspring.datasource.url=jdbc:sqlserver://localhost:1433;DatabaseName=dataflow-db -Dspring.datasource.username=sa -Dspring.datasource.password=p@ssw0rd -jar spring-cloud-dataflow-server-2.7.2.jar  --spring.cloud.skipper.client.serverUri=http://localhost:7577/api
java -jar spring-cloud-dataflow-shell-2.7.2.jar

Open SCDF dashboard:
http://localhost:9393/dashboard

Register Task app in SCDF dashbaord:
SpringBatchFileImportTask file:///C:/sandeep/code/source/others/spring-dataflow/3_batch-job/jars/file-import-task-0.0.1.jar
SpringTaskFileMoveTask file:///C:/sandeep/code/source/others/spring-dataflow/3_batch-job/jars/file-move-task-0.0.1.jar

Launch Task from the SCDF menus:

