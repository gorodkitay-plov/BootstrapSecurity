package habsida.spring.boot_security.demo.model;

import jakarta.validation.constraints.*;

import java.util.HashSet;
import java.util.Set;

public class UserCreateDto {

    @NotBlank(message = "Username обязателен")
    @Size(min = 3, max = 20, message = "Username должен быть 3-20 символов")
    private String username;

    @NotBlank(message = "Password обязателен")
    @Size(min = 4, message = "Пароль минимум 4 символа")
    private String password;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный email")
    private String email;

    @NotBlank(message = "Имя обязательно")
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\-]+$", message = "Имя — только буквы")
    private String name;

    @NotBlank(message = "Фамилия обязательна")
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\-]+$", message = "Фамилия — только буквы")
    private String surname;

    @NotNull(message = "Возраст обязателен")
    @Min(value = 1, message = "Возраст должен быть больше 0")
    @Max(value = 120, message = "Возраст должен быть меньше 120")
    private Integer age;

    @NotEmpty(message = "Выберите хотя бы одну роль")
    private Set<String> roles = new HashSet<>();

    public String getUsername()           { return username; }
    public void setUsername(String u)     { this.username = u; }
    public String getPassword()           { return password; }
    public void setPassword(String p)     { this.password = p; }
    public String getEmail()              { return email; }
    public void setEmail(String e)        { this.email = e; }
    public String getName()               { return name; }
    public void setName(String n)         { this.name = n; }
    public String getSurname()            { return surname; }
    public void setSurname(String s)      { this.surname = s; }
    public Integer getAge()               { return age; }
    public void setAge(Integer a)         { this.age = a; }
    public Set<String> getRoles()         { return roles; }
    public void setRoles(Set<String> r)   { this.roles = r; }
}