package com.zkd.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScorllAfterVo<T> {

    private List<T> dataList;

    private Object[] sortValues;

}
