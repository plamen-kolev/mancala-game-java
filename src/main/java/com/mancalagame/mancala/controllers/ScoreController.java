package com.mancalagame.mancala.controllers;

import com.mancalagame.mancala.enums.Player;
import com.mancalagame.mancala.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
public class ScoreController {

    private GameService gameService;

    @Autowired
    public ScoreController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/score")
    public ModelAndView score() {

        Map<Player, Integer> results = gameService.getResults();
        Player winnter = gameService.getWinner();
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.getModel().put("results",  results);

        if (!Player.NOBODY.equals(winnter)) {
            gameService.reset();
        }

        modelAndView.getModel().put("winner",  winnter);
        modelAndView.setViewName("result");
        return modelAndView;
    }
}
