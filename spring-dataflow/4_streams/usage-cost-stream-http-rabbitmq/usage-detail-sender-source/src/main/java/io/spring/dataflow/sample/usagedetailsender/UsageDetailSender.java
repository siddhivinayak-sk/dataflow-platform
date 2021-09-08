package io.spring.dataflow.sample.usagedetailsender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableBinding(Source.class)
@RestController
public class UsageDetailSender {
	
	private static final Logger logger = LoggerFactory.getLogger(UsageDetailSender.class);

	@Autowired
	private Source source;

	@RequestMapping("/send")
	public String post(@RequestBody String body) {
		source.output().send(MessageBuilder.withPayload(body).build());
		logger.info("New Request:" + body);
		return "success!";
	}
}