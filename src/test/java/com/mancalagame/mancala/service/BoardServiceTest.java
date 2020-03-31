package com.mancalagame.mancala.service;

import com.mancalagame.mancala.exceptions.IllegalPlayerMoveException;
import com.mancalagame.mancala.model.PitDAO;
import com.mancalagame.mancala.model.PitType;
import com.mancalagame.mancala.model.Players;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class BoardServiceTest {

    private BoardService boardService;
    private static final int INITIAL_STONES_PER_PIT = 6;
    private static final int PIT_ID_NOT_IN_DATASET = -1;
    private static final Players CURRENT_PLAYER = Players.PLAYER2;
    private static final Players OTHER_PLAYER = Players.PLAYER1;

    @BeforeEach
    public void setup() {
        boardService = new BoardService();
    }
    @Test
    public void shouldInitializeFieldOfPlayerOne() {
        List<PitDAO> pits = boardService.getBoard(CURRENT_PLAYER);
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
    public void playerShouldBeAbleToMakeAMove() throws IllegalPlayerMoveException {

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
    public void youCantStartPlayingWithOponentsItem() throws IllegalPlayerMoveException {

        List<PitDAO> pitsOfOtherPlayer = boardService.getBoard(OTHER_PLAYER);
        String expectedErrorMessage = "You can't use pit with id '0', this belongs to the other player";

        try {
            boardService.play(pitsOfOtherPlayer.get(0).getId(), CURRENT_PLAYER);
            fail();
        } catch (IllegalPlayerMoveException e) {
            assertThat(e.getMessage(), is(expectedErrorMessage));
        }
    }

    @Test
    public void willThrowExceptionWhenPitCantBeFound() throws IllegalPlayerMoveException {
        try {
            boardService.play(PIT_ID_NOT_IN_DATASET, CURRENT_PLAYER);
            fail();
        } catch ( IllegalPlayerMoveException e) {
            assertThat(e.getMessage(), is("Trying to play pit with id '-1', but we couldn't find it"));
        }
    }

    @Test
    public void youCantPickFromEitherBigPits() throws IllegalPlayerMoveException {
        PitDAO bigPit1 = boardService.getBoard(CURRENT_PLAYER).stream().filter(pit -> pit.getType() == PitType.BIG).findFirst().get();
        PitDAO bigPit2 = boardService.getBoard(OTHER_PLAYER).stream().filter(pit -> pit.getType() == PitType.BIG).findFirst().get();
        String expectedErrorMessage = "this belongs to the other player or is the big pit";
        try {
            boardService.play(bigPit1.getId(), CURRENT_PLAYER);
            fail("This test should have failed due to picking from big pit");
        } catch (IllegalPlayerMoveException e) {
            assertThat(e.getMessage(), containsString(expectedErrorMessage));
        }

        try {
            boardService.play(bigPit2.getId(), CURRENT_PLAYER);
            fail("This test should have failed due to picking from big pit");
        } catch (IllegalPlayerMoveException e) {
            assertThat(e.getMessage(), containsString(expectedErrorMessage));
        }
    }

    @Test
    public void playerCantPickFromEmptyPile() throws IllegalPlayerMoveException {
        List<PitDAO> pits = boardService.getBoard(CURRENT_PLAYER);
        boardService.play(pits.get(0).getId(), CURRENT_PLAYER);
        String expectedErrorMessage = "Cannot pick from an empty pit";
        // playing the same pit twice will cause the exception
        try {
            boardService.play(pits.get(0).getId(), CURRENT_PLAYER);
            fail("This test should have failed due to picking from empty pit");
        } catch (IllegalPlayerMoveException e){
            assertThat(e.getMessage(), containsString(expectedErrorMessage));
        }

    }
}