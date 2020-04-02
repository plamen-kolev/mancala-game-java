package com.mancalagame.mancala;

import com.mancalagame.mancala.statemachine.events.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;

@SpringBootApplication
public class Runner implements CommandLineRunner {

    @Autowired
    StateMachine stateMachine;


    public static void main(String[] args) {
        SpringApplication.run(MancalaApplication.class, args);
    }

    @Override
    public void run(String... args) {
        stateMachine.start();
        stateMachine.sendEvent(Event.PLAY);
        System.out.println(stateMachine.getState().getId());
    }
}
