package com.mancalagame.mancala.service;

import com.mancalagame.mancala.exceptions.IllegalPlayerMoveException;
import com.mancalagame.mancala.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private PlayerTurnService turnService;
    private BoardService boardService;

    @Autowired
    public GameService(PlayerTurnService turnService, BoardService boardService){
        this.turnService = turnService;
        this.boardService = boardService;
    }

    public synchronized void play(int pitId) throws IllegalPlayerMoveException {
        Player currentPlayer = turnService.getCurrentPlayer();
        this.boardService.play(pitId, currentPlayer);
        this.turnService.changeTurn();
    }
}
