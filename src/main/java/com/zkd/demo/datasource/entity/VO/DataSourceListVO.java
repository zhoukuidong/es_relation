package com.zkd.demo.datasource.entity.VO;


import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@ToString
@Accessors(chain = true)
public class DataSourceListVO {

    @ApiModelProperty("数据源名称")
    protected String sourceName;

    @ApiModelProperty("数据源编码")
    protected String sourceCode;

    @ApiModelProperty("数据源类型编码")
    protected String sourceTypeCode;

    @ApiModelProperty("数据源类型")
    @TableField(exist = false)
    protected String sourceTypeName;

    @ApiModelProperty("创建时间")
    protected LocalDateTime createAt;

    @ApiModelProperty("修改时间")
    protected LocalDateTime updateAt;

}
