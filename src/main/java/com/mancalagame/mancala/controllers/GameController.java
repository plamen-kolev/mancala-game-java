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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
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

        Player currentTurn = turnService.getCurrentPlayer();
        modelAndView.getModel().put("turn", currentTurn);
        log.info(String.format("Current player: %s", currentTurn));

        return modelAndView;
    }

    @GetMapping("/score")
    public ModelAndView gameResults(Model model) {


        Map<Player, Integer> results = gameService.getResults();
        Player winnter = gameService.getWinner();

        ModelAndView modelAndView = new ModelAndView();

        String error = (String) model.asMap().get("error");

        modelAndView.getModel().put("results",  results);
        modelAndView.getModel().put("winner",  winnter);
        modelAndView.getModel().put("error",  error);

        gameService.reset();
        modelAndView.setViewName("result");
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String play(@RequestParam MultiValueMap body, RedirectAttributes attr) throws IllegalPlayerMoveException {
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

    @RequestMapping(method = RequestMethod.DELETE)
    public String reset() throws IllegalPlayerMoveException {
        gameService.reset();
        log.info("Resetting game state");
        return "redirect:/";
    }

    @ExceptionHandler(IllegalPlayerMoveException.class)
    public String handleCustomException(IllegalPlayerMoveException e,  RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("error", e.getMessage());
        log.severe(String.format("Error:  %s", e.getMessage()));
        return "redirect:/";

    }

}
