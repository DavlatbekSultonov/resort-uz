package com.example.resort_uz.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Service_entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Qaysi maskanga tegishli
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resort_id", nullable = false)
    private Resort resort;

    // ================================================
    //  XIZMAT MA'LUMOTLARI
    // ================================================

    // Xizmat nomi: "Hovuz", "Sauna", "Restoran", "Bolalar maydoni"
    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    // Qo'shimcha tavsif
    @Column(columnDefinition = "TEXT")
    private String description;

    // Kategoriya
    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false, length = 50)
    private ServiceType serviceType;

    // Frontend uchun ikonka nomi: "pool", "utensils", "dumbbell"
    @Column(length = 50)
    private String icon;

    // ================================================
    //  NARX
    // ================================================

    // Bepulmi yoki pullikmi
    @Column(name = "is_paid", nullable = false)
    @Builder.Default
    private Boolean isPaid = false;

    // Narxi (agar pullik bo'lsa)
    @Column(name = "price")
    private BigDecimal price;

    // Narx turi
    @Enumerated(EnumType.STRING)
    @Column(name = "price_type", length = 20)
    private PriceType priceType;

    // Valyuta
    @Column(length = 5)
    @Builder.Default
    private String currency = "UZS";

    // ================================================
    //  HOLAT
    // ================================================

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    // ================================================
    //  ENUMLAR
    // ================================================

    public enum ServiceType {
        SUV_HAVZA,          // Hovuz, hammom, jaküzi, suv park
        SPORT,              // Tennis, futbol, voleybol, fitnes
        OVQATLANISH,        // Restoran, kafe, BBQ, barbekyu
        BOLALAR,            // Bolalar maydoni, animatsiya
        TRANSPORT,          // Transfer, taksi, parking, velosiped
        SOGLIKNI_SAQLASH,   // Spa, massaj, tibbiy xona
        FAOLLIK,            // Kvadrosikl, ot minish, gidropedal, baliqchilik
        BOSHQA
    }

    public enum PriceType {
        SOATLIK,    // Soatiga
        KUNLIK,     // Kuniga
        KISHIGA,    // Har bir kishiga
        GURUHGA     // Butun guruhga
    }
}
