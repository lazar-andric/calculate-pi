package com.lazar.andric;

import akka.actor.typed.ActorSystem;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        final long numberOfIntervals;
        final int numberOfWorkers;

        if( args.length == 2) {
            numberOfIntervals = Long.parseLong(args[0]);
            numberOfWorkers = Integer.parseInt(args[1]);
        } else {
            numberOfIntervals = Constants.NUMBER_OF_INTERVALS;
            numberOfWorkers = Constants.NUMBER_OF_WORKERS;
        }

        final ActorSystem<Master.Command> master = ActorSystem.create(Master.create(numberOfIntervals, numberOfWorkers), "master");
        master.tell(new Master.StartCalculation());
        try {
            System.out.println(">>> stisni enter da izadjes <<<");
            System.in.read();
        } catch (IOException ignored) {
        } finally {
            master.terminate();
        }
    }
}
