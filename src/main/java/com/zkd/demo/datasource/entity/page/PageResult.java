package com.zkd.demo.datasource.entity.page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@ApiModel(value = "分页数据返回对象")
public class PageResult<T> implements Serializable {
	private static final long serialVersionUID = -699006056541727165L;

	@ApiModelProperty(value = "当前页数")
	private int pageNum;

	@ApiModelProperty(value = "每页数量")
	private int pageSize;

	@ApiModelProperty(value = "总页数")
	private int totalPages;

	@ApiModelProperty(value = "总记录数")
	private long totalSize;

	@ApiModelProperty(value = "数据列表")
	private List<T> dataList;

	public PageResult(int pageNum, int pageSize, int totalSize) {
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.totalSize = totalSize;
		this.totalPages = totalSize / pageSize + 1;
	}
}