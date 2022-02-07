package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.UCSBSubject;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import edu.ucsb.cs156.team02.repositories.UCSBSubjectRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@Api(description = "UCSBSubjects")
@RequestMapping("/api/UCSBSubjects")
@RestController
@Slf4j
public class UCSBSubjectController extends ApiController {

    /**
     * This inner class helps us factor out some code for checking
     * whether subjects exist, and whether they belong to the current user,
     * along with the error messages pertaining to those situations. It
     * bundles together the state needed for those checks.
     */
    public class UCSBSubjectOrError {
        Long id;
        UCSBSubject subject;
        ResponseEntity<String> error;

        public UCSBSubjectOrError(Long id) {
            this.id = id;
        }
    }

    @Autowired
    UCSBSubjectRepository subjectRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "List all subjects")
    @GetMapping("/all")
    public Iterable<UCSBSubject> allSubjects() {
        loggingService.logMethod();
        return subjectRepository.findAll();
    }
}
