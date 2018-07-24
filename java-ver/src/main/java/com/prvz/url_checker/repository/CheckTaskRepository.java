package com.prvz.url_checker.repository;

import com.prvz.url_checker.model.CheckTask;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CheckTaskRepository extends MongoRepository<CheckTask, String> {

    Optional<CheckTask> findTopByOrderByCreateDateDesc();

}
