package com.unibuc.product.model;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
public class Review {

    @NotNull(message = "Rating is required")
    @Positive(message = "Rating must be positive")
    private double rating;

    private String content;

    @FutureOrPresent(message = "The date must be in the present or in the past")
    private LocalDate date;

    @NotBlank(message = "Username is required")
    private String username;
}
