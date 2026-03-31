package habsida.spring.boot_security.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username не может быть пустым")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Пароль не может быть пустым")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "Имя не может быть пустым")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Фамилия не может быть пустой")
    @Column(nullable = false)
    private String surname;

    @NotNull(message = "Возраст не может быть пустым")
    @Min(value = 0, message = "Возраст не может быть отрицательным")
    @Max(value = 150, message = "Некорректный возраст")
    @Column(nullable = false)
    private Integer age;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return roles; }

    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }

    public User() {}

    public User(String username, String password, String email,
                String name, String surname, Integer age) {
        this.username = username;
        this.password = password;
        this.email    = email;
        this.name     = name;
        this.surname  = surname;
        this.age      = age;
    }

    public Long    getId()       { return id; }
    public String  getUsername() { return username; }
    public String  getPassword() { return password; }
    public String  getEmail()    { return email; }
    public String  getName()     { return name; }
    public String  getSurname()  { return surname; }
    public Integer getAge()      { return age; }
    public Set<Role> getRoles()  { return roles; }

    public void setId(Long id)             { this.id = id; }
    public void setUsername(String u)      { this.username = u; }
    public void setPassword(String p)      { this.password = p; }
    public void setEmail(String e)         { this.email = e; }
    public void setName(String n)          { this.name = n; }
    public void setSurname(String s)       { this.surname = s; }
    public void setAge(Integer a)          { this.age = a; }
    public void setRoles(Set<Role> roles)  { this.roles = roles; }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + '\'' +
                ", email='" + email + '\'' + ", name='" + name + '\'' +
                ", surname='" + surname + '\'' + ", age=" + age +
                ", roles=" + roles + '}';
    }
}