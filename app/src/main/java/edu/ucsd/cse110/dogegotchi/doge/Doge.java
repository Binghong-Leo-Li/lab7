package edu.ucsd.cse110.dogegotchi.doge;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import edu.ucsd.cse110.dogegotchi.daynightcycle.IDayNightCycleObserver;
import edu.ucsd.cse110.dogegotchi.observer.IActivityObserver;
import edu.ucsd.cse110.dogegotchi.observer.ISubject;
import edu.ucsd.cse110.dogegotchi.ticker.ITickerObserver;

/**
 * Logic for our friendly, sophisticated doge.
 *
 * TODO: Exercise 1 -- add support for {@link State#SLEEPING}.
 *
 * TODO: Exercise 2 -- enable {@link State#SAD} mood, and add support for {@link State#EATING} behavior.
 */
public class Doge implements ISubject<IDogeObserver>, ITickerObserver, IDayNightCycleObserver, IActivityObserver {
    /**
     * Current number of ticks. Reset after every potential mood swing.
     */
    int numTicks;

    /**
     * How many ticks before we toss a multi-sided die to check mood swing.
     */
    final int numTicksBeforeMoodSwing;

    /**
     * Probability of a mood swing every {@link #numTicksBeforeMoodSwing}.
     */
    final double moodSwingProbability;

    /**
     * State of doge.
     */
    State state;

    private Collection<IDogeObserver> observers;

    /**
     * Constructor.
     *
     * @param numTicksBeforeMoodSwing Number of ticks before checking for mood swing.
     * @param moodSwingProbability Probability of a mood swing every {@link #numTicksBeforeMoodSwing}.
     */
    public Doge(final int numTicksBeforeMoodSwing, final double moodSwingProbability) {
        Preconditions.checkArgument(
                0.0 <= moodSwingProbability && moodSwingProbability < 1.0f,
                "Mood swing probability must be in range [0,1).");

        this.numTicks = 0;
        this.numTicksBeforeMoodSwing = numTicksBeforeMoodSwing;
        this.moodSwingProbability = moodSwingProbability;
        this.state = State.HAPPY;
        this.observers = new ArrayList<>();
        Log.i(this.getClass().getSimpleName(), String.format(
                "Creating Doge with initial state %s, with mood swing prob %.2f"
                + "and num ticks before each swing attempt %d",
                this.state, this.moodSwingProbability, this.numTicksBeforeMoodSwing));
    }

    @Override
    public void onTick() {
        this.numTicks++;

        if (this.numTicks > 0
            && (this.numTicks % this.numTicksBeforeMoodSwing) == 0) {
            tryRandomMoodSwing();
            this.numTicks = 0;
        }

        if (this.state.equals(State.EATING) && this.numTicks == 0) {
            this.setState(State.HAPPY);
        }
    }

    /**
     * TODO: Exercise 1 -- Fill in this method to randomly make doge sad with probability {@link #moodSwingProbability}.
     *
     * **Strictly follow** the Finite State Machine in the write-up.
     */
    private void tryRandomMoodSwing() {
        // TODO: Exercise 1 -- Implement this method...
        if (this.state.equals(State.HAPPY)) {
            Random rand = new Random();
            double chance = rand.nextDouble();
            if (chance < this.moodSwingProbability) this.setState(State.SAD);
        }
        else return;

    }

    @Override
    public void register(IDogeObserver observer) {
        observers.add(observer);
    }

    @Override
    public void unregister(IDogeObserver observer) {
        observers.remove(observer);
    }

    /**
     * Updates the state of our friendly doge and updates all observers.
     *
     * Note: observe how by using a setter we guarantee that side effects of
     *       an update occur, namely notifying the observers. And it's unused
     *       right now, hm...
     */
    private void setState(final Doge.State newState) {
        this.state = newState;
        Log.i(this.getClass().getSimpleName(), "Doge state changed to: " + newState);
        for (IDogeObserver observer : this.observers) {
            observer.onStateChange(newState);
        }
    }

    @Override
    public void onPeriodChange(Period newPeriod) {
        if (newPeriod.equals(Period.DAY)) this.setState(State.HAPPY);
        else this.setState(State.SLEEPING);
    }

    @Override
    public void onFoodClick(State newState) {
        if (newState.equals(State.EATING)) {
            this.setState(State.EATING);
        }
        this.numTicks = -2;
    }


    /**
     * Moods and actions for our doge.
     */
    public enum State {
        HAPPY,
        SAD,
        // TODO: Implement asleep and eating states, and transitions between all states.
        SLEEPING,
        EATING;
    }
}
