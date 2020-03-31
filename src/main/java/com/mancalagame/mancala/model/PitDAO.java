package com.mancalagame.mancala.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
public class PitDAO {
    private PitType type;
    private UUID uuid;
    private int numberOfStones;
    private Players ownership;
}

