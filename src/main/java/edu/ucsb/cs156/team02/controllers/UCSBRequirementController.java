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

    /**
     * This inner class helps us factor out some code for checking
     * whether ucsbRequirements exist, and whether they belong to the current user,
     * along with the error messages pertaining to those situations. It
     * bundles together the state needed for those checks.
     */
    public class UCSBRequirementOrError {
        Long id;
        UCSBRequirement ucsbRequirement;
        ResponseEntity<String> error;

        public UCSBRequirementOrError(Long id) {
            this.id = id;
        }
    }

    @Autowired
    UCSBRequirementRepository ucsbRequirementRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "List all UCSBRequirements")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/all")
    public Iterable<UCSBRequirement> allUsersUCSBRequirement() {
        loggingService.logMethod();
        Iterable<UCSBRequirement> ucsbRequirements = UCSBRequirementRepository.findAll();
        return ucsbRequirements;
    }

    @ApiOperation(value = "List this user's UCSBRequirements")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBRequirement> thisUsersUCSBRequirement() {
        loggingService.logMethod();
        CurrentUser currentUser = getCurrentUser();
        Iterable<UCSBRequirement> ucsbRequirements = UCSBRequirementRepository.findAllByUserId(currentUser.getUser().getId());
        return ucsbRequirements;
    }

    @ApiOperation(value = "Get a single ucsbRequirement (if it belongs to current user)")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<String> getUCSBRequirementById(
            @ApiParam("id") @RequestParam Long id) throws JsonProcessingException {
        loggingService.logMethod();
        UCSBRequirementOrError uoe = new UCSBRequirementOrError(id);

        uoe = doesUCSBRequirementExist(uoe);
        if (uoe.error != null) {
            return uoe.error;
        }
        uoe = doesUCSBRequirementBelongToCurrentUser(uoe);
        if (uoe.error != null) {
            return uoe.error;
        }
        String body = mapper.writeValueAsString(uoe.ucsbRequirement);
        return ResponseEntity.ok().body(body);
    }

    @ApiOperation(value = "Get a single ucsbRequirement (no matter who it belongs to, admin only)")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<String> getUCSBRequirementById_admin(
            @ApiParam("id") @RequestParam Long id) throws JsonProcessingException {
        loggingService.logMethod();

        UCSBRequirementOrError uoe = new UCSBRequirementOrError(id);

        uoe = doesUCSBRequirementExist(uoe);
        if (uoe.error != null) {
            return uoe.error;
        }

        String body = mapper.writeValueAsString(uoe.ucsbRequirement);
        return ResponseEntity.ok().body(body);
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

    @ApiOperation(value = "Delete a UCSBRequirement owned by this user")
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("")
    public ResponseEntity<String> deleteUCSBRequirement(
            @ApiParam("id") @RequestParam Long id) {
        loggingService.logMethod();

        UCSBRequirementOrError uoe = new UCSBRequirementOrError(id);

        uoe = doesUCSBRequirementExist(uoe);
        if (uoe.error != null) {
            return uoe.error;
        }

        uoe = doesUCSBRequirementBelongToCurrentUser(uoe);
        if (uoe.error != null) {
            return uoe.error;
        }
        ucsbRequirementRepository.deleteById(id);
        return ResponseEntity.ok().body(String.format("ucsbRequirement with id %d deleted", id));

    }

    @ApiOperation(value = "Delete another user's ucsbRequirement")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/admin")
    public ResponseEntity<String> deleteUCSBRequirement_Admin(
            @ApiParam("id") @RequestParam Long id) {
        loggingService.logMethod();

        UCSBRequirementOrError uoe = new UCSBRequirementOrError(id);

        uoe = doesUCSBRequirementExist(uoe);
        if (uoe.error != null) {
            return uoe.error;
        }

        ucsbRequirementRepository.deleteById(id);

        return ResponseEntity.ok().body(String.format("ucsbRequirement with id %d deleted", id));

    }

    @ApiOperation(value = "Update a single ucsbRequirement (if it belongs to current user)")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("")
    public ResponseEntity<String> putUCSBRequirementById(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid UCSBRequirement incomingUCSBRequirement) throws JsonProcessingException {
        loggingService.logMethod();

        CurrentUser currentUser = getCurrentUser();
        User user = currentUser.getUser();

        UCSBRequirementOrError uoe = new UCSBRequirementOrError(id);

        uoe = doesUCSBRequirementExist(uoe);
        if (uoe.error != null) {
            return uoe.error;
        }
        uoe = doesUCSBRequirementBelongToCurrentUser(uoe);
        if (uoe.error != null) {
            return uoe.error;
        }

        incomingUCSBRequirement.setUser(user);
        ucsbRequirementRepository.save(incomingUCSBRequirement);

        String body = mapper.writeValueAsString(incomingUCSBRequirement);
        return ResponseEntity.ok().body(body);
    }

    @ApiOperation(value = "Update a single ucsbRequirement (regardless of ownership, admin only, can't change ownership)")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/admin")
    public ResponseEntity<String> putUCSBRequirementById_admin(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid UCSBRequirement incomingUCSBRequirement) throws JsonProcessingException {
        loggingService.logMethod();

        UCSBRequirementOrError uoe = new UCSBRequirementOrError(id);

        uoe = doesUCSBRequirementExist(uoe);
        if (uoe.error != null) {
            return uoe.error;
        }

        // Even the admin can't change the user; they can change other details
        // but not that.

        User previousUser = uoe.ucsbRequirement.getUser();
        incomingUCSBRequirement.setUser(previousUser);
        ucsbRequirementRepository.save(incomingUCSBRequirement);

        String body = mapper.writeValueAsString(incomingUCSBRequirement);
        return ResponseEntity.ok().body(body);
    }

    /**
     * Pre-conditions: uoe.id is value to look up, uoe.ucsbRequirement and uoe.error are null
     * 
     * Post-condition: if ucsbRequirement with id uoe.id exists, uoe.ucsbRequirement now refers to it, and
     * error is null.
     * Otherwise, ucsbRequirement with id uoe.id does not exist, and error is a suitable return
     * value to
     * report this error condition.
     */
    public UCSBRequirementOrError doesUCSBRequirementExist(UCSBRequirementOrError uoe) {

        Optional<UCSBRequirement> optionalUCSBRequirement = ucsbRequirementRepository.findById(uoe.id);

        if (optionalUCSBRequirement.isEmpty()) {
            uoe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("ucsbRequirement with id %d not found", uoe.id));
        } else {
            uoe.ucsbRequirement = optionalUCSBRequirement.get();
        }
        return uoe;
    }

    /**
     * Pre-conditions: uoe.ucsbRequirement is non-null and refers to the ucsbRequirement with id uoe.id,
     * and uoe.error is null
     * 
     * Post-condition: if ucsbRequirement belongs to current user, then error is still null.
     * Otherwise error is a suitable
     * return value.
     */
    public UCSBRequirementOrError doesUCSBRequirementBelongToCurrentUser(UCSBRequirementOrError uoe) {
        CurrentUser currentUser = getCurrentUser();
        log.info("currentUser={}", currentUser);

        Long currentUserId = currentUser.getUser().getId();
        Long ucsbRequirementUserId = uoe.ucsbRequirement.getUser().getId();
        log.info("currentUserId={} ucsbRequirementUserId={}", currentUserId, ucsbRequirementUserId);

        if (ucsbRequirementUserId != currentUserId) {
            uoe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("ucsbRequirement with id %d not found", uoe.id));
        }
        return uoe;
    }

}