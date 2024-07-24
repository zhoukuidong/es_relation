package com.zkd.demo.datasource.entity.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;


@Data
@ToString
@Accessors(chain = true)
public class Link {

    @ApiModelProperty("数据源详情字段编码")
    private String sourceTypeKeyCode;

    @ApiModelProperty("字段名称")
    private String sourceTypeKeyName;

    @ApiModelProperty("字段英文")
    private String sourceTypeKey;

    @ApiModelProperty("字段对应值")
    private String linkValue;

    @ApiModelProperty("默认值")
    private String defaultValue;

    @ApiModelProperty("输入类型")
    private String keyType;

    @ApiModelProperty("是否必填 Y-是 N-否")
    private String requiredFlag;

    @ApiModelProperty("校验正则")
    private String checkRegular;

    @ApiModelProperty("是否只读 Y-是 N-否")
    private String onlyReadFlag;
}