package habsida.spring.boot_security.demo.service;


import habsida.spring.boot_security.demo.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService {
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
}
