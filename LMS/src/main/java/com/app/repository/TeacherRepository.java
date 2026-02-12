package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.entity.Teacher;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByEmail(String email);
}
