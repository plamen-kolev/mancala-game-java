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
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
