package com.github.xiaoyao9184.eproject.version.controller;

import com.github.xiaoyao9184.eproject.restful.model.Pagination;
import com.github.xiaoyao9184.eproject.version.document.ApiVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.github.xiaoyao9184.eproject.version.router.ApiVersionHandler.TIME_PARAM;

/**
 * API version
 * Created by xy on 2021/2/3.
 */
@RestController
public class ApiVersionController {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @Operation(summary="get API versions", description="get API versions")
    @GetMapping("/v1/versions/{*path}")
    public Flux<ApiVersion> handle(
            @PathVariable("path") String path,
            @Parameter(example = "2030-06-29T08:56:30+00:00")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start_time,
            @Parameter(example = "2030-06-29T08:56:30+00:00")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end_time,
            Pagination pagination
            ) {
        if(end_time == null){
            end_time = LocalDateTime.now();
        }
        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getPer_page());
        Query query = Query.query(
                Criteria.where(TIME_PARAM).gte(start_time).lte(end_time))
            .with(pageable);
        //TODO use ApiVersionSchema
        Flux<ApiVersion> list = reactiveMongoTemplate.find(query, ApiVersion.class, path);
        return list.switchIfEmpty(Mono.error(new NullPointerException()));
    }

    @ResponseStatus(
            value = HttpStatus.NO_CONTENT,
            reason = "empty")
    @ExceptionHandler(NullPointerException.class)
    public void emptyHandler() {
        //
    }
}
