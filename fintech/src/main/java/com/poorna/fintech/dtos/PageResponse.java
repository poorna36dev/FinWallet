package com.poorna.fintech.dtos;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PageResponse<T> {

    @Schema(description = "Items returned for the current page")
    private List<T> content;

    @Schema(description = "Current page number", example = "0")
    private int page;

    @Schema(description = "Number of items requested per page", example = "20")
    private int size;

    @Schema(description = "Total number of elements available", example = "45")
    private long totalElements;

    @Schema(description = "Total number of pages available", example = "3")
    private int totalPages;

    @Schema(description = "Indicates whether the current page is the first page")
    private boolean first;

    @Schema(description = "Indicates whether the current page is the last page")
    private boolean last;

    @Schema(description = "Indicates whether a next page exists")
    private boolean hasNext;

    @Schema(description = "Indicates whether a previous page exists")
    private boolean hasPrevious;
}