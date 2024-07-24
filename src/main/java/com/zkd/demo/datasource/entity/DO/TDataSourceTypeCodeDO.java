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
@TableName(value = "t_data_source_type_code")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TDataSourceTypeCodeDO extends Model<TDataSourceTypeCodeDO> {

    @ApiModelProperty("数据源详情自增id")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("数据源类型编码")
    private String sourceTypeCode;

    @ApiModelProperty("字段编码")
    private String sourceTypeKeyCode;

    @ApiModelProperty("字段名称")
    private String sourceTypeKeyName;

    @ApiModelProperty("字段英文")
    private String sourceTypeKey;

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
