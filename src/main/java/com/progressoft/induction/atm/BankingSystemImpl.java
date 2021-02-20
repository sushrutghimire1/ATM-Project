package com.progressoft.induction.atm;

import com.progressoft.induction.atm.exceptions.AccountNotFoundException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class BankingSystemImpl implements BankingSystem {
    private final Map<String, BigDecimal> accountDetailMap;

    BankingSystemImpl() {
        Map<String, BigDecimal> accountDetails = new HashMap<>();
        accountDetails.put("123456789", new BigDecimal("1000.0"));
        accountDetails.put("111111111", new BigDecimal("1000.0"));
        accountDetails.put("222222222", new BigDecimal("1000.0"));
        accountDetails.put("333333333", new BigDecimal("1000.0"));
        accountDetails.put("444444444", new BigDecimal("1000.0"));
        accountDetailMap = accountDetails;
    }

    @Override
    public BigDecimal getAccountBalance(String accountNumber) {
        BigDecimal amount = accountDetailMap.get(accountNumber);
        if (amount == null)
            throw new AccountNotFoundException();
        return amount;
    }

    @Override
    public void debitAccount(String accountNumber, BigDecimal amount) {
        accountDetailMap.replace(accountNumber, accountDetailMap.get(accountNumber).subtract(amount));
    }
}
