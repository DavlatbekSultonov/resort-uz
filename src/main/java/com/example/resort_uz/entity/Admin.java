package com.example.resort_uz.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "admins")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================================================
    //  LOGIN MA'LUMOTLARI
    // ================================================

    @NotBlank
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String password;

    // ================================================
    //  SHAXSIY MA'LUMOTLAR
    // ================================================

    @NotBlank
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    // ================================================
    //  ROL
    // ================================================

    // SUPERADMIN — hamma narsani boshqaradi
    // OWNER — faqat o'z maskanlarini boshqaradi
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AdminRole role = AdminRole.OWNER;

    // ================================================
    //  HOLAT
    // ================================================

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    // ================================================
    //  VAQT
    // ================================================

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    // ================================================
    //  BOG'LIQ MASKANLAR
    // ================================================

    @OneToMany(mappedBy = "admin", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Resort> resorts = new ArrayList<>();

    public enum AdminRole {
        SUPERADMIN,  // Hamma narsani boshqaradi
        OWNER        // Faqat o'z maskanlarini boshqaradi
    }
}
