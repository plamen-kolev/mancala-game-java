package com.mancalagame.mancala.statemachine.configuration;

import com.mancalagame.mancala.statemachine.events.Event;
import com.mancalagame.mancala.statemachine.states.State;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;

@Configuration
@EnableStateMachine
public class SimpleStateMachineConfiguration
        extends StateMachineConfigurerAdapter<State, Event> {

    @Override
    public void configure(StateMachineStateConfigurer<State, Event> states)
            throws Exception {

        states
                .withStates()
                .initial(State.PLAYER1_TURN)
                .end(State.END)
                .states(EnumSet.allOf(State.class));

    }

    @Override
    public void configure(
            StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {

        transitions.withExternal()
                .source(State.PLAYER1_TURN)
                .target(State.PLAYER1_GETS_EXTRA_TURN_CHOICE)
                .event(Event.PLAY)
        ;
    }
}