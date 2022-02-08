package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.UCSBRequirement;
import edu.ucsb.cs156.team02.repositories.UCSBRequirementRepository;
import edu.ucsb.cs156.team02.entities.User;

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

@WebMvcTest(controllers = UCSBRequirementController.class)
@Import(TestConfig.class)
public class UCSBRequirementControllerTests extends ControllerTestCase {
    @MockBean
    UCSBRequirementRepository repo;
    @MockBean
    UserRepository userRepository;
    // all

     @Test
    public void api_requirements_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/UCSBRequirements/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_requirements_all__user_logged_in__returns_200() throws Exception {
        mockMvc.perform(get("/api/UCSBRequirements/all"))
                .andExpect(status().isOk());
    }

    // post

    @Test
    public void api_requirements_post__logged_out__returns_403() throws Exception {
        mockMvc.perform(post("/api/UCSBRequirements/post"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_requirements_all__user_logged_in__returns_all_requirements() throws Exception {

        // arrange

        UCSBRequirement req1 = UCSBRequirement.builder()
                .id(0L)
                .courseCount(1)
                .unit(2)
                .inactive(true)
                .requirementCode("Req 1")
                .requirementTranslation("Req 1")
                .collegeCode("Req 1")
                .objCode("Req 1")
                .build();
        UCSBRequirement req2 = UCSBRequirement.builder()
                .id(1L)
                .courseCount(2)
                .unit(3)
                .inactive(true)
                .requirementCode("Req 2")
                .requirementTranslation("Req 2")
                .collegeCode("Req 2")
                .objCode("Req 2")
                .build();

        ArrayList<UCSBRequirement> expectedReqs = new ArrayList<>();
        expectedReqs.addAll(Arrays.asList(req1, req2));
        when(repo.findAll()).thenReturn(expectedReqs);

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBRequirements/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(repo, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedReqs);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    } 

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_requirements_post__user_logged_in() throws Exception {
        // arrange

        UCSBRequirement expectedReq = UCSBRequirement.builder()
                .id(0L)
                .courseCount(2)
                .unit(3)
                .inactive(true)
                .requirementCode("Test")
                .requirementTranslation("Test")
                .collegeCode("Test")
                .objCode("Test")
                .build();

        when(repo.save(eq(expectedReq))).thenReturn(expectedReq);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/UCSBRequirements/post?id=0&courseCount=2&unit=3&inactive=true&requirementCode=Test&requirementTranslation=Test&collegeCode=Test&objCode=Test")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(repo, times(1)).save(expectedReq);
        String expectedJson = mapper.writeValueAsString(expectedReq);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }
}