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

    @ApiOperation(value = "Create a new UCSBRequirement")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBRequirement postUCSBRequirement(
            @ApiParam("title") @RequestParam String title,
            @ApiParam("details") @RequestParam String details,
            @ApiParam("done") @RequestParam Boolean done) {
        loggingService.logMethod();
        CurrentUser currentUser = getCurrentUser();
        log.info("currentUser={}", currentUser);

        UCSBRequirement ucsbRequirement = new UCSBRequirement();
        ucsbRequirement.setUser(currentUser.getUser());
        ucsbRequirement.setTitle(title);
        ucsbRequirement.setDetails(details);
        ucsbRequirement.setDone(done);
        UCSBRequirement savedUCSBRequirement = ucsbRequirementRepository.save(ucsbRequirement);
        return savedUCSBRequirement;
    }


}