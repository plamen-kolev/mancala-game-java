package com.mancalagame.mancala.controllers;

import com.mancalagame.mancala.enums.Player;
import com.mancalagame.mancala.service.GameService;
import com.mancalagame.mancala.service.PlayerTurnService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


@ExtendWith(SpringExtension.class)
@WebAppConfiguration()
class GameControllerTest {

    private MockMvc mockMvc;
    private static final String GAME_URL = "/";

    private GameService gameService;
    private PlayerTurnService turnService;

    @BeforeEach
    private void setUp() {
        gameService = mock(GameService.class);
        turnService = mock(PlayerTurnService.class);

        mockMvc = MockMvcBuilders.standaloneSetup(new GameController(gameService, turnService))
                .build();
    }

    @Test
    public void onGetRequestShouldCallDataLayer() throws Exception {

        MvcResult mvcResult = (MvcResult) mockMvc.perform(MockMvcRequestBuilders.get(GAME_URL))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status, is(200));

        verify(gameService).getBoard(Player.PLAYER1);
        verify(gameService).getBoard(Player.PLAYER2);
        verify(turnService).getCurrentPlayer();
    }

    @Test
    public void onPostRequestShouldPlayTurn() throws Exception {
        int pitToPlay = 3;
        int responseStatusAfterRedirect = 302;

        MvcResult mvcResult = (MvcResult) mockMvc.perform(
                MockMvcRequestBuilders
                        .post(GAME_URL)
                        .param("id", Integer.toString(pitToPlay))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status, is(responseStatusAfterRedirect));

        verify(gameService).play(pitToPlay);
    }
}