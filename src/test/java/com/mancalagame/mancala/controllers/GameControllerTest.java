package com.mancalagame.mancala.controllers;

import com.mancalagame.mancala.enums.GameState;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;


@ExtendWith(SpringExtension.class)
@WebAppConfiguration()
class GameControllerTest {

    private MockMvc mockMvc;
    private static final String GAME_URL = "/";

    private GameService gameService;
    private PlayerTurnService turnService;

    private static final int PIT_TO_PLAY = 3;
    private static final int RESPONSE_STATUS_AFTER_REDIRECT = 302;


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

        MvcResult mvcResult = (MvcResult) mockMvc.perform(
                MockMvcRequestBuilders
                        .post(GAME_URL)
                        .param("id", Integer.toString(PIT_TO_PLAY))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status, is(RESPONSE_STATUS_AFTER_REDIRECT));

        verify(gameService).play(PIT_TO_PLAY);
    }

    @Test
    public void shouldRedirectToScoreScreen_whenGameWon() throws Exception {
        when(gameService.play(PIT_TO_PLAY)).thenReturn(GameState.GAME_WON);

        MvcResult mvcResult = (MvcResult) mockMvc.perform(
                MockMvcRequestBuilders
                        .post(GAME_URL)
                        .param("id", Integer.toString(PIT_TO_PLAY))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(redirectedUrl("/score"))
                .andReturn();
    }

    @Test
    public void youCanSendDeleteRequestToResetTheGame() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .delete(GAME_URL));
        verify(gameService).reset();
    }
}