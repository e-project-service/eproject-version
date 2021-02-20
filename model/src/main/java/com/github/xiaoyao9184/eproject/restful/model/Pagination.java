package com.github.xiaoyao9184.eproject.restful.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@ApiModel(
        description = "pagination"
)
@Schema(
        description = "pagination"
)
public class Pagination {
    @ApiModelProperty(
            value = "page number",
            example = "0"
    )
    @Parameter(
            in = ParameterIn.QUERY,
            required = true,
            example = "0"
    )
    @Schema(
            title = "page number"
    )
    @NotNull
    @Min(0L)
    private int page;
    @ApiModelProperty(
            value = "page size",
            example = "10"
    )
    @Parameter(
            in = ParameterIn.QUERY,
            required = true,
            example = "10"
    )
    @Schema(
            title = "page size"
    )
    @NotNull
    @Min(0L)
    private int per_page;

    public Pagination() {
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPer_page() {
        return this.per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }
}
