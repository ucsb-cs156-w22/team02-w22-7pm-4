package edu.ucsb.cs156.team02.repositories;

import edu.ucsb.cs156.team02.entities.UCSBRequirement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

 @Repository
 public interface UCSBRequirementRepository extends CrudRepository<UCSBRequirement, Long> {
   Iterable<UCSBRequirement> findBySubject(String subject);
 }