package com.mancalagame.mancala.service;

import com.mancalagame.mancala.enums.GameState;
import com.mancalagame.mancala.exceptions.IllegalPlayerMoveException;
import com.mancalagame.mancala.enums.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private PlayerTurnService turnService;
    private BoardService boardService;
    private boolean gameWon;

    @Autowired
    public GameService(PlayerTurnService turnService, BoardService boardService){
        this.turnService = turnService;
        this.boardService = boardService;
        this.gameWon = false;
    }

    public synchronized GameState play(int pitId) throws IllegalPlayerMoveException {
        if (gameWon) {
            return GameState.GAME_WON;
        }

        Player currentPlayer = turnService.getCurrentPlayer();
        GameState state = this.boardService.play(pitId, currentPlayer);
        if(!GameState.EXTRA_TURN.equals(state)) {
            this.turnService.changeTurn();
        }
        if(GameState.GAME_WON.equals(state)) {
            gameWon = true;
        }
        return state;
    }
}
