package io.spring.dataflow.sample.usagecostsender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.spring.dataflow.sample.domain.UsageCostDetail;

@EnableBinding(Sink.class)
public class UsageCostSender {

	private static final Logger logger = LoggerFactory.getLogger(UsageCostSenderApplication.class);
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Value("${exchange.name:}")
	private String exchangeName;
	
	@Value("${routing.key:}")
	private String routingKey;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@StreamListener(Sink.INPUT)
	public void process(UsageCostDetail usageCostDetail) throws JsonProcessingException {
		logger.info("Message is being send to RabbitMQ");
		String data = objectMapper.writeValueAsString(usageCostDetail);
		rabbitTemplate.convertAndSend(exchangeName, routingKey, data);
	}
}
