package com.mancalagame.mancala.model;

import com.mancalagame.mancala.enums.PitType;
import com.mancalagame.mancala.enums.Player;
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
    private int stones;
    private Player owner;
}

