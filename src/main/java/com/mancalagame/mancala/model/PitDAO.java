package com.mancalagame.mancala.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PitDAO {
    private PitType type;
    private int numberOfStones;
    private Players ownership;
}

