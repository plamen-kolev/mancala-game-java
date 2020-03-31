package com.mancalagame.mancala.service;

import com.mancalagame.mancala.enums.GameState;
import com.mancalagame.mancala.exceptions.IllegalPlayerMoveException;
import com.mancalagame.mancala.model.PitDAO;
import com.mancalagame.mancala.enums.PitType;
import com.mancalagame.mancala.enums.Player;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.mancalagame.mancala.enums.Player.PLAYER1;
import static com.mancalagame.mancala.enums.Player.PLAYER2;

@Log
@Service
public class BoardService {

    private LinkedList<PitDAO> board;
    private static final int NUMBER_OF_PITS_PER_PLAYER = 6;
    private static final int INITIAL_NUMBER_OF_STONES_PER_SMALL_PIT = 6;
    private static final int INITIAL_STONES_IN_BIG_PIT = 0;
    private static final int TOTAL_PIT_INDEXES = 14;

    @Autowired
    public BoardService() {
        initializeBoard();
    }

    public List<PitDAO> getBoard(Player player) {
        return board.stream()
                .filter(pit -> player.equals(pit.getOwnership()))
                .map(pit ->
                        PitDAO.builder()
                                .numberOfStones(pit.getNumberOfStones())
                                .ownership(pit.getOwnership())
                                .id(pit.getId())
                                .type(pit.getType())
                                .build()
                ).collect(Collectors.toList());
    }

    public GameState play(int pitId, Player current_player) throws IllegalPlayerMoveException {
        int pitIndex = findIndexOfPit(pitId, current_player);
        PitDAO currentPit = board.get(pitIndex);

        int movesToMake = currentPit.getNumberOfStones();
        currentPit.setNumberOfStones(0);
        while (movesToMake != 0) {
            PitDAO nextPit = board.get((pitIndex + 1) % TOTAL_PIT_INDEXES);

            log.info(nextPit.toString());
            if(nextPit.getType() == PitType.BIG && currentPit.getOwnership() != current_player) {
            } else {
                nextPit.setNumberOfStones(nextPit.getNumberOfStones() + 1);
                --movesToMake;

                if(movesToMake == 0 && this.hasStonesLeft(current_player)) {
                    return GameState.EXTRA_TURN;
                }

            }

            pitIndex = ++pitIndex; // skip this pit if its the big pit of the opponent
        }

        if (!this.hasStonesLeft(current_player)) {
            return GameState.GAME_WON;
        }

        return GameState.NEXT_MOVE;

    }

    private int findIndexOfPit(int id, Player current_player) throws IllegalPlayerMoveException {

        for (int i = 0; i < board.size(); i++) {
            PitDAO pit = board.get(i);
            if (pit.getId() == id){
                if (pit.getOwnership() != current_player || PitType.BIG.equals(pit.getType())) {
                    throw (new IllegalPlayerMoveException(String.format("You can't use pit with id '%s', this belongs to the other player or is the big pit", id)));
                }
                if(pit.getNumberOfStones() == 0) {
                    throw (new IllegalPlayerMoveException("Cannot pick from an empty pit"));
                }
                return i;
            }
        }
        throw (new IllegalPlayerMoveException(String.format("Trying to play pit with id '%s', but we couldn't find it", id)));
    }

    public boolean hasStonesLeft(Player player) {
        return getBoard(player).stream()
                .filter(pit -> PitType.SMALL.equals(pit.getType()))
                .map(pit -> pit.getNumberOfStones())
                .reduce(Integer::sum)
                .get()  > 0;

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
                        .id(6)
                        .type(PitType.BIG)
                        .build()
        );

        IntStream.range(0, NUMBER_OF_PITS_PER_PLAYER)
                .forEach( i -> board.add(
                        PitDAO.builder()
                                .numberOfStones(INITIAL_NUMBER_OF_STONES_PER_SMALL_PIT)
                                .ownership(PLAYER2)
                                .type(PitType.SMALL)
                                .id(7+i)
                                .build()
                ));
        board.add(
                PitDAO.builder()
                        .numberOfStones(INITIAL_STONES_IN_BIG_PIT)
                        .ownership(PLAYER2)
                        .type(PitType.BIG)
                        .id(13)
                        .build()
        );

        // that will make the board wrap around
    }
}
