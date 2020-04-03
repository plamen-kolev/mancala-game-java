package com.mancalagame.mancala.service;

import com.mancalagame.mancala.builders.BoardBuilder;
import com.mancalagame.mancala.enums.GameState;
import com.mancalagame.mancala.exceptions.IllegalPlayerMoveException;
import com.mancalagame.mancala.model.PitDTO;
import com.mancalagame.mancala.enums.PitType;
import com.mancalagame.mancala.enums.Player;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Log
@Service
public class BoardService {

    private List<PitDTO> board;
    private boolean hasGameWon = false;

    private static final int INDEX_OF_FIRST_ELEMENT = 0;

    @Autowired
    public BoardService() {
        board = BoardBuilder.build();
    }

    public List<PitDTO> getBoard(Player player) {
        return board.stream()
                .filter(pit -> player.equals(pit.getOwner()))
                .map(pit -> clonePit(pit)
                ).collect(Collectors.toList());
    }

    public Map<Player, List<PitDTO>> getBoard() {
        return board.stream()
                .map(pit -> clonePit(pit)
                ).collect(Collectors.groupingBy(PitDTO::getOwner));
    }

    public void reset() {
        hasGameWon = false;
        board = BoardBuilder.build();
    }

    public Player getWinner() {
        if (!hasStonesLeft(Player.PLAYER1) || !hasStonesLeft(Player.PLAYER2) ) {

            Map<Player,Integer> results = getResults();
            // from https://www.baeldung.com/java-find-map-max
            Optional<Map.Entry<Player, Integer>> maxEntry = results.entrySet()
                    .stream()
                    .max(Comparator.comparing(Map.Entry::getValue));
            return maxEntry.get().getKey();
        }
        return Player.NOBODY;
    }

    public Map<Player,Integer> getResults() {
        return board.stream().filter(pit -> PitType.BIG.equals(pit.getType())).collect(Collectors.toMap(PitDTO::getOwner, PitDTO::getStones));
    }

    public GameState play(int pitId, Player currentPlayer) throws IllegalPlayerMoveException {
        if (hasGameWon) {
            log.info("Game has been won, not moving");
            return GameState.GAME_WON;
        }
        PitDTO startPit = validatePlayerMove(pitId, currentPlayer);

        int movesToMake = startPit.getStones();
        startPit.setStones(0);

        Iterator<PitDTO> iterator = board.listIterator(pitId + 1);
        while (movesToMake != 0) {
            if(!iterator.hasNext()) {
                iterator = board.listIterator(INDEX_OF_FIRST_ELEMENT);
            }

            PitDTO currentPit = iterator.next();
            if (isPuttingInOpponentBigPit(currentPit, currentPlayer)) {
                continue;
            } else {
                currentPit.setStones(currentPit.getStones() + 1);
                --movesToMake;
            }
            if(isLastMoveInBigPit(currentPit, currentPlayer, movesToMake)) {
                return GameState.EXTRA_TURN;
            }
        }

        if (!hasStonesLeft(currentPlayer)) {
            hasGameWon = true;
            return GameState.GAME_WON;
        }

        return GameState.NEXT_MOVE;

    }

    public boolean hasGameWon() {
        return hasGameWon;
    }

    public boolean hasStonesLeft(Player player) {
        return getBoard(player).stream()
                .filter(pit -> PitType.SMALL.equals(pit.getType()))
                .map(pit -> pit.getStones())
                .reduce(Integer::sum)
                .get() > 0;

    }


    //    USED FOR FINITE STATE MACHINE
    public boolean isPitEmpty(int pitId) {
        try {
            PitDTO pit = getPit(pitId);
            return pit.getStones() != 0;
        } catch (IllegalPlayerMoveException e) {
            return false;
        }
    }

    public boolean doesPitExist(int pitId) {
        try {
            getPit(pitId);
            return true;
        } catch (IllegalPlayerMoveException e) {
            return false;
        }
    }

    public boolean isSmallPit(int pitId) {
        try {
            PitDTO pit = getPit(pitId);
            return PitType.SMALL.equals(pit.getType());
        } catch (IllegalPlayerMoveException e) {
            return false;
        }
    }

    private boolean isLastMoveInBigPit(PitDTO lastMovePit, Player currentPlayer, int movesLeft) {
        return this.hasStonesLeft(currentPlayer)
                && PitType.BIG.equals(lastMovePit.getType())
                && lastMovePit.getOwner() == currentPlayer
                && movesLeft == 0;

    }

    private boolean isPuttingInOpponentBigPit(PitDTO pit, Player currentPlayer) {
        return PitType.BIG.equals(pit.getType()) && !pit.getOwner().equals(currentPlayer);
    }

    private PitDTO getPit(int pitId) throws IllegalPlayerMoveException {
        PitDTO pit;
        try {
            pit = board.get(pitId);
        } catch (IndexOutOfBoundsException exception) {
            throw (new IllegalPlayerMoveException(String.format("Trying to play pit with id '%s', but we couldn't find it", pitId)));
        }
        return pit;
    }

    public PitDTO validatePlayerMove(int pitId, Player current_player) throws IllegalPlayerMoveException {
        PitDTO pit = getPit(pitId);


        if (pit.getOwner() != current_player || PitType.BIG.equals(pit.getType())) {
            throw (new IllegalPlayerMoveException(String.format("You can't use pit with id '%s', this belongs to the other player or is the big pit", pit.toString())));
        }

        return pit;
    }

    private PitDTO clonePit(PitDTO pit) {
        return PitDTO.builder()
                .stones(pit.getStones())
                .owner(pit.getOwner())
                .id(pit.getId())
                .type(pit.getType())
                .build();
    }
}
