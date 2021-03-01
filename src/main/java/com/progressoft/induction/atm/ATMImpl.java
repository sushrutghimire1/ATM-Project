package com.progressoft.induction.atm;

import com.progressoft.induction.atm.exceptions.InsufficientFundsException;
import com.progressoft.induction.atm.exceptions.NotEnoughMoneyInATMException;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ATMImpl implements ATM {

    private final BankingSystem bankingSystem = new BankingSystemImpl();
    private final Map<Banknote, Integer> availableMoneyMap;
    private final ArrayList<Banknote> lastTransaction = new ArrayList<>();
    private final HashMap<Banknote, Integer> withdrawalMoneyMap = new HashMap<>();
    private double leastStandardDeviation = Double.MAX_VALUE;

    ATMImpl() {
        Map<Banknote, Integer> availableMoney = new HashMap<>();
        availableMoney.put(Banknote.FIFTY_JOD, 10);
        availableMoney.put(Banknote.TWENTY_JOD, 20);
        availableMoney.put(Banknote.TEN_JOD, 100);
        availableMoney.put(Banknote.FIVE_JOD, 100);
        availableMoneyMap = availableMoney;
    }

    @Override
    public List<Banknote> withdraw(String accountNumber, BigDecimal amount) {
        if (amount.compareTo(bankingSystem.getAccountBalance(accountNumber)) > 0)
            throw new InsufficientFundsException();
        if (amount.compareTo(getTotalMoneyInATM()) > 0)
            throw new NotEnoughMoneyInATMException();
        checkAmountValidity(amount);
        bankingSystem.debitAccount(accountNumber, amount);
        withdrawAmountFromATM(amount);
        return lastTransaction;
    }

    private void checkAmountValidity(BigDecimal amount) {
        if (amount.compareTo(new BigDecimal(0)) <= 0) {
            throw new InputMismatchException();
        }
        if (amount.intValue() % Banknote.values()[0].getValue().intValue() != 0) {
            throw new InputMismatchException();
        }
    }

    public void withdrawAmountFromATM(BigDecimal amount) {
        regularDecrementFromATM(amount);
        diversifyLastTransaction(prepareWithdrawalMoneyMap());
        withdrawalMoneyMap.putAll(prepareWithdrawalMoneyMap());
        lastTransaction.clear();
        withdrawalMoneyMap.forEach((key, value) -> {
            for (int i = 0; i < value; i++) {
                lastTransaction.add(key);
                availableMoneyMap.put(key, availableMoneyMap.get(key) - 1);
            }
        });
    }

    public BigDecimal getTotalMoneyInATM() {
        AtomicInteger totalMoney = new AtomicInteger(0);
        this.availableMoneyMap.forEach((bankNote, times) ->
                totalMoney.addAndGet(
                        Banknote.valueOf(bankNote.toString()).getValue().multiply(new BigDecimal(times)).intValue()
                )
        );
        return new BigDecimal(totalMoney.get());
    }

    private boolean isPresentInATM(HashMap<Banknote, Integer> counterMap) {
        AtomicBoolean isPresent = new AtomicBoolean(true);
        availableMoneyMap.forEach((key, value) -> {
            if (counterMap.containsKey(key))
                isPresent.set(isPresent.get() && (counterMap.get(key) <= availableMoneyMap.get(key)));
        });
        return isPresent.get();
    }

    private void regularDecrementFromATM(BigDecimal givenAmount) {
        lastTransaction.clear();
        while (givenAmount.compareTo(new BigDecimal("0.0")) > 0) {
            for (int i = Banknote.values().length - 1; i >= 0; i--) {
                if (givenAmount.compareTo(new BigDecimal("0.0")) <= 0)
                    break;
                Banknote banknote = Banknote.values()[i];
                int numberOfNotesToDecrement = givenAmount.intValue() / banknote.getValue().intValue();
                if (numberOfNotesToDecrement > 0) {
                    if (availableMoneyMap.get(banknote) > numberOfNotesToDecrement) {
                        givenAmount = givenAmount.subtract(
                                banknote.getValue().multiply(new BigDecimal(numberOfNotesToDecrement))
                        );
                        for (int j = 0; j < numberOfNotesToDecrement; j++)
                            lastTransaction.add(banknote);
                    } else {
                        givenAmount = givenAmount.subtract(
                                banknote.getValue().multiply(new BigDecimal(availableMoneyMap.get(banknote)))
                        );
                        for (int j = 0; j < availableMoneyMap.get(banknote); j++)
                            lastTransaction.add(banknote);
                    }
                }
            }
        }
    }

    private void diversifyLastTransaction(HashMap<Banknote, Integer> atmNotesCounterMap) {
        for (int i = Banknote.values().length - 1; i >= 0; i--)
            if (atmNotesCounterMap.containsKey(Banknote.values()[i]))
                if (atmNotesCounterMap.get(Banknote.values()[i]) != 0) break;
                else atmNotesCounterMap.remove(Banknote.values()[i]);
        Banknote maxBankNote = HelperUtils.findHeavierNote(
                atmNotesCounterMap, Collections.max(atmNotesCounterMap.values())
        );
        Banknote minBankNote = HelperUtils.findHeavierNote(
                atmNotesCounterMap, Collections.min(atmNotesCounterMap.values())
        );
        int numberOfMinReq = maxBankNote.getValue().intValue() / minBankNote.getValue().intValue();
        int remainingCount = maxBankNote.getValue().intValue() % minBankNote.getValue().intValue();
        if (numberOfMinReq <= 1)
            return;
        atmNotesCounterMap.put(maxBankNote, atmNotesCounterMap.get(maxBankNote) - 1);
        atmNotesCounterMap.put(minBankNote, atmNotesCounterMap.get(minBankNote) + numberOfMinReq);
        if (remainingCount != 0)
            for (int i = Banknote.values().length - 1; i >= 0; i--)
                if (Banknote.values()[i].getValue().intValue() == remainingCount)
                    atmNotesCounterMap.put(Banknote.values()[i], atmNotesCounterMap.get(Banknote.values()[i]) + 1);

        if (leastStandardDeviation > HelperUtils.calculateStandardDeviation(atmNotesCounterMap)
                && isPresentInATM(atmNotesCounterMap)) {
            leastStandardDeviation = HelperUtils.calculateStandardDeviation(atmNotesCounterMap);
            withdrawalMoneyMap.clear();
            withdrawalMoneyMap.putAll(atmNotesCounterMap);
        }
        diversifyLastTransaction(atmNotesCounterMap);
    }

    private HashMap<Banknote, Integer> prepareWithdrawalMoneyMap() {
        HashMap<Banknote, Integer> withdrawalMoneyMap = new HashMap<>();
        for (int i = Banknote.values().length - 1; i >= 0; i--) {
            withdrawalMoneyMap.put(Banknote.values()[i], 0);
        }
        lastTransaction.forEach(transaction ->
                withdrawalMoneyMap.replace(transaction, withdrawalMoneyMap.get(transaction) + 1)
        );
        return withdrawalMoneyMap;
    }
}
