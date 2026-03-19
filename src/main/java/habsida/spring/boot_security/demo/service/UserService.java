package habsida.spring.boot_security.demo.service;


import habsida.spring.boot_security.demo.model.Role;
import habsida.spring.boot_security.demo.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Set;

public interface UserService extends UserDetailsService {
    List<User> getAllUsers();
    void saveUser(User user);
    void updateUser(Long id, User user);
    void deleteUser(Long id);
    User getUser(Long id);

    boolean isUsernameTaken(String username);

    boolean isUsernameTakenForUpdate(String username, Long id);


    UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException;

    User findByUsername(String username);

    Role getRoleByName(String roleAdmin);

    Set<Role> getRolesByNames(Set<String> roles);
}
