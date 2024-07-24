package com.zkd.demo.datasource.entity.VO;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@ToString
@Accessors(chain = true)
public class DataSourceTypeVO  {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("数据源类型编码")
    private String sourceTypeCode;

    @ApiModelProperty("数据源类型名称")
    private String sourceTypeName;

    @ApiModelProperty("数据源类型 类型唯一")
    private String sourceType;

    @ApiModelProperty("数据源类型图片地址")
    private String sourceTypePicUrl;

    @ApiModelProperty("创建时间")
    private LocalDateTime createAt;

    @ApiModelProperty("修改时间")
    private LocalDateTime updateAt;

}
