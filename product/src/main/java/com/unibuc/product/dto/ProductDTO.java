package com.unibuc.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unibuc.product.model.Review;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO extends RepresentationModel<ProductDTO> {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String barcode;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Float price;

    private List<String> categories;

    private List<Review> reviews;
}
