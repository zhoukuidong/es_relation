package com.zkd.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageVo<T> {

    private List<T> dataList;

    private String scrollId;

    private int pageNum;

    private int pageSize;

}
