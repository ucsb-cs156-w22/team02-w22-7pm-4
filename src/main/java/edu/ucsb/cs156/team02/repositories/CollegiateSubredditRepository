package edu.ucsb.cs156.team02.repositories;

import edu.ucsb.cs156.team02.entities.Todo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollegiateSubredditRepository extends CrudRepository<CollegiateSubreddit, Long> {
    Iterable<CollegiateSubredddit> findByName(String name);
    Iterable<CollegiateSubredddit> findBySubreddit(String subreddit);

}