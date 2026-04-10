package com.scanly.scanlyBackend.models;

import com.scanly.scanlyBackend.models.enums.CouponType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private String label;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType type;

    @Column(nullable = false)
    private BigDecimal value;

    @Column(nullable = false)
    private BigDecimal minOrderValue;

    @Column(nullable = false)
    private Boolean active = true;

    private Instant validFrom;

    private Instant validUntil;

    private Integer maxUsages;

    private Integer currentUsages = 0;

    @CreationTimestamp
    private Instant createdAt;

    public Coupon(String code, String label, CouponType type, BigDecimal value, BigDecimal minOrderValue) {
        this.code = code;
        this.label = label;
        this.type = type;
        this.value = value;
        this.minOrderValue = minOrderValue;
        this.active = true;
        this.currentUsages = 0;
    }

    public boolean isValid() {
        if (!active) {
            return false;
        }

        Instant now = Instant.now();
        if (validFrom != null && now.isBefore(validFrom)) {
            return false;
        }

        if (validUntil != null && now.isAfter(validUntil)) {
            return false;
        }

        if (maxUsages != null && currentUsages >= maxUsages) {
            return false;
        }

        return true;
    }

    public void incrementUsage() {
        this.currentUsages++;
    }
}
