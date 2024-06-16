package com.unibuc.product.controller;

import com.unibuc.product.dto.ProductDTO;
import com.unibuc.product.model.Product;
import com.unibuc.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/product")
public class ProductController {
    ProductService productService;
    ModelMapper modelMapper;

    public ProductController(ProductService productService, ModelMapper modelMapper) {
        this.productService = productService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<Product> save(@Valid @RequestBody Product product){
        Product savedSubscription = modelMapper.map(productService.save(product), Product.class);
        URI locationUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{productId}").buildAndExpand(savedSubscription.getBarcode())
                .toUri();

        return ResponseEntity.created(locationUri).body(savedSubscription);
    }

    @PutMapping
    public ResponseEntity<Product> update(@Valid @RequestBody Product product){
        Product updatedSubscription = productService.update(product.getBarcode(), product);
        return ResponseEntity.ok(updatedSubscription);
    }

    @GetMapping("/barcode/{barcode}")
    public Product findByBarcode(@PathVariable String barcode) {
        return productService.findProductByBarcode(barcode);
    }

    @GetMapping("/name")
    public ProductDTO findByProductName(@RequestParam String productName) {
        Product theProduct =  productService.findProductByProductName(productName);
        ProductDTO productDTO = modelMapper.map(theProduct, ProductDTO.class);

        Link selfLink = linkTo(methodOn(ProductController.class).findByBarcode(theProduct.getBarcode())).withSelfRel();
        productDTO.add(selfLink);

        return productDTO;
    }

    @GetMapping
    public CollectionModel<ProductDTO> findAll(){
        List<ProductDTO> productDtoList =  productService.findAll();


        for (final ProductDTO productDTO : productDtoList) {
            Link selfLink = linkTo(methodOn(ProductController.class).findByBarcode(productDTO.getBarcode())).withSelfRel();
            productDTO.add(selfLink);

//            Link deleteLink = linkTo(methodOn(ProductController.class).deleteById(product.getId())).withRel("deleteProduct");
//            productDTO.add(deleteLink);

            Link postLink = linkTo(methodOn(ProductController.class).save(modelMapper.map(productDTO, Product.class))).withRel("saveSubscription");
            productDTO.add(postLink);

            Link putLink = linkTo(methodOn(ProductController.class).update(modelMapper.map(productDTO, Product.class))).withRel("updateSubscription");
            productDTO.add(putLink);
        }

        Link link = linkTo(methodOn(ProductController.class).findAll()).withSelfRel();
        return CollectionModel.of(productDtoList, link);
    }

    @DeleteMapping("/{barcode}")
    @Operation(summary = "Delete product by product id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content)})
    public ResponseEntity<Void> deleteById(@PathVariable String barcode) {

        boolean deleted = productService.deleteByBarcode(barcode);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{barcode}/null-quantity")
    public String deleteProductWhenEmptyInventory(@PathVariable String barcode) {
        productService.deleteProductWhenEmptyInventory(barcode);
        return "The product with barcode " + barcode + " was successfully deleted";
    }
}
