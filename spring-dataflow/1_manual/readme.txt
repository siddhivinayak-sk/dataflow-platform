Run below command to build jar:
mvn clean package

java -jar -Dspring.config.additional-location=C:\sandeep\code\source\others\1\file-import-task.yaml file-import-task-v0.0.1.jar

java -jar -Dspring.config.additional-location=C:\sandeep\code\source\others\1\file-move-task.yaml file-move-task-v0.0.1.jar


java -jar -Dcom.ibm.jsse2.overrideDefaultTLS=true -Dspring.config.additional-location=/opt/application/myapp/testjobs/file-import-task.yaml /opt/application/myapp/testjobs/file-import-task-v0.0.1.jar

java -jar -Dcom.ibm.jsse2.overrideDefaultTLS=true -Dspring.config.additional-location=/opt/application/myapp/testjobs/file-move-task.yaml /opt/application/myapp/testjobs/file-move-task-v0.0.1.jar





