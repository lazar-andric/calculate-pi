package com.lazar.andric;

public class SimpleWay {

    private static long NUMBER_OF_INTERVALS;

    public static void main(String[] args) {
        if( args.length == 1) {
            NUMBER_OF_INTERVALS = Long.parseLong(args[0]);
        } else {
            NUMBER_OF_INTERVALS = Constants.NUMBER_OF_INTERVALS;
        }
        new SimpleWay().run();
    }

    private void run() {
        long startTimeMillis = System.currentTimeMillis();
        calculatePi();
        long endTimeMillis = System.currentTimeMillis();
        System.out.println("Vreme izvrsavanja u milisekundama je: " + (endTimeMillis - startTimeMillis));
    }

    private double calculatePi() {
        double h = 1.0 / NUMBER_OF_INTERVALS;
        double sum = 0;
        for (int i = 0; i <= NUMBER_OF_INTERVALS; i ++) {
            double pointOnInterval = h * (i - 0.5);
            sum += 4.0 / (1.0 + pointOnInterval * pointOnInterval);
        }
        return h * sum;
    }
}
