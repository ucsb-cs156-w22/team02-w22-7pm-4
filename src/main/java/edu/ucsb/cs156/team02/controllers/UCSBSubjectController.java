package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.UCSBSubject;
import edu.ucsb.cs156.team02.models.CurrentUser;
import edu.ucsb.cs156.team02.repositories.UCSBSubjectRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBSubject> allSubjects() {
        loggingService.logMethod();
        return subjectRepository.findAll();
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
            @ApiParam("inactive") @RequestParam Boolean inactive,
            @ApiParam("done") @RequestParam Boolean done) {
        loggingService.logMethod();

        UCSBSubject subject = new UCSBSubject();
        subject.setSubjectCode(subjectCode);
        subject.setSubjectTranslation(subjectTranslation);
        subject.setDeptCode(deptCode);
        subject.setCollegeCode(collegeCode);
        subject.setRelatedDeptCode(relatedDeptCode);
        subject.setInactive(inactive);
        subject.setDone(done);

        return subjectRepository.save(subject);
    }
}
