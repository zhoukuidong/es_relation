package com.zkd.demo.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.zkd.demo.constant.CommonConstant;
import com.zkd.demo.vo.ActualUsageTimeVo;
import com.zkd.demo.vo.HolidayVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class WorkdayCalculatorWithHolidaysUtil {


    private static RedisTemplate<String, String> redisTemplate = SpringUtil.getBean("redisTemplate");

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        StringBuilder sb = new StringBuilder();
        String sql = "insert into gbase8s_test (id,t_char,t_date,t_datetime) values ";
        sb.append(sql);
        for (int i = 500; i <= 10000; i++) {
            sb.append("(").append(i).append( ",'z','2024-03-22','2024-03-22 12:00:00') ");
            if (i == 10000) {
                sb.append(";");            }

        }
        System.out.println(sb.toString());
    }

        /**
         * 解析节假日json串
         *
         * @param holidayData
         * @return
         */
    public static HolidayVo parseHolidays(String holidayData) {
        //法定节假日
        Set<LocalDate> holidays = new HashSet<>();
        //补班
        Set<LocalDate> supplementaryShiftDays = new HashSet<>();
        JSONObject jsonData = JSONObject.parseObject(holidayData);
        JSONObject holidayObj = jsonData.getJSONObject("holiday");
        for (String key : holidayObj.keySet()) {
            JSONObject dateInfo = holidayObj.getJSONObject(key);
            boolean isHoliday = dateInfo.getBoolean("holiday");
            String dateStr = dateInfo.getString("date");
            if (isHoliday) {
                holidays.add(LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE));
            } else {
                supplementaryShiftDays.add(LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE));
            }
        }
        return new HolidayVo().setHolidays(holidays).setSupplementaryShiftDays(supplementaryShiftDays);
    }

    public static LocalDate calculateWorkday(LocalDate startDate, int workdays, HolidayVo holidayVo) {
        int startYear = startDate.getYear();
        LocalDate date = startDate;
        int addedDays = 0;
        while (addedDays < workdays) {
            date = date.plusDays(1);
            int curYear = date.getYear();
            if (startYear == curYear) {
                if (isWorkingDay(date, holidayVo)) {
                    addedDays++;
                }
            } else {
                //跨年了 更新下HolidayVo
                holidayVo = parseHolidays(getHolidayVo(curYear));
                if (isWorkingDay(date, holidayVo)) {
                    addedDays++;
                }
                startYear = curYear;
            }
        }
        return date;
    }

    /**
     * 是否是工作日
     *
     * @param date
     * @param
     * @return
     */
    private static boolean isWorkingDay(LocalDate date, HolidayVo holidayVo) {
        //节假日
        Set<LocalDate> holidays = holidayVo.getHolidays();
        //补班日
        Set<LocalDate> supplementaryShiftDays = holidayVo.getSupplementaryShiftDays();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        boolean isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
        if (holidays.contains(date)) {
            return false;
        } else {
            if (supplementaryShiftDays.contains(date)) {
                return true;
            } else {
                return !isWeekend;
            }
        }
    }

    /**
     * 获startDate后dayCount个工作日的目标日期
     *
     * @param startDate 起始日期
     * @param dayCount  需要增加的工作日数
     * @return
     */
    public static LocalDate queryTargetDate(LocalDate startDate, int dayCount) {
        String holidayData = getHolidayVo(startDate.getYear());
        HolidayVo holidayVo = parseHolidays(holidayData);
        return calculateWorkday(startDate, dayCount, holidayVo);
    }

    /**
     * 获取当年的节假日json串
     *
     * @param year 年份
     * @return
     */
    public static String getHolidayVo(int year) {
        //从redis获取节假日json
        String holidayKey = CommonConstant.HOLIDAY_PREFIX + year;
        String holidayData = "";
        try {
            holidayData = redisTemplate.opsForValue().get(holidayKey);
            if (StrUtil.isBlank(holidayData)) {
                holidayData = HttpUtil.get("http://timor.tech/api/holiday/year/" + year);
                redisTemplate.opsForValue().set(holidayKey, holidayData);
            }
        } catch (Exception e) {
            log.error("" + e);
            holidayData = CommonConstant.DEFAULT_HOLIDAY_STR;
        }
        if (StrUtil.isBlank(holidayData)) {
            holidayData = CommonConstant.DEFAULT_HOLIDAY_STR;
        }
        return holidayData;
    }

    /**
     * 获取两个日期之间的除节假日之外的时长
     *
     * @return
     */
    public static String queryActualUsageTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) {
            return "1分";
        }
        long totalSeconds = getTotalSeconds(startTime, endTime);
        ActualUsageTimeVo actualUsageTimeVo = formatSeconds(totalSeconds);
        return formatActualUsageTimeVo(actualUsageTimeVo);
    }

    public static long getTotalSeconds(LocalDateTime startTime, LocalDateTime endTime){
        //总秒数
        long seconds = 0;
        LocalDate startDate = startTime.toLocalDate();
        LocalDate endDate = endTime.toLocalDate();
        //判断同一天的情况
        boolean sameDayFlag = startDate.equals(endDate);
        HolidayVo holidayVo = parseHolidays(getHolidayVo(startDate.getYear()));
        if (isWorkingDay(startDate, holidayVo)) {
            if (sameDayFlag) {
                seconds = ChronoUnit.SECONDS.between(startTime, endTime);
                return seconds;
            }
            //起始日和截止日都单独处理一下
            LocalDateTime endOfDay = startTime.toLocalDate().atTime(LocalTime.MAX);
            seconds = ChronoUnit.SECONDS.between(startTime, endOfDay);
        } else {
            if (sameDayFlag) {
                return 0;
            }
        }
        int startYear = startDate.getYear();
        LocalDate plusDate = startDate.plusDays(1);
        while (plusDate.compareTo(endDate) < 0) {
            int curYear = plusDate.getYear();
            if (startYear == curYear) {
                if (isWorkingDay(plusDate, holidayVo)) {
                    seconds += 24 * 60 * 60;
                }
            } else {
                //跨年了
                holidayVo = parseHolidays(getHolidayVo(curYear));
                if (isWorkingDay(plusDate, holidayVo)) {
                    seconds += 24 * 60 * 60;
                }
                startYear = curYear;
            }
            //往后推一天
            plusDate = plusDate.plusDays(1);
        }
        //计算一下截止日期的
        holidayVo = parseHolidays(getHolidayVo(endDate.getYear()));
        if (isWorkingDay(endDate, holidayVo)) {
            // 获取当天的开始时间
            LocalDateTime startOfDay = endTime.toLocalDate().atTime(LocalTime.MIN);
            seconds += ChronoUnit.SECONDS.between(startOfDay, endTime);
        }
        return seconds;
    }

    public static String formatActualUsageTimeVo(ActualUsageTimeVo actualUsageTimeVo){
        StringBuffer stringBuffer = new StringBuffer();
        if (actualUsageTimeVo.getDays() > 0) {
            stringBuffer.append(actualUsageTimeVo.getDays() + "工作日");
        }
        if (actualUsageTimeVo.getHours() > 0) {
            stringBuffer.append(actualUsageTimeVo.getHours() + "小时");
        }
        if (actualUsageTimeVo.getMinutes() > 0) {
            stringBuffer.append(actualUsageTimeVo.getMinutes() + "分");
        }
        return stringBuffer.toString();
    }

    /**
     * formatSeconds
     * 获取ActualUsageTimeVo
     *
     * @param seconds
     * @return
     */
    public static ActualUsageTimeVo formatSeconds(long seconds) {
        long days = seconds / (24 * 3600);
        long hours = (seconds % (24 * 3600)) / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;
        // 如果余数是1秒，进位到一分钟
        if (remainingSeconds > 0) {
            minutes++;
        }
        // 如果分钟是60，进位到一小时
        if (minutes == 60) {
            hours++;
            minutes = 0;
        }
        // 如果小时是24，进位到一天
        if (hours == 24) {
            days++;
            hours = 0;
        }
        return new ActualUsageTimeVo().setDays(days).setHours(hours).setMinutes(minutes);
    }

//    public static void main(String[] args) {
//
//        LocalDate startDate = LocalDate.of(2024, 1, 1); // 示例起始日期
//        System.out.println(queryTargetDate(startDate, 10));
//        LocalDate localDate = LocalDate.of(2023, 12, 26);
//        LocalDateTime localDateTime = LocalDateTime.of(2023,12,27,8,10,10);
//        LocalDateTime endLocalDateTime = LocalDateTime.of(2024,1,5,11,20,5);
//        System.out.println(WorkdayCalculatorWithHolidaysUtil.queryActualUsageTime(localDateTime,endLocalDateTime));
//        System.out.println(WorkdayCalculatorWithHolidaysUtil.queryTargetDate(localDate,10));
//    }

}
