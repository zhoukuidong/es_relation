package com.zkd.demo.datasource.entity.DO;


import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.zkd.demo.datasource.entity.VO.DataSourceListVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;


@Data
@ToString
@TableName(value = "t_data_source")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TDataSourceDO extends Model<TDataSourceDO> {

    @ApiModelProperty("数据源自增id")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("数据源名称")
    private String sourceName;

    @ApiModelProperty("数据源编码")
    private String sourceCode;

    @ApiModelProperty("最后连接信息 NORMAL-正常 ERROR-异常")
    private String lastStatus;

    @ApiModelProperty("最后连接时间")
    private LocalDateTime lastConnTime;

    @ApiModelProperty("数据源类型编码")
    private String sourceTypeCode;

    @ApiModelProperty("心跳job对应id")
    private Integer jobId;

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

    public DataSourceListVO changeToVO(String sourceTypeName) {
        DataSourceListVO dataSourceListVO = new DataSourceListVO();
        BeanUtils.copyProperties(this, dataSourceListVO);
        dataSourceListVO.setSourceTypeName(sourceTypeName);
        return dataSourceListVO;
    }
}
