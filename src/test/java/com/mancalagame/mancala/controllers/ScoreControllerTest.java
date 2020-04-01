package com.mancalagame.mancala.controllers;

import com.mancalagame.mancala.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration()
class ScoreControllerTest {

    private GameService gameService;
    private MockMvc mockMvc;

    private static final String GAME_SCORE_URL = "/score";

    @BeforeEach
    private void setUp() {
        gameService = mock(GameService.class);

        mockMvc = MockMvcBuilders.standaloneSetup(new ScoreController(gameService))
                .build();
    }

    @Test
    public void shouldFetchScoreAndHaveWinner() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GAME_SCORE_URL))
                .andExpect(status().is(200))
                .andReturn();

        verify(gameService).getResults();
        verify(gameService).getWinner();
        verify(gameService).reset();
    }
}