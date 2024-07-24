package com.zkd.demo.datasource.entity.DO;


import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@ToString
@TableName(value = "t_data_source_link_value")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TDataSourceLinkValueDO extends Model<TDataSourceLinkValueDO> {

    @ApiModelProperty("数据源自增id")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("数据源编码")
    private String sourceCode;

    @ApiModelProperty("数据源详情字段编码")
    private String sourceTypeKeyCode;

    @ApiModelProperty("字段对应值")
    private String linkValue;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty("创建人用户编码")
    private String creator;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty("更新人用户编码")
    private String updator;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty("创建时间")
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty("修改时间")
    private LocalDateTime updateAt;

    @ApiModelProperty("是否删除 N正常 Y删除")
    private String deleteFlag;

}
