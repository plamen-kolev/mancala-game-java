package com.mancalagame.mancala.service;

import com.mancalagame.mancala.enums.Player;
import org.springframework.stereotype.Service;

@Service
public class PlayerTurnService {
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
}
