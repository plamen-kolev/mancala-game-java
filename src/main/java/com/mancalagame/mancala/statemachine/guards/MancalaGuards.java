package com.mancalagame.mancala.statemachine.guards;

import com.mancalagame.mancala.enums.HeaderName;
import com.mancalagame.mancala.service.BoardService;
import com.mancalagame.mancala.statemachine.events.Event;
import com.mancalagame.mancala.statemachine.states.State;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
@Log
public class MancalaGuards {

    private BoardService boardService;

    public MancalaGuards(BoardService boardService) {
        this.boardService = boardService;
    }

    public Guard<State, Event> pitNotEmpty() {
        return new Guard<State, Event>() {

            @Override
            public boolean evaluate(StateContext<State, Event> context) {
                System.out.println("Guard is running");
                int pitToPlay = (int) context.getMessage().getHeaders().get(HeaderName.PITID.name());
                return boardService.isPitEmpty(pitToPlay);
            }
        };
    }
}
