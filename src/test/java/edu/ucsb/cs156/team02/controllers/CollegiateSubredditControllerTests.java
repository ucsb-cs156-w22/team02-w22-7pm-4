package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.CollegiateSubreddit;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.CollegiateSubredditRepository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = CollegiateSubredditController.class)
@Import(TestConfig.class)
public class CollegiateSubredditControllerTests extends ControllerTestCase {

    @MockBean
    CollegiateSubredditRepository collegiateSubredditRepository;

    @MockBean
    UserRepository userRepository;
     // Authorization tests for /api/collegiateSubreddits/all

     @Test
     public void api_todos_all__logged_out__returns_403() throws Exception {
         mockMvc.perform(get("/api/collegiateSubreddits/all"))
                 .andExpect(status().is(403));
     }
 
     @WithMockUser(roles = { "USER" })
     @Test
     public void api_todos_all__user_logged_in__returns_200() throws Exception {
         mockMvc.perform(get("/api/collegiateSubreddits/all"))
                 .andExpect(status().isOk());
     }

     // Authorization tests for /api/collegiateSubreddits/post

    @Test
    public void api_todos_post__logged_out__returns_403() throws Exception {
        mockMvc.perform(post("/api/collegiateSubreddits/post"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiateSubreddits_post__user_logged_in() throws Exception {
        // arrange

        //User u = currentUserService.getCurrentUser().getUser();

        CollegiateSubreddit expectedCollegiateSubreddit = CollegiateSubreddit.builder()
                .name("Test Name")
                .location("Test Location")
                .subreddit("Test Subreddit")
                //.done(true)
                //.user(u)
                .id(0L)
                .build();

        when(collegiateSubredditRepository.save(eq(expectedCollegiateSubreddit))).thenReturn(expectedCollegiateSubreddit);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/collegiateSubreddits/post?name=Test Name&location=Test Location&subreddit=Test Subreddit")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(collegiateSubredditRepository, times(1)).save(expectedCollegiateSubreddit);
        String expectedJson = mapper.writeValueAsString(expectedCollegiateSubreddit);
        //System.out.println("This is the expectedJson:" + expectedJson);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_user_all__user_logged_in__returns_all_subreddits() throws Exception {

        // arrange

        CollegiateSubreddit cs1 = CollegiateSubreddit.builder().name("Name 1").location("location 1").subreddit("subreddit 1").build();
        CollegiateSubreddit cs2 = CollegiateSubreddit.builder().name("Name 2").location("location 2").subreddit("subreddit 2").build();
        CollegiateSubreddit cs3 = CollegiateSubreddit.builder().name("Name 3").location("location 3").subreddit("subreddit 3").build();

        ArrayList<CollegiateSubreddit> expectedCollegiateSubreddit = new ArrayList<>();
        expectedCollegiateSubreddit.addAll(Arrays.asList(cs1, cs2, cs3));

        when(collegiateSubredditRepository.findAll()).thenReturn(expectedCollegiateSubreddit);

        // act
        MvcResult response = mockMvc.perform(get("/api/collegiateSubreddits/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(collegiateSubredditRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedCollegiateSubreddit);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api__user_logged_in__search_for_collegiateSubreddit_that_does_not_exist() throws Exception {

        // arrange

        //User u = currentUserService.getCurrentUser().getUser();

        when(collegiateSubredditRepository.findById(eq(7L))).thenReturn(Optional.empty());
        // act
        MvcResult response = mockMvc.perform(get("/api/collegiateSubreddits?id=7"))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(collegiateSubredditRepository, times(1)).findById(eq(7L));
        String responseString = response.getResponse().getContentAsString();
        assertEquals("CollegiateSubreddit with id 7 not found", responseString);
    } 

}
