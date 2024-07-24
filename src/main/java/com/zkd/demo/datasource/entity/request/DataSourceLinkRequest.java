package com.zkd.demo.datasource.entity.request;

import com.zkd.demo.datasource.entity.DTO.Link;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.List;


@Data
@ToString
@Accessors(chain = true)
public class DataSourceLinkRequest {
    @ApiModelProperty("数据源编码")
    private String sourceCode;

    @ApiModelProperty(value = "数据源名称", required = true)
    @NotBlank(message = "数据源名称不能为空")
    private String sourceName;

    @ApiModelProperty(value = "数据源类型编码", required = true)
    @NotBlank(message = "数据源类型编码不能为空")
    private String sourceTypeCode;

    @ApiModelProperty("连接信息")
    private List<Link> linkList;
}
