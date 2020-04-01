package com.mancalagame.mancala.service;

import com.mancalagame.mancala.enums.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerTurnService {

    private Player playerTurn;
    private static final Player PLAYER_WHO_STARTS = Player.PLAYER1;

    @Autowired
    public PlayerTurnService() {
        playerTurn = PLAYER_WHO_STARTS;
    }

    public PlayerTurnService(Player playerWhoGoesFirst) {
        this.playerTurn = playerWhoGoesFirst;
    }

    public boolean hasTurn(Player player) {
        return playerTurn == player;
    }

    public Player getCurrentPlayer() {
        return this.playerTurn;
    }

    public void reset() {
       this.playerTurn = PLAYER_WHO_STARTS;
    }

    public void changeTurn() {
        if (this.playerTurn == Player.PLAYER1) {
            this.playerTurn = Player.PLAYER2;
        } else {
            this.playerTurn = Player.PLAYER1;
        }
    }

}
