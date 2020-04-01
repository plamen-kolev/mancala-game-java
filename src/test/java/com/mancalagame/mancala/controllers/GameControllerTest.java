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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebAppConfiguration()
class GameControllerTest {

    private MockMvc mockMvc;
    private static final String GAME_URL = "/";
    private static final String GAME_SCORE_URL = "/score";

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

        mockMvc.perform(MockMvcRequestBuilders.get(GAME_URL))
                .andExpect(status().is(200))
                .andReturn();

        verify(gameService).getBoard(Player.PLAYER1);
        verify(gameService).getBoard(Player.PLAYER2);
        verify(turnService).getCurrentPlayer();
    }

    @Test
    public void onPostRequestShouldPlayTurn() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post(GAME_URL)
                        .param("id", Integer.toString(PIT_TO_PLAY))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(RESPONSE_STATUS_AFTER_REDIRECT))
                .andReturn();

        verify(gameService).play(PIT_TO_PLAY);
    }

    @Test
    public void shouldRedirectToScoreScreen_whenGameWon() throws Exception {
        when(gameService.play(PIT_TO_PLAY)).thenReturn(GameState.GAME_WON);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post(GAME_URL)
                        .param("id", Integer.toString(PIT_TO_PLAY))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(redirectedUrl(GAME_SCORE_URL))
                .andReturn();
    }

    @Test
    public void youCanSendDeleteRequestToResetTheGame() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .delete(GAME_URL));
        verify(gameService).reset();
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