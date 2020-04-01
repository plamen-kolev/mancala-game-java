package com.mancalagame.mancala.service;

import com.mancalagame.mancala.enums.GameState;
import com.mancalagame.mancala.exceptions.IllegalPlayerMoveException;
import com.mancalagame.mancala.model.PitDAO;
import com.mancalagame.mancala.enums.PitType;
import com.mancalagame.mancala.enums.Player;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.mancalagame.mancala.enums.Player.PLAYER1;
import static com.mancalagame.mancala.enums.Player.PLAYER2;

@Log
@Service
public class BoardService {

    private ArrayList<PitDAO> board;
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
                .filter(pit -> player.equals(pit.getOwner()))
                .map(pit -> clonePit(pit)
                ).collect(Collectors.toList());
    }

    public void reset() {
        initializeBoard();
    }

    public Player getWinner() {
        Map<Player,Integer> results = getResults();
        // from https://www.baeldung.com/java-find-map-max
        Optional<Map.Entry<Player, Integer>> maxEntry = results.entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue));

        return maxEntry.get().getKey();
    }

    public Map<Player,Integer> getResults() {
        return board.stream().filter(pit -> PitType.BIG.equals(pit.getType())).collect(Collectors.toMap(PitDAO::getOwner, PitDAO::getStones));
    }

    public GameState play(int pitId, Player currentPlayer) throws IllegalPlayerMoveException {
        int currentPitId = pitId;
        PitDAO pitToMove = validatePlayerMove(currentPitId, currentPlayer);
        int movesToMake = pitToMove.getStones();
        pitToMove.setStones(0);
        while (movesToMake != 0) {
            PitDAO currentPit = board.get((currentPitId + 1) % TOTAL_PIT_INDEXES);

            log.info(currentPit.toString());
            if(currentPit.getType() == PitType.BIG && currentPit.getOwner() != currentPlayer) {
            } else {
                currentPit.setStones(currentPit.getStones() + 1);
                --movesToMake;

                if(movesToMake == 0 && lastMoveIsInBigPit(currentPit, currentPlayer)) {
                    return GameState.EXTRA_TURN;
                }

            }

            currentPitId = ++currentPitId;
        }

        if (!this.hasStonesLeft(currentPlayer)) {
            return GameState.GAME_WON;
        }

        return GameState.NEXT_MOVE;

    }

    private boolean lastMoveIsInBigPit(PitDAO lastMovePit, Player currentPlayer) {
        return this.hasStonesLeft(currentPlayer) && PitType.BIG.equals(lastMovePit.getType());
    }

    private PitDAO validatePlayerMove(int pitId, Player current_player) throws IllegalPlayerMoveException {
        PitDAO pit;
        try {
            pit = board.get(pitId);

        } catch (IndexOutOfBoundsException exception) {
            throw (new IllegalPlayerMoveException(String.format("Trying to play pit with id '%s', but we couldn't find it", pitId)));
        }

        if (pit.getOwner() != current_player || PitType.BIG.equals(pit.getType())) {
            throw (new IllegalPlayerMoveException(String.format("You can't use pit with id '%s', this belongs to the other player or is the big pit", pit.toString())));
        }
        if(pit.getStones() == 0) {
            throw (new IllegalPlayerMoveException("Cannot pick from an empty pit"));
        }

        return pit;
    }

    public boolean hasStonesLeft(Player player) {
        return getBoard(player).stream()
                .filter(pit -> PitType.SMALL.equals(pit.getType()))
                .map(pit -> pit.getStones())
                .reduce(Integer::sum)
                .get()  > 0;

    }

    private PitDAO clonePit(PitDAO pit) {
        return PitDAO.builder()
                .stones(pit.getStones())
                .owner(pit.getOwner())
                .id(pit.getId())
                .type(pit.getType())
                .build();
    }

    private void initializeBoard() {
        board = new ArrayList<>();
        IntStream.range(0, NUMBER_OF_PITS_PER_PLAYER)
                .forEach( i -> board.add(
                        i,
                        PitDAO.builder()
                                .stones(INITIAL_NUMBER_OF_STONES_PER_SMALL_PIT)
                                .owner(PLAYER1)
                                .id(i)
                                .type(PitType.SMALL)
                                .build()
                ));
        board.add(6,
                PitDAO.builder()
                        .stones(INITIAL_STONES_IN_BIG_PIT)
                        .owner(PLAYER1)
                        .id(6)
                        .type(PitType.BIG)
                        .build()
        );

        IntStream.range(0, NUMBER_OF_PITS_PER_PLAYER)
                .forEach(
                        i -> board.add(
                                7+i,
                        PitDAO.builder()
                                .stones(INITIAL_NUMBER_OF_STONES_PER_SMALL_PIT)
                                .owner(PLAYER2)
                                .type(PitType.SMALL)
                                .id(7+i)
                                .build()
                ));

        board.add(
                13,
                PitDAO.builder()
                        .stones(INITIAL_STONES_IN_BIG_PIT)
                        .owner(PLAYER2)
                        .type(PitType.BIG)
                        .id(13)
                        .build()
        );
    }
}
