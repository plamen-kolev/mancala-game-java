package com.mancalagame.mancala.service;

import com.mancalagame.mancala.exceptions.IllegalPlayerMoveException;
import com.mancalagame.mancala.exceptions.InvalidItemAccessException;
import com.mancalagame.mancala.model.PitDAO;
import com.mancalagame.mancala.model.PitType;
import com.mancalagame.mancala.model.Players;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.is;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BoardServiceTest {

    private BoardService boardService;
    private static final int INITIAL_STONES_PER_PIT = 6;
    private static final int PIT_ID_NOT_IN_DATASET = -1;
    private static final Players CURRENT_PLAYER = Players.PLAYER2;

    @BeforeEach
    public void setup() {
        boardService = new BoardService();
    }
    @Test
    public void shouldInitializeFieldOfPlayerOne() {
        List<PitDAO> pits = boardService.getBoard(Players.PLAYER1);
        assertEquals(pits.size(), 7);


        // Last pit is the big pit
        assertThat(pits.get(6).getType(), is(PitType.BIG));

        assertTrue(pits.stream().filter(pit -> pit.getType() != PitType.BIG).allMatch(pit -> pit.getNumberOfStones() == INITIAL_STONES_PER_PIT));
        assertTrue(pits.stream().filter(pit -> pit.getType() == PitType.BIG).findFirst().get().getNumberOfStones() == 0);
    }

    @Test
    public void shouldInitializeFieldOfPlayerTwo() {
        List<PitDAO> pits = boardService.getBoard(CURRENT_PLAYER);
        assertEquals(pits.size(), 7);

        // Last Pit is the big pit
        assertThat(pits.get(6).getType(), is(PitType.BIG));

        assertTrue(pits.stream().filter(pit -> pit.getType() != PitType.BIG).allMatch(pit -> pit.getNumberOfStones() == INITIAL_STONES_PER_PIT));
        assertTrue(pits.stream().filter(pit -> pit.getType() == PitType.BIG).findFirst().get().getNumberOfStones() == 0);
    }

    @Test
    public void playerShouldBeAbleToMakeAMove() throws InvalidItemAccessException, IllegalPlayerMoveException {

        List<PitDAO> pits = boardService.getBoard(CURRENT_PLAYER);
        boardService.play(pits.get(0).getId(), CURRENT_PLAYER);
        List<PitDAO> updatedPits = boardService.getBoard(CURRENT_PLAYER);

        // first small pit is empty
        assertThat(updatedPits.get(0).getNumberOfStones(), is(0));

        // rest have an extra stone in them (rest meaning 5)
        assertThat(updatedPits.stream().filter(pit -> pit.getType() == PitType.SMALL && pit.getNumberOfStones() == 7).toArray().length, is(5));

        // and last pit has 1 stone
        assertThat(updatedPits.stream().filter(pit -> pit.getType() == PitType.BIG && pit.getOwnership() == CURRENT_PLAYER).findFirst().get().getNumberOfStones(), is(0));
    }

    @Test
    public void youCantStartPlayingWithOponentsItem() throws InvalidItemAccessException {
        Players other_player = Players.PLAYER1;
        List<PitDAO> pitsOfOtherPlayer = boardService.getBoard(other_player);
        String expectedErrorMessage = "You can't use pit with id '0', this belongs to the other player";

        try {
            boardService.play(pitsOfOtherPlayer.get(0).getId(), CURRENT_PLAYER);
            fail();
        } catch (IllegalPlayerMoveException e) {
            assertThat(e.getMessage(), is(expectedErrorMessage));
        }
    }

    @Test
    public void willThrowExceptionWhenPitCantBeFound() {
        try {
            boardService.play(PIT_ID_NOT_IN_DATASET, CURRENT_PLAYER);
            fail();
        } catch (IllegalPlayerMoveException | InvalidItemAccessException e) {
            assertThat(e.getMessage(), is("Trying to play pit with id '-1', but we couldn't find it"));
        }
    }
}