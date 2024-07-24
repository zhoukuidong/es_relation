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
@TableName(value = "t_data_source_type")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TDataSourceTypeDO extends Model<TDataSourceTypeDO> {

    @ApiModelProperty("自增id")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("数据源类型编码")
    private String sourceTypeCode;

    @ApiModelProperty("数据源类型名称")
    private String sourceTypeName;

    @ApiModelProperty("数据源类型 类型唯一")
    private String sourceType;

    @ApiModelProperty("数据源类型图片地址")
    private String sourceTypePicUrl;

    @ApiModelProperty("数据源类型排序号")
    private int sourceTypeOrder;

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
