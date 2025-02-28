package com.yatmk.persistence.models;

import java.time.Instant;

import jakarta.persistence.Entity;

import com.yatmk.persistence.models.config.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RefreshToken extends BaseEntity {

    private String token;

    private boolean isLoggedOut;

    private Instant expiryDate;

    private String userId;

}
