package com.progressoft.induction.atm;

import java.math.BigDecimal;

public class BankingSystemImpl implements BankingSystem {
    @Override
    public BigDecimal getAccountBalance(String accountNumber) {
        return AccountManagementService.getAccountBalance(accountNumber);
    }

    @Override
    public void debitAccount(String accountNumber, BigDecimal amount) {
        AccountManagementService.decrementAmount(accountNumber, amount);
    }
}
