package com.lazar.andric;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.AllArgsConstructor;

public class Worker extends AbstractBehavior<Worker.Command> {

    private final long numberOfIntervals;
    private final int workerNumber;
    private final int numberOfWorkers;

    public interface Command {}

    public static Behavior<Command> create(long numberOfIntervals, int workerNumber, int numberOfWorkers) {
        return Behaviors.setup(context -> new Worker(context, numberOfIntervals, workerNumber, numberOfWorkers));
    }

    private Worker(ActorContext<Command> context, long numberOfIntervals, int workerNumber, int numberOfWorkers) {
        super(context);
        this.numberOfIntervals = numberOfIntervals;
        this.workerNumber = workerNumber;
        this.numberOfWorkers = numberOfWorkers;
    }

    @AllArgsConstructor
    public static final class Calculate implements Command {
        final ActorRef<Master.Command> replyTo;
    }

    @Override public Receive<Command> createReceive() {
        return newReceiveBuilder().onMessage(Calculate.class, this::onCalculate)
                                  .build();
    }

    private Behavior<Command> onCalculate(Calculate c) {
        double value = calculation();
        c.replyTo.tell(new Master.Result(value));
        return this;
    }

    private double calculation() {
        double h = 1.0 / numberOfIntervals;
        double sum = 0;
        for (int i = workerNumber + 1; i <= numberOfIntervals; i += numberOfWorkers) {
            double pointOnInterval = h * (i - 0.5);
            sum += 4.0 / (1.0 + pointOnInterval * pointOnInterval);
        }
        return h * sum;
    }
}
