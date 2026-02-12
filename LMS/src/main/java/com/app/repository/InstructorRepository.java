package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.entity.Instructor;
import java.util.Optional;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    Optional<Instructor> findByEmail(String email);
}
