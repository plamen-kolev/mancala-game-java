package com.mancalagame.mancala.service;

import com.mancalagame.mancala.enums.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PlayerTurnServiceTest {

    private PlayerTurnService turnService;
    private static final Player PLAYER_WHO_PLAYS_FIRST = Player.PLAYER1;
    private static final Player PLAYER_WHO_PLAYS_SECOND = Player.PLAYER2;

    @BeforeEach
    public void setup() {
        turnService = new PlayerTurnService(PLAYER_WHO_PLAYS_FIRST);
    }

    @Test
    public void playerShouldHaveATurn() {
        assertTrue(turnService.hasTurn(PLAYER_WHO_PLAYS_FIRST));
        assertFalse(turnService.hasTurn(PLAYER_WHO_PLAYS_SECOND));
    }

    @Test
    public void shouldChangeTurn() {
        assertTrue(turnService.hasTurn(PLAYER_WHO_PLAYS_FIRST));
        turnService.changeTurn();

        assertFalse(turnService.hasTurn(PLAYER_WHO_PLAYS_FIRST));
        assertTrue(turnService.hasTurn(PLAYER_WHO_PLAYS_SECOND));
    }

    @Test
    public void shouldResetPlayerTurn() {
        turnService.changeTurn();
        turnService.reset();
        assertThat(turnService.getCurrentPlayer(), is(PLAYER_WHO_PLAYS_FIRST));
    }
}