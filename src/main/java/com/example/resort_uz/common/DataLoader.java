    package com.example.resort_uz.common;

    import com.example.resort_uz.entity.Admin;
    import com.example.resort_uz.repository.AdminRepository;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.boot.CommandLineRunner;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.stereotype.Component;

    @Slf4j
    @Component
    @RequiredArgsConstructor
    public class DataLoader implements CommandLineRunner {

        private final AdminRepository adminRepository;
        private final BCryptPasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) {
            // Superadmin mavjud bo'lmasa yaratadi
            if (!adminRepository.existsByUsername("admin")) {
                adminRepository.save(Admin.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .fullName("Super Admin")
                        .role(Admin.AdminRole.SUPERADMIN)
                        .active(true)
                        .build());
                log.info("Superadmin yaratildi: admin / admin123");
            }
        }
    }