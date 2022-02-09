package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.Todo;
import edu.ucsb.cs156.team02.repositories.TodoRepository;
import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.UCSBSubject;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.UCSBSubjectRepository;

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

@WebMvcTest(controllers = UCSBSubjectController.class)
@Import(TestConfig.class)
class UCSBSubjectControllerTest extends ControllerTestCase {

    @MockBean
    UCSBSubjectRepository subjectRepository;

    @MockBean
    UserRepository userRepository;

    // Authorization tests for /api/UCSBSubjects/all

    @Test
    public void api_UCSBSubjects_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/UCSBSubjects/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubjects_all__user_logged_in__returns_200() throws Exception {
        mockMvc.perform(get("/api/UCSBSubjects/all"))
                .andExpect(status().isOk());
    }

    // Authorization tests for /api/UCSBSubjects/post

    @Test
    public void api_UCSBSubjects_post__logged_out__returns_403() throws Exception {
        mockMvc.perform(post("/api/UCSBSubjects/post"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubjects_all__user_logged_in__returns_all_subjects() throws Exception {

        // arrange

        UCSBSubject subject1 = UCSBSubject.builder().subjectCode("Subject 1").subjectTranslation("Translation 1").deptCode("Dept code 1").collegeCode("College code 1").relatedDeptCode("Related dept code 1").inactive(false).id(1L).build();
        UCSBSubject subject2 = UCSBSubject.builder().subjectCode("Subject 2").subjectTranslation("Translation 2").deptCode("Dept code 2").collegeCode("College code 2").relatedDeptCode("Related dept code 2").inactive(false).id(1L).build();
        UCSBSubject subject3 = UCSBSubject.builder().subjectCode("Subject 3").subjectTranslation("Translation 3").deptCode("Dept code 3").collegeCode("College code 3").relatedDeptCode("Related dept code 3").inactive(false).id(1L).build();

        ArrayList<UCSBSubject> expectedSubjects = new ArrayList<>(Arrays.asList(subject1, subject2, subject3));

        when(subjectRepository.findAll()).thenReturn(expectedSubjects);

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBSubjects/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(subjectRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedSubjects);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubjects_post__user_logged_in() throws Exception {
        // arrange

        UCSBSubject expectedSubject = UCSBSubject.builder()
                .subjectCode("Test Code")
                .subjectTranslation("Test Translation")
                .deptCode("Test dept code")
                .collegeCode("Test college code")
                .relatedDeptCode("Related dept code")
                .inactive(false)
                .id(0L)
                .build();

        when(subjectRepository.save(eq(expectedSubject))).thenReturn(expectedSubject);

        // act
        MvcResult response = mockMvc.perform(
                        post("/api/UCSBSubjects/post?subjectCode=Test Code&subjectTranslation=Test Translation&deptCode=Test dept code&collegeCode=Test college code&relatedDeptCode=Related dept code&inactive=false")
                                .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(subjectRepository, times(1)).save(expectedSubject);
        String expectedJson = mapper.writeValueAsString(expectedSubject);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubjects__logged_in__delete_subject() throws Exception {
        // arrange

        //User otherUser = User.builder().id(98L).build();
        UCSBSubject subject = UCSBSubject.builder()
                .subjectCode("Test Code")
                .subjectTranslation("Test Translation")
                .deptCode("Test dept code")
                .collegeCode("Test college code")
                .relatedDeptCode("Related dept code")
                .inactive(false)
                .id(16L)
                .build();
        when(subjectRepository.findById(eq(16L))).thenReturn(Optional.of(subject));

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/UCSBSubjects?id=16")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(subjectRepository, times(1)).findById(16L);
        verify(subjectRepository, times(1)).deleteById(16L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("subject with id 16 deleted", responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubjects__logged_in__delete_subject_that_does_not_exist() throws Exception {
        // arrange

        //User otherUser = User.builder().id(98L).build();
        //UCSBSubject todo1 = UCSBSubject.builder().title("UCSBSubject").details("UCSBSubject").done(false).user(otherUser).id(15L).build();
        UCSBSubject subject = UCSBSubject.builder()
                .subjectCode("Test Code")
                .subjectTranslation("Test Translation")
                .deptCode("Test dept code")
                .collegeCode("Test college code")
                .relatedDeptCode("Related dept code")
                .inactive(false)
                .id(16L)
                .build();
        when(subjectRepository.findById(eq(16L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/UCSBSubjects?id=16")
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(subjectRepository, times(1)).findById(16L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("subject with id 16 not found", responseString);
    }
}