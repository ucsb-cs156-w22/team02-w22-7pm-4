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

    @ApiOperation(value = "List this user's UCSBRequirements")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBRequirement> thisUsersUCSBRequirement() {
        loggingService.logMethod();
        CurrentUser currentUser = getCurrentUser();
        Iterable<UCSBRequirement> ucsbRequirements = UCSBRequirementRepository.findAllByUserId(currentUser.getUser().getId());
        return ucsbRequirements;
    }

    @ApiOperation(value = "Create a new UCSBRequirement for the table")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    // im changing the parameters for a controller method
    public UCSBRequirement postUCSBRequirement(
            @ApiParam("id") @RequestParam long id,
            @ApiParam("courseCount") @RequestParam int courseCount,
            @ApiParam("units") @RequestParam int unit,
            @ApiParam("inactive") @RequestParam boolean inactive,
            @ApiParam("requirementCode") @RequestParam String requirementCode,
            @ApiParam("requirementTranslation") @RequestParam String requirementTranslation,
            @ApiParam("collegeCode") @RequestParam String collegeCode,
            @ApiParam("objCode") @RequestParam String objCode) {
        loggingService.logMethod();

        UCSBRequirement ucsbRequirement = new UCSBRequirement(id, courseCount, unit, inactive, requirementCode, requirementTranslation, collegeCode, objCode);
        UCSBRequirement savedUCSBRequirement = UCSBRequirementRepository.save(ucsbRequirement);
        return savedUCSBRequirement;
    }


}