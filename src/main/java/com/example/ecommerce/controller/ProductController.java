package com.example.ecommerce.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> createProduct(
            @RequestPart(value = "product") String productJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Product product = mapper.readValue(productJson, Product.class);
        return ResponseEntity.ok(productService.createProduct(product, file));
    }

    private static final String UPLOAD_DIR = "uploads";  // Directory where images are stored

    @GetMapping("/uploads/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        try {
            // Build the full file path
            Path imagePath = Paths.get(UPLOAD_DIR).resolve(imageName).normalize();
            File imageFile = imagePath.toFile();

            if (!imageFile.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Create a URL resource for the image
            Resource resource = new UrlResource(imageFile.toURI());

            // Set the appropriate content type (assuming it's a JPEG, change it accordingly)
            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                contentType = MediaType.IMAGE_JPEG_VALUE;  // Default to JPEG if unknown
            }

            // Return the image as a ResponseEntity
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageName + "\"")
                    .body(resource);
        } catch (Exception ex) {
            return ResponseEntity.status(500).build();  // Internal Server Error in case of exception
        }
    }
}