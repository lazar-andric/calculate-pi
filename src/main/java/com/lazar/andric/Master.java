package com.lazar.andric;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class Master extends AbstractBehavior<Master.Command> {

    private final long numberOfIntervals;
    private final int numberOfWorkers;
    private final List<ActorRef<Worker.Command>> workers = new ArrayList<>();
    private final List<Double> results = new ArrayList<>();
    private long startTimeMillis;

    public interface Command {}

    public static class StartCalculation implements Command {}

    @AllArgsConstructor
    public static class Result implements Command {
        final double value;
    }

    public static Behavior<Command> create(long numberOfIntervals, int numberOfWorkers) {
        return Behaviors.setup(context -> new Master(context, numberOfIntervals, numberOfWorkers));
    }

    private Master(ActorContext<Command> context, long numberOfIntervals, int numberOfWorkers) {
        super(context);
        this.numberOfIntervals = numberOfIntervals;
        this.numberOfWorkers = numberOfWorkers;
        createWorkers();
    }

    private void createWorkers() {
        for (int i = 0; i < numberOfWorkers; i++) {
            workers.add(getContext().spawn(Worker.create(numberOfIntervals, i, numberOfWorkers), "worker" + i));
        }
    }

    @Override public Receive<Command> createReceive() {
        return newReceiveBuilder().onSignal(PostStop.class, signal -> onPostStop())
                                  .onMessage(StartCalculation.class, message -> onStartCalculation())
                                  .onMessage(Result.class, this::onResult)
                                  .build();
    }

    private Master onPostStop() {
        getContext().getLog().info("Master je stao.");
        return this;
    }

    private Behavior<Command> onStartCalculation() {
        startTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < numberOfWorkers; i++) {
            ActorRef<Worker.Command> worker = workers.get(i);
            worker.tell(new Worker.Calculate(getContext().getSelf()));
        }
        return this;
    }

    private Behavior<Command> onResult(Result r) {
        this.results.add(r.value);
        if (this.results.size() == numberOfWorkers) {
            double finalResult = this.results.stream().reduce(0.0, Double::sum);
            long endTimeMillis = System.currentTimeMillis();
            getContext().getLog().info("suma je {}", finalResult);
            getContext().getLog().info("vreme izvrsavanja u milisekundama je {}", endTimeMillis - startTimeMillis);
            return Behaviors.stopped();
        }
        return this;
    }
}
