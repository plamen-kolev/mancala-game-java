package com.mancalagame.mancala.statemachine.states;

public enum State {
    PLAYER_1_TURN,
    PLAYER_1_WINS_OR_GET_EXTRA_TURN_CHOICE,
    PLAYER_1_END_TURN,
    PLAYER_1_EMPTY_BOARD_CHOICE,

    PLAYER_2_TURN,
    PLAYER_2_END_TURN,
    PLAYER_2_WINS_OR_GET_EXTRA_TURN_CHOICE,

    END
}