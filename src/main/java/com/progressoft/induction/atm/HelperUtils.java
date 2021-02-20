package com.progressoft.induction.atm;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class HelperUtils {

    public static Banknote findElementWithValue(Map<Banknote, Integer> map, Integer value) {
        Banknote minMax = null;
        BigDecimal valueOfMinMax = new BigDecimal(Integer.MIN_VALUE);
        for (Map.Entry<Banknote, Integer> entry : map.entrySet()) {
            if (entry.getValue().equals(value) && valueOfMinMax.compareTo(entry.getKey().getValue()) < 0) {
                minMax = entry.getKey();
                valueOfMinMax = entry.getKey().getValue();
            }
        }
        return minMax;
    }

    public static double calculateStandardDeviation(HashMap<Banknote, Integer> notes) {
        int[] array = new int[notes.size()];
        AtomicInteger count = new AtomicInteger(0);
        notes.forEach((key, value) -> {
            array[count.getAndIncrement()] = value;
        });
        double sum = 0.0;
        for (int k : array)
            sum += k;
        double mean = sum / array.length;
        double standardDeviation = 0.0;
        for (int j : array)
            standardDeviation += Math.pow(j - mean, 2);
        return Math.sqrt(standardDeviation / array.length);
    }
}
