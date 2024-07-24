package com.zkd.demo.datasource.entity.VO;

import com.zkd.demo.datasource.entity.DTO.Link;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@ToString
@Accessors(chain = true)
public class DataSourceLinkVO {

    @ApiModelProperty("数据源编码")
    private String sourceCode;

    @ApiModelProperty("数据源类型")
    private Integer sourceType;

    @ApiModelProperty("连接信息")
    private List<Link> linkList;

}
