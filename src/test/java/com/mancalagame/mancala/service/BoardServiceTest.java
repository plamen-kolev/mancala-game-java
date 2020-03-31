package com.mancalagame.mancala.service;

import com.mancalagame.mancala.exceptions.InvalidItemAccessException;
import com.mancalagame.mancala.model.PitDAO;
import com.mancalagame.mancala.model.PitType;
import com.mancalagame.mancala.model.Players;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardServiceTest {

    private BoardService boardService;
    private static final int INITIAL_STONES_PER_PIT = 6;

    @BeforeEach
    public void setup() {
        boardService = new BoardService();
    }
    @Test
    public void shouldInitializeFieldOfPlayerOne() {
        List<PitDAO> pits = boardService.getBoard(Players.PLAYER1);
        assertThat(pits.size()).isEqualTo(7);

        // Last pit is the big pit
        assertThat(pits.get(6).getType()).isEqualTo(PitType.BIG);

        assertThat(pits.stream().filter(pit -> pit.getType() != PitType.BIG).allMatch(pit -> pit.equals(INITIAL_STONES_PER_PIT)));
        assertThat(pits.stream().filter(pit -> pit.getType() == PitType.BIG).findFirst().get().getNumberOfStones() == 0);
    }

    @Test
    public void shouldInitializeFieldOfPlayerTwo() {
        List<PitDAO> pits = boardService.getBoard(Players.PLAYER2);
        assertThat(pits.size()).isEqualTo(7);

        // Last Pit is the big pit
        assertThat(pits.get(6).getType()).isEqualTo(PitType.BIG);

        assertThat(pits.stream().filter(pit -> pit.getType() != PitType.BIG).allMatch(pit -> pit.equals(INITIAL_STONES_PER_PIT)));
        assertThat(pits.stream().filter(pit -> pit.getType() == PitType.BIG).findFirst().get().getNumberOfStones() == 0);
    }

    @Test
    public void playerShouldBeAbleToMakeAMove() throws InvalidItemAccessException {

        Players current_player = Players.PLAYER2;

        List<PitDAO> pits = boardService.getBoard(current_player);
        boardService.play(pits.get(0).getUuid(), current_player);
        List<PitDAO> updatedPits = boardService.getBoard(current_player);

        // first small pit is empty
        assertThat(updatedPits.get(0).getNumberOfStones() == 0);

        // rest have an extra stone in them
        assertThat(updatedPits.stream().filter(pit -> pit.getType() == PitType.SMALL).allMatch(pit -> pit.getNumberOfStones() == 7));

        // and last pit has 1 stone
        assertThat(updatedPits.stream().filter(pit -> pit.getType() == PitType.BIG).allMatch(pit -> pit.getNumberOfStones() == 0));


    }
}