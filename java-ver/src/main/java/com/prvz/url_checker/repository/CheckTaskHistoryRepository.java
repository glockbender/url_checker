package com.prvz.url_checker.repository;

import com.prvz.url_checker.model.CheckTaskHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckTaskHistoryRepository extends MongoRepository<CheckTaskHistory, String> {

    Page<CheckTaskHistory> findAllByTaskId(String id, Pageable pageable);
}
