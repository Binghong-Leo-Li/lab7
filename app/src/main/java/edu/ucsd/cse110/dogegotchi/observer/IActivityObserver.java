package edu.ucsd.cse110.dogegotchi.observer;

import edu.ucsd.cse110.dogegotchi.doge.Doge;

public interface IActivityObserver {

    public void onFoodClick(Doge.State s);
}
