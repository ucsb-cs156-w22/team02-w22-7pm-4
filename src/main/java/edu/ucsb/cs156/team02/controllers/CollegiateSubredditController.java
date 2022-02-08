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

    @Autowired
    ObjectMapper mapper;

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
            @ApiParam("subreddit") @RequestParam String subreddit)
            //@ApiParam("done") @RequestParam Boolean done) 
            {
        loggingService.logMethod();

        CollegiateSubreddit collegiateSubreddit = new CollegiateSubreddit();
        //collegiateSubreddit.setUser(currentUser.getUser());
        collegiateSubreddit.setName(name);
        collegiateSubreddit.setLocation(location);
        collegiateSubreddit.setSubreddit(subreddit);
        //collegiateSubreddit.setDone(done);
        CollegiateSubreddit savedcollegiateSubreddit = collegiateSubredditRepository.save(collegiateSubreddit);
        return savedcollegiateSubreddit;
    }

    public class CollegiateSubredditOrError {
        Long id;
        CollegiateSubreddit todo;
        ResponseEntity<String> error;

        public CollegiateSubredditOrError(Long id) {
            this.id = id;
        }
    }
    public CollegiateSubredditOrError doesCollegiateSubredditExist(CollegiateSubredditOrError toe) {

        Optional<CollegiateSubreddit> optionalTodo = collegiateSubredditRepository.findById(toe.id);

        if (optionalTodo.isEmpty()) {
            toe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("CollegiateSubreddit with id %d not found", toe.id));
        } else {
            toe.todo = optionalTodo.get();
        }
        return toe;
    }


    @ApiOperation(value = "get subreddit with given id")
    @PreAuthorize("hasRole('ROLE_USER')")
    //@GetMapping("/collegiateSubreddits?id=123")
    @GetMapping("")

    public ResponseEntity<String> getCollegiateSubredditById(
        @ApiParam("id") @RequestParam Long id) throws JsonProcessingException {
        loggingService.logMethod();
        CollegiateSubredditOrError toe = new CollegiateSubredditOrError(id);

        toe = doesCollegiateSubredditExist(toe);
        if (toe.error != null) {
            return toe.error;
        }
        
        String body = mapper.writeValueAsString(toe.todo);
        return ResponseEntity.ok().body(body);
    }


    /*
    @ApiOperation(value = "List this user's CollegiateSubreddit")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<CollegiateSubreddit> thisUsersTodos() {
        loggingService.logMethod();
        CurrentUser currentUser = getCurrentUser();
        Iterable<CollegiateSubreddit> todos = CollegiateSubredditRepository.findAllByUserId(currentUser.getUser().getId());
        return todos;
    }*/
    
}
