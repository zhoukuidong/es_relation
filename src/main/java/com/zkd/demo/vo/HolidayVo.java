package com.zkd.demo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
public class HolidayVo {

    //法定节假日
    Set<LocalDate> holidays = new HashSet<>();
    //补班
    Set<LocalDate> supplementaryShiftDays = new HashSet<>();

}
