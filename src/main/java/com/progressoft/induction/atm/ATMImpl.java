package com.progressoft.induction.atm;

import com.progressoft.induction.atm.exceptions.InsufficientFundsException;
import com.progressoft.induction.atm.exceptions.NotEnoughMoneyInATMException;

import java.math.BigDecimal;
import java.util.List;

public class ATMImpl implements ATM {
    private final BankingSystem bankingSystem = new BankingSystemImpl();

    @Override
    public List<Banknote> withdraw(String accountNumber, BigDecimal amount) {
        BigDecimal accountBalance = bankingSystem.getAccountBalance(accountNumber);
        if (amount.compareTo(accountBalance) > 0)
            throw new InsufficientFundsException();
        if (amount.compareTo(AccountManagementService.getTotalMoneyInATM()) > 0)
            throw new NotEnoughMoneyInATMException();
        bankingSystem.debitAccount(accountNumber, amount);
        return AccountManagementService.lastTransaction;
    }
}
