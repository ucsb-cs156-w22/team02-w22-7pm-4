package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.UCSBRequirement;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import edu.ucsb.cs156.team02.repositories.UCSBRequirementRepository;
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

@Api(description = "UCSBRequirement")
@RequestMapping("/api/UCSBRequirements")
@RestController
@Slf4j
public class UCSBRequirementController extends ApiController {

    @Autowired
    UCSBRequirementRepository ucsbRequirementRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "List all UCSBRequirements")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBRequirement> getUCSBRequirement() {
        loggingService.logMethod();
        Iterable<UCSBRequirement> reqs = ucsbRequirementRepository.findAll();
        return reqs;
    }

    @ApiOperation(value = "Create a new UCSBRequirement for the table")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBRequirement postUCSBRequirement(
            @ApiParam("course count") @RequestParam int courseCount,
            @ApiParam("units") @RequestParam int unit,
            @ApiParam("inactive") @RequestParam Boolean inactive,
            @ApiParam("requirement code") @RequestParam String requirementCode,
            @ApiParam("requirement translation") @RequestParam String requirementTranslation,
            @ApiParam("college code") @RequestParam String collegeCode,
            @ApiParam("object code") @RequestParam String objCode) {
        loggingService.logMethod();

        UCSBRequirement req = new UCSBRequirement();
        req.setCourseCount(courseCount);
        req.setUnit(unit);
        req.setInactive(inactive);
        req.setRequirementCode(requirementCode);
        req.setRequirementTranslation(requirementTranslation);
        req.setCollegeCode(collegeCode);
        req.setObjCode(objCode);
        
        UCSBRequirement savedUCSBRequirement = ucsbRequirementRepository.save(req);
        return savedUCSBRequirement;
    }

    @ApiOperation(value = "Get a single UCSB Requirement")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<String> getUCSBRequirementById(
            @ApiParam("id") @RequestParam Long id) throws JsonProcessingException {
        loggingService.logMethod();

        Optional<UCSBRequirement> ucsbRequirement = ucsbRequirementRepository.findById(id);

        if (ucsbRequirement.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(String.format("id %d not found", id));
        }

        String body = mapper.writeValueAsString(ucsbRequirement);
        return ResponseEntity.ok().body(body);
    }

    @ApiOperation(value = "Update a UCSB requirement.")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("")
    public ResponseEntity<String> putUCSBRequirement(
        @ApiParam("id") @RequestParam Long id,
        @RequestBody @Valid UCSBRequirement newReq)
        throws JsonProcessingException
    {
        loggingService.logMethod();
        Optional<UCSBRequirement> ucsbRequirement = ucsbRequirementRepository.findById(id);

        if (ucsbRequirement.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(String.format("id %d not found", id));
        }
        newReq.setId(id);
        ucsbRequirementRepository.save(newReq);

        String body = mapper.writeValueAsString(newReq);
        return ResponseEntity.ok().body(body);
    }

    @ApiOperation(value = "Delete a UCSB requirement.")
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("")
    public ResponseEntity<String> deleteUCSBRequirement(
        @ApiParam("id") @RequestParam Long id)
    {
        loggingService.logMethod();

        Optional<UCSBRequirement> ucsbRequirement = ucsbRequirementRepository.findById(id);

        if (ucsbRequirement.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(String.format("record %d not found", id));
        }

        ucsbRequirementRepository.deleteById(id);
        return ResponseEntity.ok().body(String.format("record %d deleted", id));
    }

}