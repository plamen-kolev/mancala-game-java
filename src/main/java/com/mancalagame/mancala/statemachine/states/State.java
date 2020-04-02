package com.mancalagame.mancala.statemachine.states;

public enum State {
    PLAYER_1_TURN,
    PLAYER_1_GETS_EXTRA_TURN_CHOICE,
    PLAYER_1_END_TURN,
    PLAYER_1_EMPTY_BOARD_CHOICE,

    PLAYER_2_TURN,
    PLAYER_2_END_TURN,
    PLAYER_2_EMPTY_BOARD_CHOICE,
    PLAYER_2_GETS_EXTRA_TURN_CHOICE,

    END
}