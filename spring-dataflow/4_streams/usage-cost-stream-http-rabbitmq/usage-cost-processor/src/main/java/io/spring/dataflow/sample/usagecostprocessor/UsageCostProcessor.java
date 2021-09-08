package io.spring.dataflow.sample.usagecostprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.SendTo;

import io.spring.dataflow.sample.domain.UsageCostDetail;
import io.spring.dataflow.sample.domain.UsageDetail;

@EnableBinding(Processor.class)
public class UsageCostProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(UsageCostProcessor.class);

	private double ratePerSecond = 0.1;

	private double ratePerMB = 0.05;

	@StreamListener(Processor.INPUT)
	@SendTo(Processor.OUTPUT)
	public UsageCostDetail processUsageCost(String userCostDetailData) {
		logger.info("Start: " + userCostDetailData);
		UsageCostDetail usageCostDetail = new UsageCostDetail();
		if(userCostDetailData != null && userCostDetailData.contains(",")) {
			String[] tokens = userCostDetailData.split(",");
			UsageDetail usageDetail = new UsageDetail();
			usageDetail.setUserId(tokens[0]);
			usageDetail.setDuration(Long.parseLong(tokens[1]));
			usageDetail.setData(Long.parseLong(tokens[2]));
			
			usageCostDetail.setUserId(usageDetail.getUserId());
			usageCostDetail.setCallCost(usageDetail.getDuration() * this.ratePerSecond);
			usageCostDetail.setDataCost(usageDetail.getData() * this.ratePerMB);
		}
		logger.info("End: " + usageCostDetail);
		return usageCostDetail;
	}
}
