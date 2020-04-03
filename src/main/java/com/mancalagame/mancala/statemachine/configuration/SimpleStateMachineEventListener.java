package com.mancalagame.mancala.statemachine.configuration;

import com.mancalagame.mancala.statemachine.events.Event;
import com.mancalagame.mancala.statemachine.states.State;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.action.ActionListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.PseudoState;
import org.springframework.statemachine.state.StateListener;
import org.springframework.statemachine.transition.Transition;

import java.util.Collection;
import java.util.Optional;

@Configuration
@Log
public class SimpleStateMachineEventListener extends StateMachineListenerAdapter<State, Event> {
    @Override
    public void stateChanged(org.springframework.statemachine.state.State<State, Event> from, org.springframework.statemachine.state.State<State, Event> to) {
        super.stateChanged(from, to);
//        String message = "State changed from '%s' to '%s'";
//        if(from != null && to != null) {
//             log.info(String.format(message, from.getId(), to.getId()));
//        } else {
//            log.info(String.format(message, from, to));
//        }
    }


    @Override
    public void stateMachineStarted(StateMachine<State, Event> stateMachine) {
//        stateMachine.getTransitions().stream().forEach(transition -> {
//            log.info(String.format(
//                    "%s -> %s",
//                    transition.getSource().getId(),
//                    transition.getTarget().getId()
//            ));
//        });
    }

    @Override
    public void stateEntered(org.springframework.statemachine.state.State<State, Event> state) {
        super.stateEntered(state);
        log.info(String.format("State entered: %s: ", state.getId()));
    }

    @Override
    public void extendedStateChanged(Object key, Object value) {
        super.extendedStateChanged(key, value);
        log.info(String.format("Variable change: '%s' -> '%s'", key, value));
    }

    @Override
    public void eventNotAccepted(Message<Event> event) {
        super.eventNotAccepted(event);
        log.info(String.format("Event '%s' not accepted' ", event));
    }

    @Override
    public void stateMachineError(StateMachine<State, Event> stateMachine, Exception exception) {
        super.stateMachineError(stateMachine, exception);
        log.info(String.format("State machine exception: %s", exception.getMessage()));

    }
}
