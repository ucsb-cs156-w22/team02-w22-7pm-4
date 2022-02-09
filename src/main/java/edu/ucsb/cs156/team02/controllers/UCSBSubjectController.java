package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.Todo;
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
     *
     * Don't need yet because we haven't written these methods
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
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBSubject> allSubjects() {
        loggingService.logMethod();
        return subjectRepository.findAll();
    }

    @ApiOperation(value = "Get a single subject")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<String> getSubjectByID(
            @ApiParam("id") @RequestParam Long id) throws JsonProcessingException {
        loggingService.logMethod();

        UCSBSubjectController.UCSBSubjectOrError soe = new UCSBSubjectController.UCSBSubjectOrError(id);

        soe = doesSubjectExist(soe);
        if (soe.error != null) {
            return soe.error;
        }

        String body = mapper.writeValueAsString(soe.subject);
        return ResponseEntity.ok().body(body);
    }

    @ApiOperation(value = "Create a new UCSBSubject")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBSubject postSubject(
            @ApiParam("subject code") @RequestParam String subjectCode,
            @ApiParam("subject translation") @RequestParam String subjectTranslation,
            @ApiParam("department code") @RequestParam String deptCode,
            @ApiParam("college code") @RequestParam String collegeCode,
            @ApiParam("related department code") @RequestParam String relatedDeptCode,
            @ApiParam("inactive") @RequestParam Boolean inactive)
            {
        loggingService.logMethod();

        UCSBSubject subject = new UCSBSubject();
        subject.setSubjectCode(subjectCode);
        subject.setSubjectTranslation(subjectTranslation);
        subject.setDeptCode(deptCode);
        subject.setCollegeCode(collegeCode);
        subject.setRelatedDeptCode(relatedDeptCode);
        subject.setInactive(inactive);

        return subjectRepository.save(subject);
    }

    @ApiOperation(value = "Delete a subject")
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("")
    public ResponseEntity<String> deleteUCSBSubject (
            @ApiParam("id") @RequestParam Long id) {
        loggingService.logMethod();

        UCSBSubjectOrError soe = new UCSBSubjectOrError(id);

        soe = doesSubjectExist(soe);
        if (soe.error != null) {
            return soe.error;
        }

        subjectRepository.deleteById(id);

        return ResponseEntity.ok().body(String.format("subject with id %d deleted", id));

    }

    @ApiOperation(value = "Update a single subject")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("")
    public ResponseEntity<String> putSubjectById(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid UCSBSubject incomingSubject) throws JsonProcessingException {
        loggingService.logMethod();

        UCSBSubjectOrError soe = new UCSBSubjectOrError(id);

        soe = doesSubjectExist(soe);
        if (soe.error != null) {
            return soe.error;
        }
        soe = doesSubjectExist(soe);
        if (soe.error != null) {
            return soe.error;
        }

        incomingSubject.setId(id);

        subjectRepository.save(incomingSubject);

        String body = mapper.writeValueAsString(incomingSubject);
        return ResponseEntity.ok().body(body);
    }

    public UCSBSubjectOrError doesSubjectExist(UCSBSubjectOrError soe) {

        Optional<UCSBSubject> optionalSubject = subjectRepository.findById(soe.id);

        if (optionalSubject.isEmpty()) {
            soe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("subject with id %d not found", soe.id));
        } else {
            soe.subject = optionalSubject.get();
        }
        return soe;
    }
}
