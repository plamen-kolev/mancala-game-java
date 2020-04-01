package com.mancalagame.mancala.service;

import com.mancalagame.mancala.enums.GameState;
import com.mancalagame.mancala.exceptions.IllegalPlayerMoveException;
import com.mancalagame.mancala.model.PitDTO;
import com.mancalagame.mancala.enums.PitType;
import com.mancalagame.mancala.enums.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class BoardServiceTest {

    private BoardService boardService;
    private static final int INITIAL_STONES_PER_PIT = 6;
    private static final int PIT_ID_NOT_IN_DATASET = -1;
    private static final Player CURRENT_PLAYER = Player.PLAYER2;
    private static final Player OTHER_PLAYER = Player.PLAYER1;

    @BeforeEach
    public void setup() {
        boardService = new BoardService();
    }
    @Test
    public void shouldInitializeFieldOfPlayerOne() {
        List<PitDTO> pits = boardService.getBoard(CURRENT_PLAYER);
        assertEquals(pits.size(), 7);


        // Last pit is the big pit
        assertThat(pits.get(6).getType(), is(PitType.BIG));

        assertTrue(pits.stream().filter(pit -> pit.getType() != PitType.BIG).allMatch(pit -> pit.getStones() == INITIAL_STONES_PER_PIT));
        assertTrue(pits.stream().filter(pit -> pit.getType() == PitType.BIG).findFirst().get().getStones() == 0);
    }

    @Test
    public void shouldInitializeFieldOfPlayerTwo() {
        List<PitDTO> pits = boardService.getBoard(CURRENT_PLAYER);
        assertEquals(pits.size(), 7);

        // Last Pit is the big pit
        assertThat(pits.get(6).getType(), is(PitType.BIG));

        assertTrue(pits.stream().filter(pit -> pit.getType() != PitType.BIG).allMatch(pit -> pit.getStones() == INITIAL_STONES_PER_PIT));
        assertTrue(pits.stream().filter(pit -> pit.getType() == PitType.BIG).findFirst().get().getStones() == 0);
    }

    @Test
    public void playerShouldBeAbleToMakeAMove() throws IllegalPlayerMoveException {

        List<PitDTO> pits = boardService.getBoard(CURRENT_PLAYER);
        boardService.play(pits.get(0).getId(), CURRENT_PLAYER);
        List<PitDTO> updatedPits = boardService.getBoard(CURRENT_PLAYER);

        // first small pit is empty
        assertThat(updatedPits.get(0).getStones(), is(0));

        // rest have an extra stone in them (rest meaning 5)
        assertThat(updatedPits.stream().filter(pit -> pit.getType() == PitType.SMALL && pit.getStones() == 7).toArray().length, is(5));

        // and last pit has 1 stone
        PitDTO bigPitOfPlayer = updatedPits.stream().filter(pit -> pit.getType() == PitType.BIG).findFirst().get();
        assertThat(bigPitOfPlayer.getStones(), is(1));
    }

    @Test
    public void youCantStartPlayingWithOponentsItem() throws IllegalPlayerMoveException {

        List<PitDTO> pitsOfOtherPlayer = boardService.getBoard(OTHER_PLAYER);
        String expectedErrorMessage = "this belongs to the other player or is the big pit";

        try {
            boardService.play(pitsOfOtherPlayer.get(0).getId(), CURRENT_PLAYER);
            fail();
        } catch (IllegalPlayerMoveException e) {
            assertThat(e.getMessage(), containsString(expectedErrorMessage));
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
        PitDTO bigPit1 = boardService.getBoard(CURRENT_PLAYER).stream().filter(pit -> pit.getType() == PitType.BIG).findFirst().get();
        PitDTO bigPit2 = boardService.getBoard(OTHER_PLAYER).stream().filter(pit -> pit.getType() == PitType.BIG).findFirst().get();
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
        List<PitDTO> pits = boardService.getBoard(CURRENT_PLAYER);
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

    @Test
    public void playerCannotModifyTheBoard() {
        int modifiedValue = 1000;
        List<PitDTO> pits = boardService.getBoard(CURRENT_PLAYER);
        pits.get(0).setStones(modifiedValue);
        List<PitDTO> refetchedPits = boardService.getBoard(CURRENT_PLAYER);
        assertThat(refetchedPits.get(0).getStones(), not(modifiedValue));
    }

    @Test
    public void initiallyBothPlayersShouldHaveStones() {
        assertTrue(boardService.hasStonesLeft(CURRENT_PLAYER));
        assertTrue(boardService.hasStonesLeft(OTHER_PLAYER));
    }

    @Test
    public void whenOnePlayerHasMovedAllTheirStones_shouldReturnFalse() throws IllegalPlayerMoveException {
        List<PitDTO> pits = boardService.getBoard(CURRENT_PLAYER);
        GameState gameState = playGameToWin(CURRENT_PLAYER, pits);

        // also this wins the game
        assertFalse(boardService.hasStonesLeft(CURRENT_PLAYER));
        assertThat(gameState, is(GameState.GAME_WON));
    }

    @Test
    public void shouldGetResult() throws IllegalPlayerMoveException {
        List<PitDTO> pits = boardService.getBoard(CURRENT_PLAYER);
        boardService.play(pits.get(5).getId(), CURRENT_PLAYER);

        Map<Player, Integer> results = boardService.getResults();

        assertThat(results.get(Player.PLAYER2), is(1));
        assertThat(results.get(Player.PLAYER1), is(0));
    }

    @Test
    public void shouldGetWinner() throws IllegalPlayerMoveException {
        List<PitDTO> pits = boardService.getBoard(CURRENT_PLAYER);
        playGameToWin(CURRENT_PLAYER, pits);

        Player winner = boardService.getWinner();
        assertThat(winner, is(CURRENT_PLAYER));
    }

    @Test
    public void shouldNotGetWinner_ifNobodyHasEmptyBoard() throws IllegalPlayerMoveException {
        List<PitDTO> pits = boardService.getBoard(CURRENT_PLAYER);
        boardService.play(pits.get(0).getId(), CURRENT_PLAYER);

        Player winner = boardService.getWinner();
        assertThat(winner, is(Player.NOBODY));
    }

    @Test
    public void shouldResetGame() throws IllegalPlayerMoveException {
        List<PitDTO> pits = boardService.getBoard(CURRENT_PLAYER);
        boardService.play(pits.get(5).getId(), CURRENT_PLAYER);

        boardService.reset();

        List<PitDTO> restartedPits = boardService.getBoard(CURRENT_PLAYER);
        assertTrue(
                restartedPits.stream().filter(pit -> pit.getType() == PitType.SMALL).allMatch(pit -> pit.getStones() == 6)
        );

        assertTrue(
                restartedPits.stream().filter(pit -> pit.getType() == PitType.BIG).allMatch(pit -> pit.getStones() == 0)
        );
    }

    private GameState playGameToWin(Player player, List<PitDTO> pits) throws IllegalPlayerMoveException {
        boardService.play(pits.get(5).getId(), CURRENT_PLAYER);
        boardService.play(pits.get(4).getId(), CURRENT_PLAYER);
        boardService.play(pits.get(3).getId(), CURRENT_PLAYER);
        boardService.play(pits.get(2).getId(), CURRENT_PLAYER);
        boardService.play(pits.get(1).getId(), CURRENT_PLAYER);
        boardService.play(pits.get(0).getId(), CURRENT_PLAYER);

        // Brute force to win the game
        boardService.play(pits.get(1).getId(), CURRENT_PLAYER);
        boardService.play(pits.get(2).getId(), CURRENT_PLAYER);
        boardService.play(pits.get(5).getId(), CURRENT_PLAYER);
        boardService.play(pits.get(3).getId(), CURRENT_PLAYER);
        boardService.play(pits.get(4).getId(), CURRENT_PLAYER);
        return boardService.play(pits.get(5).getId(), CURRENT_PLAYER);

    }
}