package com.capstone.desk_nova.repository;

import com.capstone.desk_nova.model.TicketComments;
import com.capstone.desk_nova.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long>{

    Optional<Users> findById(Long id);
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);
}
