package com.mancalagame.mancala.builders;

import com.mancalagame.mancala.enums.PitType;
import com.mancalagame.mancala.model.PitDTO;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.mancalagame.mancala.enums.Player.PLAYER1;
import static com.mancalagame.mancala.enums.Player.PLAYER2;

@Builder
public class BoardBuilder {

    private static final int NUMBER_OF_PITS_PER_PLAYER = 6;
    private static final int INITIAL_NUMBER_OF_STONES_PER_SMALL_PIT = 6;
    private static final int INITIAL_STONES_IN_BIG_PIT = 0;
    private static final int NEXT_PLAYER_OFFSET = 7;

    public static List<PitDTO> build() {
        ArrayList<PitDTO> board = new ArrayList<>();
        IntStream.range(0, NUMBER_OF_PITS_PER_PLAYER)
                .forEach( i -> board.add(
                        i,
                        PitDTO.builder()
                                .stones(INITIAL_NUMBER_OF_STONES_PER_SMALL_PIT)
                                .owner(PLAYER1)
                                .id(i)
                                .type(PitType.SMALL)
                                .build()
                ));
        board.add(6,
                PitDTO.builder()
                        .stones(INITIAL_STONES_IN_BIG_PIT)
                        .owner(PLAYER1)
                        .id(6)
                        .type(PitType.BIG)
                        .build()
        );

        IntStream.range(0, NUMBER_OF_PITS_PER_PLAYER)
                .forEach(
                        i -> board.add(
                                NEXT_PLAYER_OFFSET + i,
                                PitDTO.builder()
                                        .stones(INITIAL_NUMBER_OF_STONES_PER_SMALL_PIT)
                                        .owner(PLAYER2)
                                        .type(PitType.SMALL)
                                        .id(NEXT_PLAYER_OFFSET + i)
                                        .build()
                        ));

        board.add(
                13,
                PitDTO.builder()
                        .stones(INITIAL_STONES_IN_BIG_PIT)
                        .owner(PLAYER2)
                        .type(PitType.BIG)
                        .id(13)
                        .build()
        );
        return board;
    }
}
