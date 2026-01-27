package com.getAssetsPortal.repositories;

import com.getAssetsPortal.entity.Users;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users,Long> {

    Optional<Users> findByEmployeeCode(String employeeCode);

    Optional<Users> findByDomainId(String domainId);

    List<Users> findAllByDomainIdIn(List<String> domainIds);

}
