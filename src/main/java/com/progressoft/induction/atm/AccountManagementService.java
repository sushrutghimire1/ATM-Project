package com.progressoft.induction.atm;

import com.progressoft.induction.atm.exceptions.AccountNotFoundException;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class AccountManagementService {
    private static final Map<String, BigDecimal> accountDetailMap;
    private static final Map<Banknote, Integer> availableMoneyMap;
    public static ArrayList<Banknote> lastTransaction;

    static {
        Map<String, BigDecimal> accountDetails = new HashMap<>();
        accountDetails.put("123456789", new BigDecimal("1000.0"));
        accountDetails.put("111111111", new BigDecimal("1000.0"));
        accountDetails.put("222222222", new BigDecimal("1000.0"));
        accountDetails.put("333333333", new BigDecimal("1000.0"));
        accountDetails.put("444444444", new BigDecimal("1000.0"));
        accountDetailMap = accountDetails;
        Map<Banknote, Integer> availableMoney = new HashMap<>();
        availableMoney.put(Banknote.FIFTY_JOD, 10);
        availableMoney.put(Banknote.TWENTY_JOD, 20);
        availableMoney.put(Banknote.TEN_JOD, 100);
        availableMoney.put(Banknote.FIVE_JOD, 100);
        availableMoneyMap = availableMoney;

    }

    public static BigDecimal getTotalMoneyInATM() {
        AtomicInteger totalMoney = new AtomicInteger(0);
        availableMoneyMap.forEach((bankNote, money) -> {
            if (bankNote.equals(Banknote.FIFTY_JOD))
                totalMoney.addAndGet(50 * money);
            if (bankNote.equals(Banknote.TWENTY_JOD))
                totalMoney.addAndGet(20 * money);
            if (bankNote.equals(Banknote.TEN_JOD))
                totalMoney.addAndGet(10 * money);
            if (bankNote.equals(Banknote.FIVE_JOD))
                totalMoney.addAndGet(5 * money);
        });
        return new BigDecimal(totalMoney.get());
    }

    public static BigDecimal getAccountBalance(String accountNumber) {
        BigDecimal amount = accountDetailMap.get(accountNumber);
        if (amount == null)
            throw new AccountNotFoundException();
        return amount;
    }

   /* private static final Map<Banknote, Integer> denomination = new HashMap<>();
    static int[] dp = new int[100 + 1];*/

    public static void decrementAmount(String accountNumber, BigDecimal amount) {
        lastTransaction = new ArrayList<>();
        BigDecimal newAmount = accountDetailMap.get(accountNumber).subtract(amount);
        accountDetailMap.replace(accountNumber, newAmount);
        while (!amount.equals(new BigDecimal("0.0"))) {
            Banknote[] values = Banknote.values();
            for (int i = values.length - 1; i >= 0; i--) {
                if (amount.equals(new BigDecimal("0.0")))
                    break;
                Banknote banknote = values[i];
                int numberOfNotes = amount.intValue() / banknote.getValue().intValue();
                if (numberOfNotes > 0) {
                    if (availableMoneyMap.get(banknote) > numberOfNotes) {
                        int newvalue = availableMoneyMap.get(banknote) - numberOfNotes;
                        availableMoneyMap.replace(banknote, newvalue);
                        BigDecimal subs = new BigDecimal(numberOfNotes);
                        subs = subs.multiply(banknote.getValue());
                        amount = amount.subtract(subs);
                        for (int j = 0; j < numberOfNotes; j++)
                            lastTransaction.add(banknote);
                    } else {
                        BigDecimal newamount = new BigDecimal(availableMoneyMap.get(banknote));
                        int val=availableMoneyMap.get(banknote);
                        availableMoneyMap.replace(banknote, 0);
                        newamount = newamount.multiply(banknote.getValue());
                        amount = amount.subtract(newamount);
                        for (int j = 0; j < val; j++)
                            lastTransaction.add(banknote);
                    }
                }

            }
        }
       /* Arrays.fill(dp, -1);
        int[] combinations = new int[Banknote.values().length];
        AtomicInteger i = new AtomicInteger(0);
        Arrays.stream(Banknote.values()).forEach(banknote -> {
            combinations[i.getAndIncrement()] = banknote.getValue().intValue();
        });
        findSolution(amount.intValue(), combinations);
        AtomicBoolean possible = new AtomicBoolean(false);
        denomination.forEach((banknote, numbers) -> {
            if (availableMoneyMap.get(banknote).compareTo(numbers) < 0){
                possible.set(false);
            }
        });*/

        /*if(possible.get()){
            denomination.forEach(((banknote, integer) -> {
                availableMoneyMap.put(banknote,availableMoneyMap.get(banknote)-integer);
            }));
        }*/


    }

   /* public static void main(String[] args) {
        BigDecimal amount = new BigDecimal("1000.0");
        while (!amount.equals(new BigDecimal("0.0"))) {
            Banknote[] values = Banknote.values();
            for (int i = values.length - 1; i >= 0; i--) {
                if (amount.equals(new BigDecimal("0.0")))
                    break;
                Banknote banknote = values[i];
                int numberOfNotes = amount.intValue() / banknote.getValue().intValue();
                System.out.println("3-->"+numberOfNotes);
                if (numberOfNotes > 0) {
                    if (availableMoneyMap.get(banknote) > numberOfNotes) {
                        int newvalue = availableMoneyMap.get(banknote) - numberOfNotes;
                        availableMoneyMap.replace(banknote, newvalue);
                        BigDecimal subs=new BigDecimal(numberOfNotes);
                        subs=subs.multiply(banknote.getValue());
                        amount = amount.subtract(subs);
                        System.out.println("1-->" + amount);
                    } else {
                        BigDecimal newamount = new BigDecimal(availableMoneyMap.get(banknote));
                        availableMoneyMap.replace(banknote, 0);
                        newamount = newamount.multiply(banknote.getValue());
                        amount = amount.subtract(newamount);
                        System.out.println("2-->" + amount);
                    }
                }

            }
        }
    }*/


    /*static void findSolution(int amountToCheck, int[] combinations) {
        if (amountToCheck == 0)
            return;
        for (int combination : combinations) {
            if (amountToCheck - combination >= 0 && dp[amountToCheck - combination] + 1 == dp[amountToCheck]) {
                if (denomination.containsKey(getBankNoteFromInt(combination)))
                    denomination.replace(getBankNoteFromInt(combination), denomination.get(getBankNoteFromInt(combination)) + 1);
                else
                    denomination.replace(getBankNoteFromInt(combination), 0);
                findSolution(amountToCheck - combination, combinations);
                break;
            }
        }
    }*/

    public static Banknote getBankNoteFromInt(int number) {
        switch (number) {
            case 5:
                return Banknote.FIVE_JOD;
            case 10:
                return Banknote.TEN_JOD;
            case 20:
                return Banknote.TWENTY_JOD;
            case 50:
                return Banknote.FIFTY_JOD;
        }
        return Banknote.FIVE_JOD;

    }

}
