package edu.ucsb.cs156.team02.repositories;

import edu.ucsb.cs156.team02.entities.UCSBSubject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UCSBSubjectRepository extends CrudRepository<UCSBSubject, Long> {
//    Iterable<UCSBSubject> findById(String subject);
}