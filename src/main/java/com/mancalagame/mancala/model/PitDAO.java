package com.mancalagame.mancala.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class PitDAO {
    private PitType type;
    private int id;
    private int numberOfStones;
    private Player ownership;
}

