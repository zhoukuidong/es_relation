package com.zkd.demo.datasource.entity.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@Accessors(chain = true)
public class DataSourceSqlRequest {
    @ApiModelProperty(value = "数据源编码",required = true)
    @NotBlank(message = "数据源编码不能为空")
    private String sourceCode;

    @NotBlank(message = "数据源编码不能为空")
    private String sql;
}
