package com.mancalagame.mancala.statemachine.guards;

import com.mancalagame.mancala.statemachine.events.Event;
import com.mancalagame.mancala.statemachine.states.State;
import org.springframework.statemachine.guard.Guard;

import java.util.Arrays;

public class GuardComposer {
    public static Guard<State, Event> compose(Guard<State, Event>... guards) {
        return context -> Arrays.stream(guards).allMatch(guard -> guard.evaluate(context));
    }
}
