package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.CollegiateSubreddit;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import edu.ucsb.cs156.team02.repositories.CollegiateSubredditRepository;
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



@Api(description = "Gets and posts information to and from collegiateSubreddits")
@RequestMapping("/api/collegiateSubreddits")
@RestController

public class CollegiateSubredditController extends ApiController{
    @Autowired
    CollegiateSubredditRepository collegiateSubredditRepository;

    @ApiOperation(value = "List all CollegiateSubreddits")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<CollegiateSubreddit> allCollegiateSubreddits() {
        loggingService.logMethod();
        Iterable<CollegiateSubreddit> collegiateSubreddits = collegiateSubredditRepository.findAll();
        return collegiateSubreddits;
    }

    @ApiOperation(value = "Create new subreddit")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")

    public CollegiateSubreddit postCollegiateSubreddit(
            @ApiParam("name") @RequestParam String name,
            @ApiParam("location") @RequestParam String location,
            @ApiParam("subreddit") @RequestParam String subreddit,
            @ApiParam("done") @RequestParam Boolean done) {
        loggingService.logMethod();
        //CurrentUser currentUser = getCurrentUser();
        //log.info("currentUser={}", currentUser);

        CollegiateSubreddit collegiateSubreddit = new CollegiateSubreddit();
        //collegiateSubreddit.setUser(currentUser.getUser());
        collegiateSubreddit.setName(name);
        collegiateSubreddit.setLocation(location);
        collegiateSubreddit.setSubreddit(subreddit);
        //collegiateSubreddit.setDone(done);
        CollegiateSubreddit savedcollegiateSubreddit = collegiateSubredditRepository.save(collegiateSubreddit);
        return savedcollegiateSubreddit;
    }

    /*
    @ApiOperation(value = "List this user's todos")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<Todo> thisUsersTodos() {
        loggingService.logMethod();
        CurrentUser currentUser = getCurrentUser();
        Iterable<Todo> todos = todoRepository.findAllByUserId(currentUser.getUser().getId());
        return todos;
    }
    */
}
