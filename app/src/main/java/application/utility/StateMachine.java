package application.utility;

import java.util.HashMap;

/**
 * Created by Niccolo on 14/04/2017.
 */

public abstract class StateMachine {
    //stato attuale in cui si trova
    protected int currentState;
    //stato immediatamente precedente
    protected int previousState;
    //indica che la state machine è in funzione o meno
    protected boolean running;

    public StateMachine () {
        currentState = 0;
        previousState = 0;
        running = true;
    }

    public int getState() {
        return currentState;
    }


    //in base allo stato attuale ed alle condizioni in cui si trova la macchina, viene valutato lo stato successivo
    protected int nextState() {
        int n = 0;
        return n;
    }
    //vengono modificati i valori relativi allo stato attuale ed a quello precedente
    protected void changeState(int next) {
        previousState = currentState;
        currentState = next;
    }

    //vengono eseguite le istruzioni in base allo stato attuale
    protected void executeState() {

    }


}
