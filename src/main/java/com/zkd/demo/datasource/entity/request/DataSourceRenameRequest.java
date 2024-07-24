package com.zkd.demo.datasource.entity.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@Accessors(chain = true)
public class DataSourceRenameRequest {

    @ApiModelProperty(value = "数据源编码",required = true)
    @NotBlank(message = "数据源编码不能为空")
    private String sourceCode;

    @ApiModelProperty(value = "数据源名称",required = true)
    @NotBlank(message = "数据源名称不能为空")
    private String sourceName;
}
