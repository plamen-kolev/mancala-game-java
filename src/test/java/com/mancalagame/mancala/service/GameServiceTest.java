package com.mancalagame.mancala.service;

import com.mancalagame.mancala.enums.GameState;
import com.mancalagame.mancala.exceptions.IllegalPlayerMoveException;
import com.mancalagame.mancala.enums.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

class GameServiceTest {

    private GameService gameService;
    private BoardService boardService;
    private PlayerTurnService turnService;

    private static final Player CURRENT_PLAYER = Player.PLAYER2;
    private static final int SOME_PIT_ID = 5;

    @BeforeEach
    public void setup() {
        turnService = mock(PlayerTurnService.class);
        boardService = mock(BoardService.class);

        gameService = new GameService(turnService, boardService);

    }

    @Test
    public void shouldLoseTurnWhenPlayingTurn() throws IllegalPlayerMoveException {
        // given
        when(turnService.getCurrentPlayer()).thenReturn(CURRENT_PLAYER);

        // when
        gameService.play(SOME_PIT_ID);

        //then
        verify(turnService).changeTurn();
        verify(boardService).play(SOME_PIT_ID, CURRENT_PLAYER);
    }

    @Test
    public void dontLoseTurnWhenBoardServiceGivesYouExtraRound() throws IllegalPlayerMoveException {
        GameState expectedState = GameState.EXTRA_TURN;
        when(turnService.getCurrentPlayer()).thenReturn(CURRENT_PLAYER);
        when(boardService.play(SOME_PIT_ID, CURRENT_PLAYER)).thenReturn(expectedState);

        // when
        GameState nextState = gameService.play(SOME_PIT_ID);

        //then
        assertThat(nextState, is(expectedState));
        verify(turnService, times(0)).changeTurn();
    }
}