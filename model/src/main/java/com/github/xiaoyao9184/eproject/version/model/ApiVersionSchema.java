package com.github.xiaoyao9184.eproject.version.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * Api version
 * Created by xy on 2021/2/3.
 */
@ApiModel(description = "ApiVersion")
@Schema(description = "Api version")
public class ApiVersionSchema {

    @ApiModelProperty(value = "uri", notes = "cant not change",
            accessMode = ApiModelProperty.AccessMode.AUTO)
    @Schema(title = "uri")
    private String uri;

    @ApiModelProperty(value = "method of http",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Schema(title = "method of http")
    private String method;

    @ApiModelProperty(value = "content-type header of http",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Schema(title = "content-type header of http")
    private String contentType;

    @NotEmpty
    @ApiModelProperty(value = "body of http",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Schema(title = "body of http")
    private String contentBody;

    @ApiModelProperty(value = "time",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Schema(title = "time")
    private Date time;

    @ApiModelProperty(value = "user name",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Schema(title = "user name")
    private String userName;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentBody() {
        return contentBody;
    }

    public void setContentBody(String contentBody) {
        this.contentBody = contentBody;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
