package com.example.lms.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="roles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false,unique = true)
    private RoleName roleName;
}
