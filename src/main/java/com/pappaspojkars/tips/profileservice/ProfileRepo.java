package com.pappaspojkars.tips.profileservice;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepo extends CrudRepository<User, Integer> {

    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
}
