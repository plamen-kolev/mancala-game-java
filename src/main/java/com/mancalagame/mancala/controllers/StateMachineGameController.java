package com.mancalagame.mancala.controllers;

import com.mancalagame.mancala.enums.GameState;
import com.mancalagame.mancala.enums.Player;
import com.mancalagame.mancala.exceptions.IllegalPlayerMoveException;
import com.mancalagame.mancala.model.PitDTO;
import com.mancalagame.mancala.service.GameService;
import com.mancalagame.mancala.service.PlayerTurnService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping(value = "/state")
@Log
public class StateMachineGameController {

    private GameService gameService;
    private PlayerTurnService turnService;
    private StateMachine stateMachine;

    @Autowired
    public StateMachineGameController(StateMachine stateMachine, GameService gameService, PlayerTurnService turnService) {
        this.stateMachine = stateMachine;
        this.gameService = gameService;
        this.turnService = turnService;
    }

    @GetMapping
    public ModelAndView game() {

        List<PitDTO> p1board = gameService.getBoard(Player.PLAYER1);

        List<PitDTO> p2board = gameService.getBoard(Player.PLAYER2);
        Collections.reverse(p2board);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("game");
        modelAndView.getModel().put("p1board", p1board);
        modelAndView.getModel().put("p2board",  p2board);

        Player currentTurn = turnService.getCurrentPlayer();
        modelAndView.getModel().put("turn", currentTurn);
        log.info(String.format("Current player: %s", currentTurn));

        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String play(@RequestParam MultiValueMap body) throws IllegalPlayerMoveException {
        Object o = Objects.requireNonNull(body.getFirst("id"));
        int pitId = -1;
        if (o instanceof String) {
            try {
                pitId = Integer.parseInt((String) o);
            } catch (NumberFormatException e) {
                String errorMessage = String.format("Cannot make a move for %s", o.toString());
                throw new IllegalPlayerMoveException(errorMessage);
            }

        }

        GameState gameState = gameService.play(pitId);
        if(GameState.GAME_WON.equals(gameState)) {
            return "redirect:/score";
        }
        return "redirect:/";
    }
}
