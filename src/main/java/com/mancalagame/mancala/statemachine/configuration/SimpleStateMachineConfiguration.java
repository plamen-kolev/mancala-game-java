package com.mancalagame.mancala.statemachine.configuration;

import com.mancalagame.mancala.enums.GameState;
import com.mancalagame.mancala.enums.HeaderName;
import com.mancalagame.mancala.service.BoardService;
import com.mancalagame.mancala.service.PlayerTurnService;
import com.mancalagame.mancala.statemachine.actions.Actions;
import com.mancalagame.mancala.statemachine.events.Event;
import com.mancalagame.mancala.statemachine.guards.Gurads;
import com.mancalagame.mancala.statemachine.states.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.*;

import java.util.EnumSet;
import java.util.Map;

@Configuration
@EnableStateMachine
public class SimpleStateMachineConfiguration
        extends StateMachineConfigurerAdapter<State, Event> {

    private SimpleStateMachineEventListener stateMachineEventListener;
    private Gurads compositeGuard;
    private Actions actions;
    private BoardService boardService;
    private PlayerTurnService turnService;

    @Autowired
    public SimpleStateMachineConfiguration(
            SimpleStateMachineEventListener stateMachineEventListener,
            BoardService boardService,
            PlayerTurnService turnService,
            Actions moveAction,
            Gurads someGuard) {
        this.stateMachineEventListener = stateMachineEventListener;
        this.boardService = boardService;
        this.turnService = turnService;
        this.actions = moveAction;
        this.compositeGuard = someGuard;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<State, Event> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true)
                .listener(stateMachineEventListener);
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
                                    Map.of(
                                            HeaderName.BOARD, boardService.getBoard(),
                                            HeaderName.TURN, turnService.getCurrentPlayer(),
                                            HeaderName.GAME_STATE, GameState.NEXT_MOVE
                                    )
                            );
                })
                .end(State.END)

                .states(EnumSet.allOf(State.class))

                .choice(State.PLAYER_1_WINS_OR_GET_EXTRA_TURN_CHOICE)
                .choice(State.PLAYER_2_WINS_OR_GET_EXTRA_TURN_CHOICE)
        ;
    }

    @Override
    public void configure(
            StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {

        transitions
                .withExternal()
                    .source(State.PLAYER_1_TURN)
                    .target(State.PLAYER_1_WINS_OR_GET_EXTRA_TURN_CHOICE)
                    .guard(compositeGuard.compose(
                            compositeGuard.pitExists(),
                            compositeGuard.pitNotEmpty(),
                            compositeGuard.isSmallPit()
                    ))
                    .action(actions.makeMove())
                    .event(Event.PLAY)

                .and()
                .withChoice()
                    .source(State.PLAYER_1_WINS_OR_GET_EXTRA_TURN_CHOICE)
                    .first(State.END, compositeGuard.isPlayerBoardEmpty())
                    .then(State.PLAYER_1_TURN, compositeGuard.playerHasExtraTurn())
                    .last(State.PLAYER_1_END_TURN)

                .and()
                .withExternal()
                    .source(State.PLAYER_1_END_TURN)
                    .target(State.PLAYER_2_TURN)
                    .action(actions.endTurn())

                .and()
                .withExternal()
                    .source(State.PLAYER_2_TURN)
                    .target(State.PLAYER_2_WINS_OR_GET_EXTRA_TURN_CHOICE)
                    .guard(compositeGuard.compose(
                            compositeGuard.pitExists(),
                            compositeGuard.pitNotEmpty(),
                            compositeGuard.isSmallPit()
                    ))
                    .action(actions.makeMove())
                    .event(Event.PLAY)

                .and()
                .withChoice()
                    .source(State.PLAYER_2_WINS_OR_GET_EXTRA_TURN_CHOICE)
                    .first(State.END, compositeGuard.isPlayerBoardEmpty())
                    .then(State.PLAYER_2_TURN, compositeGuard.playerHasExtraTurn())
                    .last(State.PLAYER_2_END_TURN)

                .and()
                .withExternal()
                    .source(State.PLAYER_2_END_TURN)
                    .target(State.PLAYER_1_TURN)
                    .action(actions.endTurn())

                .and()
                .withInternal()
                    .source(State.END)
                    .action(actions.endGame())

        ;
    }
}