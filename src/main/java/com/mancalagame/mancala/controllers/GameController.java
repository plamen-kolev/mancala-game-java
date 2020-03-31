package com.mancalagame.mancala.controllers;

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

import java.util.Collections;
import java.util.List;

@Controller
@Log
@RequestMapping(value = "/game")
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

        ModelAndView mv = new ModelAndView();
        mv.setViewName("welcome");
        mv.getModel().put("p1board", p1board);
        mv.getModel().put("p2board",  p2board);

        mv.getModel().put("turn", turnService.getCurrentPlayer());
        log.info(String.format("Current player: %s", turnService.getCurrentPlayer()));

        return mv;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String play(@RequestParam MultiValueMap body) throws IllegalPlayerMoveException {
        log.info(body.toString());
        int pitId = Integer.parseInt((String) body.getFirst("id"));
        gameService.play(pitId);
        return "redirect:/game";
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public String reset() throws IllegalPlayerMoveException {
        gameService.reset();
        log.info("Resetting game state");
        return "redirect:/game";
    }
}
