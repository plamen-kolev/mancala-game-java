//package com.mancalagame.mancala;
//
//import com.mancalagame.mancala.enums.HeaderName;
//import com.mancalagame.mancala.statemachine.events.Event;
//import com.mancalagame.mancala.statemachine.states.State;
//import lombok.extern.java.Log;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.messaging.MessageHeaders;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.statemachine.StateMachine;
//import org.springframework.statemachine.transition.Transition;
//
//@Log
//@SpringBootApplication
//public class Runner implements CommandLineRunner {
//
//    @Autowired
//    StateMachine stateMachine;
//
//
////    public static void main(String[] args) {
////        SpringApplication.run(MancalaApplication.class, args);
////    }
//
//    @Override
//    public void run(String... args) {
//        stateMachine.sendEvent(MessageBuilder
//                .withPayload(Event.PLAY)
//                .setHeader(HeaderName.PITID.name(), 3)
//                .build()
//        );
//
//        log.info(stateMachine.getTransitions().toString());
//        for (Object t: stateMachine.getTransitions()) {
//            Transition<State, Event> transition = (Transition<State, Event>) t;
//            log.info(
//                    String.format("%s -> %s",
//                        transition.getSource().getId(),
//                        transition.getTarget().getId()
//                    )
//            );
//        }
//    }
//}
