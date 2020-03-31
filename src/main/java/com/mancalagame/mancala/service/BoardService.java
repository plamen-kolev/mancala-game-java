package com.mancalagame.mancala.service;

import com.mancalagame.mancala.model.PitDAO;
import com.mancalagame.mancala.model.PitType;
import com.mancalagame.mancala.model.Players;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.mancalagame.mancala.model.Players.PLAYER1;
import static com.mancalagame.mancala.model.Players.PLAYER2;

@Service
public class BoardService {
    private LinkedList<PitDAO> board;
    private static final int NUMBER_OF_PITS_PER_PLAYER = 6;
    private static final int INITIAL_NUMBER_OF_STONES_PER_SMALL_PIT = 6;
    private static final int INITIAL_STONES_IN_BIG_PIT = 0;


    public BoardService() {
        initializeBoard();
    }

    public List<PitDAO> getBoard(Players player) {
        return board.stream().filter(pit -> player.equals(pit.getOwnership())).collect(Collectors.toList());
    }

    private void initializeBoard() {
        board = new LinkedList<>();
        IntStream.range(0, NUMBER_OF_PITS_PER_PLAYER)
                .forEach( i -> board.add(
                        PitDAO.builder()
                                .numberOfStones(INITIAL_NUMBER_OF_STONES_PER_SMALL_PIT)
                                .ownership(PLAYER1)
                                .type(PitType.SMALL)
                                .build()
                ));
        board.add(
                PitDAO.builder()
                        .numberOfStones(INITIAL_STONES_IN_BIG_PIT)
                        .ownership(PLAYER1)
                        .type(PitType.BIG)
                        .build()
        );

        IntStream.range(0, NUMBER_OF_PITS_PER_PLAYER)
                .forEach( i -> board.add(
                        PitDAO.builder()
                                .numberOfStones(INITIAL_NUMBER_OF_STONES_PER_SMALL_PIT)
                                .ownership(PLAYER2)
                                .type(PitType.SMALL)
                                .build()
                ));
        board.add(
                PitDAO.builder()
                        .numberOfStones(INITIAL_STONES_IN_BIG_PIT)
                        .ownership(PLAYER2)
                        .type(PitType.BIG)
                        .build()
        );
    }
}
