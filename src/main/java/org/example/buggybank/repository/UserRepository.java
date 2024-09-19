package org.example.buggybank.repository;

import org.example.buggybank.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

}
