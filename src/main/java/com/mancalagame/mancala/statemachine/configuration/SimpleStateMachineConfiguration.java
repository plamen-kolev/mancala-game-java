package com.mancalagame.mancala.statemachine.configuration;

import com.mancalagame.mancala.enums.HeaderName;
import com.mancalagame.mancala.enums.Player;
import com.mancalagame.mancala.model.PitDTO;
import com.mancalagame.mancala.service.BoardService;
import com.mancalagame.mancala.statemachine.actions.Actions;
import com.mancalagame.mancala.statemachine.events.Event;
import com.mancalagame.mancala.statemachine.guards.MancalaGuards;
import com.mancalagame.mancala.statemachine.states.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableStateMachine
public class SimpleStateMachineConfiguration
        extends StateMachineConfigurerAdapter<State, Event> {


    private MancalaGuards guards;
    private Actions actions;
    private BoardService boardService;

    @Autowired
    public SimpleStateMachineConfiguration(
            BoardService boardService,
            Actions moveAction,
            MancalaGuards someGuard) {
        this.boardService = boardService;
        this.actions = moveAction;
        this.guards = someGuard;
    }

    @Override
    public void configure(StateMachineStateConfigurer<State, Event> states)
            throws Exception {

        states
                .withStates()
                .initial(State.PLAYER_1_TURN, context -> {
                    context.getExtendedState()
                            .getVariables()
                            .putAll(
                                    new HashMap<String, List<PitDTO>>() {{
                                        put(HeaderName.PLAYER_1_PITS.name(), boardService.getBoard(Player.PLAYER1));
                                        put(HeaderName.PLAYER_2_PITS.name(), boardService.getBoard(Player.PLAYER2));
                                    }}
                            );
                })
                .end(State.END)

                .state(State.PLAYER_1_TURN)
                .state(State.PLAYER_1_END_TURN)

                .state(State.PLAYER_2_TURN)
                .state(State.PLAYER_2_END_TURN)

                .choice(State.PLAYER_1_EMPTY_BOARD_CHOICE)
                .choice(State.PLAYER_1_GETS_EXTRA_TURN_CHOICE)

                .choice(State.PLAYER_2_EMPTY_BOARD_CHOICE)
                .choice(State.PLAYER_2_EMPTY_BOARD_CHOICE)
                ;
    }

    @Override
    public void configure(
            StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {

        transitions.withExternal()
                .source(State.PLAYER_1_TURN)
                .target(State.PLAYER_1_EMPTY_BOARD_CHOICE)
                .guard(guards.pitExists())
                .guard(guards.pitNotEmpty())
                .guard(guards.isSmallPit())
                .action(actions.makeMove())
                .event(Event.PLAY)

                .and()
                .withChoice()
                .source(State.PLAYER_1_EMPTY_BOARD_CHOICE)
                .first(State.END, guards.isPlayerBoardEmpty())
                .last(State.PLAYER_1_GETS_EXTRA_TURN_CHOICE)

                .and()
                .withChoice()
                .source(State.PLAYER_1_GETS_EXTRA_TURN_CHOICE)
                .first(State.PLAYER_1_TURN, guards.playerHasExtraTurn())
                .last(State.PLAYER_1_END_TURN)

                .and()
                .withLocal()
                .source(State.PLAYER_1_END_TURN)
                .target(State.PLAYER_2_TURN)
                .action(actions.endTurn())

                .source(State.PLAYER_2_TURN)
                .target(State.PLAYER_2_EMPTY_BOARD_CHOICE)
                .guard(guards.pitExists())
                .guard(guards.pitNotEmpty())
                .guard(guards.isSmallPit())
                .action(actions.makeMove())
                .event(Event.PLAY)

                .and()
                .withChoice()
                .source(State.PLAYER_2_EMPTY_BOARD_CHOICE)
                .first(State.END, guards.isPlayerBoardEmpty())
                .last(State.PLAYER_2_GETS_EXTRA_TURN_CHOICE)

                .and()
                .withChoice()
                .source(State.PLAYER_2_GETS_EXTRA_TURN_CHOICE)
                .first(State.PLAYER_2_TURN, guards.playerHasExtraTurn())
                .last(State.PLAYER_2_END_TURN)

                .and()
                .withLocal()
                .source(State.PLAYER_1_END_TURN)
                .target(State.PLAYER_2_TURN)
                .action(actions.endTurn())
        ;
    }
}