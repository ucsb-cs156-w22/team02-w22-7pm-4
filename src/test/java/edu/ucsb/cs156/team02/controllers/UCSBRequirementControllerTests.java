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

    // get a single req

    @Test
    public void api_requirements__does_get() throws Exception
    {
        mockMvc.perform(get("/api/UCSBRequirements?id=0"))
            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_requirements_get__does_exist() throws Exception {

        // arrange

        User u = currentUserService.getCurrentUser().getUser();
        UCSBRequirement u1 = UCSBRequirement.builder()
                .id(7L)
                .courseCount(2)
                .unit(3)
                .inactive(true)
                .requirementCode("Test")
                .requirementTranslation("Test")
                .collegeCode("Test")
                .objCode("Test")
                .build();        
        when(UCSBRequirementRepository.findById(eq(7L))).thenReturn(Optional.of(u1));

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBRequirements?id=7"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(UCSBRequirementRepository, times(1)).findById(eq(7L));
        String expectedJson = mapper.writeValueAsString(u1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_requirements_get__does_not_exist() throws Exception {

        // arrange

        User u = currentUserService.getCurrentUser().getUser();

        when(UCSBRequirementRepository.findById(eq(7L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBRequirements?id=7"))
                .andExpect(status().isBadRequest()).andReturn();

        // assert

        verify(UCSBRequirementRepository, times(1)).findById(eq(7L));
        String responseString = response.getResponse().getContentAsString();
        assertEquals("requirement with id 7 not found", responseString);
    }

    // put 
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_requirements__user_logged_in__put() throws Exception {
        // arrange

        User u = currentUserService.getCurrentUser().getUser();
        UCSBRequirement u1 = UCSBRequirement.builder()
                .id(7L)
                .courseCount(2)
                .unit(3)
                .inactive(true)
                .requirementCode("Test")
                .requirementTranslation("Test")
                .collegeCode("Test")
                .objCode("Test")
                .build();                
        
        String requestBody = mapper.writeValueAsString(updatedTodo);
        String expectedReturn = mapper.writeValueAsString(correctTodo);

        when(todoRepository.findById(eq(7L))).thenReturn(Optional.of(todo1));

        // act
        MvcResult response = mockMvc.perform(
                put("/api/UCSBRequirements?id=67")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(todoRepository, times(1)).findById(67L);
        verify(todoRepository, times(1)).save(correctTodo); // should be saved with correct user
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedReturn, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_todos__user_logged_in__cannot_put_todo_that_does_not_exist() throws Exception {
        // arrange

        Todo updatedTodo = Todo.builder().title("New Title").details("New Details").done(true).id(67L).build();

        String requestBody = mapper.writeValueAsString(updatedTodo);

        when(todoRepository.findById(eq(67L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("/api/todos?id=67")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(todoRepository, times(1)).findById(67L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("todo with id 67 not found", responseString);
    }
}