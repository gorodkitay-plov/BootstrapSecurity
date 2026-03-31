package habsida.spring.boot_security.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    public Role() {}
    public Role(String name) { this.name = name; }

    @Override
    public String getAuthority() { return name; }

    public Long   getId()   { return id; }
    public String getName() { return name; }
    public void setId(Long id)       { this.id = id; }
    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        return Objects.equals(name, ((Role) o).name);
    }

    @Override
    public int hashCode() { return Objects.hash(name); }

    @Override
    public String toString() { return name; }
}