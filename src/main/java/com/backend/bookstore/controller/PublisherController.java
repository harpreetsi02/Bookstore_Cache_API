package com.backend.bookstore.controller;

import com.backend.bookstore.dto.request.PublisherRequest;
import com.backend.bookstore.dto.response.PublisherResponse;
import com.backend.bookstore.service.PublisherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publishers")
@Tag(name = "Publisher", description = "Books publishers")
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @Operation(summary = "Create a new publisher")
    @ApiResponse(responseCode = "201", description = "Publisher created")
    @ApiResponse(responseCode = "409", description = "Publisher name already exists")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @PostMapping
    public ResponseEntity<PublisherResponse> createPublisher(
            @Valid @RequestBody PublisherRequest request
    ) {
        PublisherResponse response =
                publisherService.createPublisher(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "List all publishers")
    @ApiResponse(responseCode = "200", description = "Publisher get successful")
    @GetMapping
    public ResponseEntity<List<PublisherResponse>> findAllPublishers(){

        List<PublisherResponse> responses =
                publisherService.getPublishers();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get publisher by id")
    @ApiResponse(responseCode = "200", description = "Publisher get successful")
    @ApiResponse(responseCode = "404", description = "Publisher not found")
    @GetMapping("/{publisherId}")
    public ResponseEntity<PublisherResponse> getPublisherById(
            @PathVariable Long publisherId
    ) {
        PublisherResponse response =
                publisherService.getPublisherById(publisherId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update publisher by id")
    @ApiResponse(responseCode = "200", description = "Publisher update successfully")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "404", description = "Publisher not found")
    @ApiResponse(responseCode = "409", description = "Publisher name already exists")
    @PutMapping("/{publisherId}")
    public ResponseEntity<PublisherResponse> updatePublisher(
            @PathVariable Long publisherId,
            @Valid @RequestBody PublisherRequest request
    ) {
        PublisherResponse response =
                publisherService.updatePublisher(publisherId, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Deleting Publisher")
    @ApiResponse(responseCode = "204", description = "Publisher delete successful")
    @ApiResponse(responseCode = "404", description = "Publisher not found")
    @DeleteMapping("/{publisherId}")
    public ResponseEntity<Void> deletePublisher(
            @PathVariable Long publisherId
    ) {
        publisherService.deletePublisherById(publisherId);

        return ResponseEntity.noContent().build();
    }
}
