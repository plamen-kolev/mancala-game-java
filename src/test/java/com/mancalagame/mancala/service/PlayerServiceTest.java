package com.mancalagame.mancala.service;

import com.mancalagame.mancala.model.PitDAO;
import com.mancalagame.mancala.model.PitType;
import com.mancalagame.mancala.model.Players;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerServiceTest {

    private BoardService boardService;
    private static final int INITIAL_STONES_PER_PIT = 6;

    @BeforeEach
    public void setup() {
        boardService = new BoardService();
    }
    @Test
    public void shouldHaveInitialisedTheField() {
        List<PitDAO> pits = boardService.getBoard(Players.PLAYER1);
        assertThat(pits.size()).isEqualTo(7);
        assertThat(pits.get(6).getType()).isEqualTo(PitType.BIG);

        assertThat(pits.stream().filter(pit -> pit.getType() != PitType.BIG).allMatch(pit -> pit.equals(INITIAL_STONES_PER_PIT)));
        assertThat(pits.stream().filter(pit -> pit.getType() == PitType.BIG).findFirst().get().getNumberOfStones() == 0);
    }
}