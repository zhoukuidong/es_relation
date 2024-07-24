package com.zkd.demo.datasource.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@Accessors(chain = true)
public class DataSourceDropVO {

    @ApiModelProperty("数据源编码")
    private String sourceCode;

    @ApiModelProperty("数据源名称")
    private String sourceName;

    @ApiModelProperty("数据源类型编码")
    private String sourceTypeCode;

}
