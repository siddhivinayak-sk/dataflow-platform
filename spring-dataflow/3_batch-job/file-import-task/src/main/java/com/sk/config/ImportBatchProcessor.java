package com.sk.config;

import org.springframework.batch.item.ItemProcessor;

import com.sk.model.Account;
import com.sk.model.AccountDetail;

public class ImportBatchProcessor implements ItemProcessor<Account, AccountDetail> {

	@Override
	public AccountDetail process(Account account) {
		AccountDetail detail = new AccountDetail();
		if(null != account) {
			detail.setAccountNo(account.getAccountNo());
			detail.setBalance(0.0);
			detail.setBankCode(account.getBankCode());
			detail.setBranchCode(account.getBranchCode());
			detail.setCountryCode("IN");
			detail.setCustomerId(account.getCustomerId());
		}
		return detail;
	}
}
