package com.zkd.demo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ActualUsageTimeVo {

    private long days;

    private long hours;

    private long minutes;

}
