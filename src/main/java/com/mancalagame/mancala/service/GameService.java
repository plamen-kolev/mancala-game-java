package com.mancalagame.mancala.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private PlayerTurnService turnService;
    private BoardService boardService;

    @Autowired
    public GameService(PlayerTurnService playerTurnService, BoardService boardService){
        this.turnService = turnService;
        this.boardService = boardService;
    }
}
