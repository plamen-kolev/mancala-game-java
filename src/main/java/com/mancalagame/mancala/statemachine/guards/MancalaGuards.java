package com.mancalagame.mancala.statemachine.guards;

import com.mancalagame.mancala.enums.HeaderName;
import com.mancalagame.mancala.service.BoardService;
import com.mancalagame.mancala.service.PlayerTurnService;
import com.mancalagame.mancala.statemachine.events.Event;
import com.mancalagame.mancala.statemachine.states.State;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
@Log
public class MancalaGuards {

    private BoardService boardService;
    private PlayerTurnService turnService;

    public MancalaGuards(BoardService boardService, PlayerTurnService turnService) {
        this.boardService = boardService;
        this.turnService = turnService;
    }

    public Guard<State, Event> pitNotEmpty() {
        return context -> {
            int pitToPlay = getPitId(context);
            return boardService.isPitEmpty(pitToPlay);
        };
    }

    public Guard<State, Event> isPlayerBoardEmpty() {
        return context -> !boardService.hasStonesLeft(turnService.getCurrentPlayer());
    }

    public Guard<State, Event> pitExists() {
        return context -> {
            int pitToPlay = getPitId(context);
            return boardService.doesPitExist(pitToPlay);
        };
    }

    public Guard<State, Event> isSmallPit() {
        return context -> {
            int pitToPlay = getPitId(context);
            return boardService.isSmallPit(pitToPlay);
        };
    }


    public Guard<State, Event> playerHasExtraTurn() {
        return context -> (boolean) context.getExtendedState().get(HeaderName.EXTRA_TURN, Boolean.class);
    }


    private int getPitId(StateContext<State, Event> context) {
        return (int) context.getMessage().getHeaders().get(HeaderName.PITID.name());
    }
}
