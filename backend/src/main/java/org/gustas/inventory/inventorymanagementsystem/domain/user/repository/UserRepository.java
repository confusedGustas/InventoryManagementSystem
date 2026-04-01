package org.gustas.inventory.inventorymanagementsystem.domain.user.repository;

import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findByUsername(String username);
    User findUserById(UUID id);
    User findUserByIdAndCompanyId(UUID id, UUID companyId);
    Page<User> findAllByCompanyId(UUID companyId, Pageable pageable);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsernameAndIdNot(String username, UUID id);
    boolean existsByEmailAndIdNot(String email, UUID id);

}
