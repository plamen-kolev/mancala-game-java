package com.mancalagame.mancala.service;

import com.mancalagame.mancala.exceptions.IllegalPlayerMoveException;
import com.mancalagame.mancala.enums.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class GameServiceTest {

    private GameService gameService;
    private BoardService boardService;
    private PlayerTurnService turnService;

    private static final Player CURRENT_PLAYER = Player.PLAYER2;

    @BeforeEach
    public void setup() {
        turnService = mock(PlayerTurnService.class);
        boardService = mock(BoardService.class);

        gameService = new GameService(turnService, boardService);

    }

    @Test
    public void shouldLoseTurnWhenPlayingTurn() throws IllegalPlayerMoveException {
        // given
        int somePitId = 5;
        when(turnService.getCurrentPlayer()).thenReturn(CURRENT_PLAYER);

        // when
        gameService.play(somePitId);

        //then
        verify(turnService).changeTurn();
        verify(boardService).play(somePitId, CURRENT_PLAYER);
    }

    @Test
    public void whenLastStoneInBigPit_playerGetsAnotherRound() {
        assertTrue(false);
    }
}