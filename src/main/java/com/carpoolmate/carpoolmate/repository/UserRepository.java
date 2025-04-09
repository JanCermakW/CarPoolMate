package com.carpoolmate.carpoolmate.repository;

import com.carpoolmate.carpoolmate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByVerificationCode(String code);

    @Query("SELECT u FROM User u WHERE u.idPhotoPath IS NOT NULL AND u.role <> :roleType")
    List<User> findPendingDrivers(@Param("roleType") com.carpoolmate.carpoolmate.model.Role roleType);


}
