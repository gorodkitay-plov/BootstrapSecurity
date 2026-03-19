package habsida.spring.boot_security.demo.repository;

import habsida.spring.boot_security.demo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

// Репозиторий для работы с ролями в БД
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}