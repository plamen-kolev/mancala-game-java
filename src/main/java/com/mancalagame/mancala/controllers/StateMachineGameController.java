package com.mancalagame.mancala.controllers;

import com.mancalagame.mancala.enums.GameState;
import com.mancalagame.mancala.enums.HeaderName;
import com.mancalagame.mancala.enums.Player;
import com.mancalagame.mancala.exceptions.IllegalPlayerMoveException;
import com.mancalagame.mancala.model.PitDTO;
import com.mancalagame.mancala.service.GameService;
import com.mancalagame.mancala.service.PlayerTurnService;
import com.mancalagame.mancala.statemachine.events.Event;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.support.MessageBuilder;
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
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping(value = "/state")
@Log
public class StateMachineGameController {

    private StateMachine stateMachine;

    @Autowired
    public StateMachineGameController(StateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    @GetMapping
    public ModelAndView game() {

        Map stateMachineVariables = stateMachine.getExtendedState().getVariables();

        Map<Player, List<PitDTO>> board = (Map<Player, List<PitDTO>>) stateMachineVariables.get(HeaderName.BOARD);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("gameCopy");
        modelAndView.getModel().put("p1board", board.get(Player.PLAYER1));
        modelAndView.getModel().put("p2board",  board.get(Player.PLAYER2));

        Player currentTurn = (Player) stateMachineVariables.get(HeaderName.TURN);
        modelAndView.getModel().put("turn", currentTurn);
        log.info(String.format("Current player: %s", currentTurn));

        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String play(@RequestParam MultiValueMap body) throws IllegalPlayerMoveException {
        Map stateMachineVariables = stateMachine.getExtendedState().getVariables();

        Object o = Objects.requireNonNull(body.getFirst("id"));
        int pitId = -1;
        if (o instanceof String) {
            try {
                pitId = Integer.parseInt((String) o);
                stateMachine.sendEvent(MessageBuilder
                        .withPayload(Event.PLAY)
                        .setHeader(HeaderName.PITID.name(), pitId)
                        .build()
                );
            } catch (NumberFormatException e) {
                String errorMessage = String.format("Cannot make a move for %s", o.toString());
                throw new IllegalPlayerMoveException(errorMessage);
            }

        }

        if(GameState.GAME_WON.equals(stateMachineVariables.get(HeaderName.GAME_STATE))) {
            return "redirect:/score";
        }
        return "redirect:/state";
    }
}
