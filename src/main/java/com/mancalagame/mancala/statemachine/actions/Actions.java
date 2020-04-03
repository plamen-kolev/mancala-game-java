package com.mancalagame.mancala.statemachine.actions;

import com.mancalagame.mancala.enums.GameState;
import com.mancalagame.mancala.enums.HeaderName;
import com.mancalagame.mancala.enums.Player;
import com.mancalagame.mancala.exceptions.IllegalPlayerMoveException;
import com.mancalagame.mancala.model.PitDTO;
import com.mancalagame.mancala.service.BoardService;
import com.mancalagame.mancala.service.PlayerTurnService;
import com.mancalagame.mancala.statemachine.events.Event;
import com.mancalagame.mancala.statemachine.states.State;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Log
public class Actions {

    private BoardService boardService;
    private PlayerTurnService turnService;

    @Autowired
    public Actions(BoardService boardService, PlayerTurnService turnService) {
        this.boardService = boardService;
        this.turnService = turnService;
    }

    public Action<State, Event> makeMove() {
        return context -> {
            int pitId = getPitId(context);
            try {
                Map stateVariables = context.getExtendedState().getVariables();
                GameState state = boardService.play(pitId, turnService.getCurrentPlayer());

                Map<Player, List<PitDTO>> board = boardService.getBoard();
                stateVariables.put(HeaderName.EXTRA_TURN, GameState.EXTRA_TURN.equals(state));
                stateVariables.put(HeaderName.BOARD, board);
                stateVariables.put(HeaderName.TURN, turnService.getCurrentPlayer());

            } catch (IllegalPlayerMoveException e) {
                log.severe(e.getMessage());
            }
        };
    }

    public Action<State, Event> endTurn() {
        return context -> {
            Map stateVariables = context.getExtendedState().getVariables();
            Player player = turnService.changeTurn();
            stateVariables.put(HeaderName.TURN, player);
        };
    }

    public Action<State, Event> endGame() {
        return context -> {
            context.getExtendedState().getVariables()
                    .put(HeaderName.GAME_STATE, GameState.GAME_WON);
        };
    }

    private int getPitId(StateContext<State, Event> context) {
        return (int) context.getMessage().getHeaders().get(HeaderName.PITID.name());
    }
}
