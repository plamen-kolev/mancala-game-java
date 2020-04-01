package com.mancalagame.mancala.service;

import com.mancalagame.mancala.builders.BoardBuilder;
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

@Log
@Service
public class BoardService {

    private List<PitDAO> board;

    @Autowired
    public BoardService() {
        board = BoardBuilder.build();
    }

    public List<PitDAO> getBoard(Player player) {
        return board.stream()
                .filter(pit -> player.equals(pit.getOwner()))
                .map(pit -> clonePit(pit)
                ).collect(Collectors.toList());
    }

    public void reset() {
        board = BoardBuilder.build();
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

        PitDAO pitToMove = validatePlayerMove(pitId, currentPlayer);
        int movesToMake = pitToMove.getStones();
        pitToMove.setStones(0);

        Iterator<PitDAO> iterator = board.listIterator(pitId + 1);

        while (movesToMake != 0) {
            if(!iterator.hasNext()) {
                iterator = board.listIterator(0);
            }

            PitDAO currentPit = iterator.next();

            log.info(currentPit.toString());
            if(currentPit.getType() == PitType.BIG && currentPit.getOwner() != currentPlayer) {
            } else {
                currentPit.setStones(currentPit.getStones() + 1);
                --movesToMake;

                if(movesToMake == 0 && lastMoveIsInBigPit(currentPit, currentPlayer)) {
                    return GameState.EXTRA_TURN;
                }
            }
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
}
