package com.mancalagame.mancala.controllers;

import com.mancalagame.mancala.enums.Player;
import com.mancalagame.mancala.model.PitDAO;
import com.mancalagame.mancala.service.GameService;
import com.mancalagame.mancala.service.PlayerTurnService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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

        ModelAndView mv = new ModelAndView();
        mv.setViewName("welcome");
        mv.getModel().put("p1board", p1board);
        mv.getModel().put("p2board", p2board);

        mv.getModel().put("turn", turnService.getCurrentPlayer());
        log.info(String.format("Current player: %s", turnService.getCurrentPlayer()));

        return mv;
    }
}
