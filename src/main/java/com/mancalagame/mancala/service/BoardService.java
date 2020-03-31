package com.mancalagame.mancala.service;

import com.mancalagame.mancala.exceptions.IllegalPlayerMoveException;
import com.mancalagame.mancala.exceptions.InvalidItemAccessException;
import com.mancalagame.mancala.model.PitDAO;
import com.mancalagame.mancala.model.PitType;
import com.mancalagame.mancala.model.Players;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
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
    private static final int TOTAL_PIT_INDEXES = 13;


    public BoardService() {
        initializeBoard();
    }

    public List<PitDAO> getBoard(Players player) {
        return board.stream().filter(pit -> player.equals(pit.getOwnership())).collect(Collectors.toList());
    }

    public void play(int pitId, Players current_player) throws InvalidItemAccessException, IllegalPlayerMoveException {
        int pitIndex = findIndexOfPit(pitId, current_player);

        PitDAO currentPit = board.get(pitIndex);

        int movesToMake = currentPit.getNumberOfStones();
        currentPit.setNumberOfStones(0);
        while (movesToMake != 0) {
            PitDAO nextPit = board.get((pitIndex + 1) % TOTAL_PIT_INDEXES);

            if(nextPit.getType() == PitType.BIG && currentPit.getOwnership() != current_player) {
            } else {
                nextPit.setNumberOfStones(nextPit.getNumberOfStones() + 1);
                --movesToMake;
            }
            System.out.println(nextPit);
            pitIndex = ++pitIndex; // skip this pit if its the big pit of the opponent
        }

    }

    private int findIndexOfPit(int id, Players current_player) throws InvalidItemAccessException, IllegalPlayerMoveException {

        for (int i = 0; i < board.size(); i++) {
            PitDAO pit = board.get(i);
            if (pit.getId() == id){
                if (pit.getOwnership() != current_player) {
                    throw (new IllegalPlayerMoveException(String.format("You can't use pit with id '%s', this belongs to the other player", id)));
                }
                return i;
            }
        }
        throw (new InvalidItemAccessException(String.format("Trying to play pit with id '%s', but we couldn't find it", id)));
    }

    private void initializeBoard() {
        board = new LinkedList<>();
        IntStream.range(0, NUMBER_OF_PITS_PER_PLAYER)
                .forEach( i -> board.add(
                        PitDAO.builder()
                                .numberOfStones(INITIAL_NUMBER_OF_STONES_PER_SMALL_PIT)
                                .ownership(PLAYER1)
                                .id(i)
                                .type(PitType.SMALL)
                                .build()
                ));
        board.add(
                PitDAO.builder()
                        .numberOfStones(INITIAL_STONES_IN_BIG_PIT)
                        .ownership(PLAYER1)
                        .id(7)
                        .type(PitType.BIG)
                        .build()
        );

        IntStream.range(0, NUMBER_OF_PITS_PER_PLAYER)
                .forEach( i -> board.add(
                        PitDAO.builder()
                                .numberOfStones(INITIAL_NUMBER_OF_STONES_PER_SMALL_PIT)
                                .ownership(PLAYER2)
                                .type(PitType.SMALL)
                                .id(8+i)
                                .build()
                ));
        board.add(
                PitDAO.builder()
                        .numberOfStones(INITIAL_STONES_IN_BIG_PIT)
                        .ownership(PLAYER2)
                        .type(PitType.BIG)
                        .id(14)
                        .build()
        );

        // that will make the board wrap around
    }
}
