package com.mancalagame.mancala.service;

import com.mancalagame.mancala.enums.GameState;
import com.mancalagame.mancala.exceptions.IllegalPlayerMoveException;
import com.mancalagame.mancala.enums.Player;
import com.mancalagame.mancala.model.PitDTO;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Log
public class GameService {

    private PlayerTurnService turnService;
    private BoardService boardService;

    @Autowired
    public GameService(PlayerTurnService turnService, BoardService boardService){
        this.boardService = boardService;
        this.turnService = turnService;
    }

    public List<PitDTO> getBoard(Player player) {
        return boardService.getBoard(player);
    }

    public Map<Player, Integer> getResults() {
        return boardService.getResults();
    }

    public Player getWinner() {
        return boardService.getWinner();
    }

    public void reset() {
        boardService.reset();
        turnService.reset();
    }
    public synchronized GameState play(int pitId) throws IllegalPlayerMoveException {
        Player currentPlayer = turnService.getCurrentPlayer();
        GameState state = boardService.play(pitId, currentPlayer);
        log.info(String.format("Game state: %s", state));
        if(GameState.NEXT_MOVE.equals(state)) {
            turnService.changeTurn();
        }

        return state;
    }
}
