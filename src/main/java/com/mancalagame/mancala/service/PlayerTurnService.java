package com.mancalagame.mancala.service;

import com.mancalagame.mancala.enums.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class PlayerTurnService {

    @Autowired
    public PlayerTurnService() {}

    private static Player playerTurn;

    public PlayerTurnService(Player playerWhoGoesFirst) {
        this.playerTurn = playerWhoGoesFirst;
    }

    public boolean hasTurn(Player player) {
        return playerTurn == player;
    }

    public Player getCurrentPlayer() {
        return this.playerTurn;
    }

    public void changeTurn() {
        if (this.playerTurn == Player.PLAYER1) {
            this.playerTurn = Player.PLAYER2;
        } else {
            this.playerTurn = Player.PLAYER1;
        }
    }

    @Bean
    private Player playerWhoGoesFirst() {
        return Player.PLAYER1;
    }
}
