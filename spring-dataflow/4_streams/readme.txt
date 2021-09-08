1. Run below command to build jar for Stream projects
mvn clean package
2. Start SCDF dashaboard
3. Register each Stream (Source, processor and sink) in SCDF
4. Start Stream from the SCDF

Stream-1-Sink file:///C:/sandeep/code/source/others/spring-dataflow/4_streams/usage-cost-stream/usage-cost-logger-sink/target/usage-cost-logger-0.0.1-SNAPSHOT.jar
Stream-1-Processor file:///C:/sandeep/code/source/others/spring-dataflow/4_streams/usage-cost-stream/usage-cost-processor/target/usage-cost-processor-0.0.1-SNAPSHOT.jar
Stream-1-Source file:///C:/sandeep/code/source/others/spring-dataflow/4_streams/usage-cost-stream/usage-detail-sender-source/target/usage-detail-sender-0.0.1-SNAPSHOT.jar


Stream-2-Sink file:///C:/sandeep/code/source/others/spring-dataflow/4_streams/usage-cost-stream-http-rabbitmq/usage-cost-logger-sink/target/usage-cost-logger-rabbit-0.0.1-SNAPSHOT.jar
Stream-2-Processor file:///C:/sandeep/code/source/others/spring-dataflow/4_streams/usage-cost-stream-http-rabbitmq/usage-cost-processor/target/usage-cost-processor-rabbit-0.0.1-SNAPSHOT.jar
Stream-2-Source file:///C:/sandeep/code/source/others/spring-dataflow/4_streams/usage-cost-stream-http-rabbitmq/usage-detail-sender-source/target/usage-detail-sender-rabbit-0.0.1-SNAPSHOT.jar

5. Send data to Stream
curl -X POST http://localhost:34504/send -H "Content-Type: plain/text" -d u001,1,2

curl -X POST http://localhost:52350/send -H "Content-Type: plain/text" -d u001,1,2

Note: Spring Stream processing work with middleware. Here RabbitMQ has been used therefore RabbitMQ must be running with configured (in properties) host and port 