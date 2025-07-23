package com.skillassessment.platform.entity;

import com.skillassessment.platform.enums.RoleName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false, unique = true)
    private RoleName name;

    /**
     * A convenience constructor that takes a RoleName enum
     * and sets it for the new Role object.
     * @param name The RoleName enum constant.
     */
    public Role(RoleName name) {
        this.name = name;
    }
}