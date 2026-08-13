package id.perumdamts.kepegawaian.dto.appwrite;

import id.perumdamts.kepegawaian.entities.system.PrefPermission;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "pref_role")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PrefRole {
    @Id
    @NotEmpty(message = "ID is required")
    String id;

    // ADR-0039: label role untuk UI manajemen role (nullable; update via PUT /system/roles/{id})
    String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "pref_role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "perm_name"))
    Set<PrefPermission> permissions = new HashSet<>();

    public PrefRole(String id) {
        this.id = id;
    }

    public PrefRole(String id, String description) {
        this.id = id;
        this.description = description;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        PrefRole prefRole = (PrefRole) o;
        return getId() != null && Objects.equals(getId(), prefRole.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
