package com.mancalagame.mancala.controllers;

import com.mancalagame.mancala.enums.GameState;
import com.mancalagame.mancala.enums.Player;
import com.mancalagame.mancala.exceptions.IllegalPlayerMoveException;
import com.mancalagame.mancala.model.PitDAO;
import com.mancalagame.mancala.service.GameService;
import com.mancalagame.mancala.service.PlayerTurnService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.expression.Lists;

import java.util.*;

@Controller
@Log
@RequestMapping(value = "/")
public class GameController {

    private GameService gameService;
    private PlayerTurnService turnService;

    @Autowired
    public GameController(GameService gameService, PlayerTurnService turnService) {
        this.gameService = gameService;
        this.turnService = turnService;
    }

    @GetMapping
    public ModelAndView game() {

        List<PitDAO> p1board = gameService.getBoard(Player.PLAYER1);

        List<PitDAO> p2board = gameService.getBoard(Player.PLAYER2);
        Collections.reverse(p2board);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("game");
        modelAndView.getModel().put("p1board", p1board);
        modelAndView.getModel().put("p2board",  p2board);

        modelAndView.getModel().put("turn", turnService.getCurrentPlayer());
        log.info(String.format("Current player: %s", turnService.getCurrentPlayer()));

        return modelAndView;
    }

    @GetMapping("/score")
    public ModelAndView gameResults() {

        Map<Player, Integer> results = gameService.getResults();
        Player winnter = gameService.getWinner();

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.getModel().put("results",  results);
        modelAndView.getModel().put("winner",  winnter);

        gameService.reset();
        modelAndView.setViewName("result");
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String play(@RequestParam MultiValueMap body) throws IllegalPlayerMoveException {
        log.info(body.toString());
        int pitId = Integer.parseInt((String) body.getFirst("id"));
        GameState gameState = gameService.play(pitId);
        if(GameState.GAME_WON.equals(gameState)) {
            return "redirect:/score";
        }
        return "redirect:/";
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public String reset() throws IllegalPlayerMoveException {
        gameService.reset();
        log.info("Resetting game state");
        return "redirect:/";
    }

    @ExceptionHandler(IllegalPlayerMoveException.class)
    public String handleCustomException(IllegalPlayerMoveException ex) {

        ModelAndView model = new ModelAndView("welcome");
        model.addObject("errCode", ex.getMessage());
        log.severe(String.format("Error: ", ex.getMessage()));
        return "redirect:/";

    }

}
