package com.zkd.demo.constant;


import java.time.format.DateTimeFormatter;

/**
 *
 * @description: 全局常量类
 */
public interface CommonConstant {


    //节假日兜底 2023年的
    String DEFAULT_HOLIDAY_STR = "{\"code\":0,\"holiday\":{\"01-01\":{\"holiday\":true,\"name\":\"元旦\",\"wage\":3,\"date\":\"2023-01-01\"},\"01-02\":{\"holiday\":true,\"name\":\"元旦\",\"wage\":2,\"date\":\"2023-01-02\"},\"01-21\":{\"holiday\":true,\"name\":\"除夕\",\"wage\":3,\"date\":\"2023-01-21\"},\"01-22\":{\"holiday\":true,\"name\":\"初一\",\"wage\":3,\"date\":\"2023-01-22\"},\"01-23\":{\"holiday\":true,\"name\":\"初二\",\"wage\":3,\"date\":\"2023-01-23\"},\"01-24\":{\"holiday\":true,\"name\":\"初三\",\"wage\":3,\"date\":\"2023-01-24\"},\"01-25\":{\"holiday\":true,\"name\":\"初四\",\"wage\":2,\"date\":\"2023-01-25\"},\"01-26\":{\"holiday\":true,\"name\":\"初五\",\"wage\":2,\"date\":\"2023-01-26\"},\"01-27\":{\"holiday\":true,\"name\":\"初六\",\"wage\":2,\"date\":\"2023-01-27\"},\"01-28\":{\"holiday\":false,\"name\":\"春节后补班\",\"wage\":1,\"after\":true,\"target\":\"春节\",\"date\":\"2023-01-28\"},\"01-29\":{\"holiday\":false,\"name\":\"春节后补班\",\"wage\":1,\"after\":true,\"target\":\"春节\",\"date\":\"2023-01-29\"},\"04-05\":{\"holiday\":true,\"name\":\"清明节\",\"wage\":3,\"date\":\"2023-04-05\",\"rest\":49},\"04-23\":{\"holiday\":false,\"name\":\"劳动节前补班\",\"wage\":1,\"target\":\"劳动节\",\"after\":false,\"date\":\"2023-04-23\"},\"04-29\":{\"holiday\":true,\"name\":\"劳动节\",\"wage\":2,\"date\":\"2023-04-29\"},\"04-30\":{\"holiday\":true,\"name\":\"劳动节\",\"wage\":2,\"date\":\"2023-04-30\"},\"05-01\":{\"holiday\":true,\"name\":\"劳动节\",\"wage\":3,\"date\":\"2023-05-01\"},\"05-02\":{\"holiday\":true,\"name\":\"劳动节\",\"wage\":3,\"date\":\"2023-05-02\"},\"05-03\":{\"holiday\":true,\"name\":\"劳动节\",\"wage\":3,\"date\":\"2023-05-03\"},\"05-06\":{\"holiday\":false,\"name\":\"劳动节后补班\",\"after\":true,\"wage\":1,\"target\":\"劳动节\",\"date\":\"2023-05-06\"},\"06-22\":{\"holiday\":true,\"name\":\"端午节\",\"wage\":3,\"date\":\"2023-06-22\"},\"06-23\":{\"holiday\":true,\"name\":\"端午节\",\"wage\":3,\"date\":\"2023-06-23\"},\"06-24\":{\"holiday\":true,\"name\":\"端午节\",\"wage\":2,\"date\":\"2023-06-24\"},\"06-25\":{\"holiday\":false,\"name\":\"端午节后补班\",\"wage\":1,\"target\":\"端午节\",\"after\":true,\"date\":\"2023-06-25\"},\"09-29\":{\"holiday\":true,\"name\":\"中秋节\",\"wage\":3,\"date\":\"2023-09-29\"},\"09-30\":{\"holiday\":true,\"name\":\"中秋节\",\"wage\":3,\"date\":\"2023-09-30\"},\"10-01\":{\"holiday\":true,\"name\":\"国庆节\",\"wage\":3,\"date\":\"2023-10-01\"},\"10-02\":{\"holiday\":true,\"name\":\"国庆节\",\"wage\":3,\"date\":\"2023-10-02\"},\"10-03\":{\"holiday\":true,\"name\":\"国庆节\",\"wage\":2,\"date\":\"2023-10-03\"},\"10-04\":{\"holiday\":true,\"name\":\"国庆节\",\"wage\":2,\"date\":\"2023-10-04\"},\"10-05\":{\"holiday\":true,\"name\":\"国庆节\",\"wage\":2,\"date\":\"2023-10-05\"},\"10-06\":{\"holiday\":true,\"name\":\"国庆节\",\"wage\":2,\"date\":\"2023-10-06\"},\"10-07\":{\"holiday\":false,\"after\":true,\"wage\":1,\"name\":\"国庆节后补班\",\"target\":\"国庆节\",\"date\":\"2023-10-07\"},\"10-08\":{\"holiday\":false,\"after\":true,\"wage\":1,\"name\":\"国庆节后补班\",\"target\":\"国庆节\",\"date\":\"2023-10-08\"},\"12-30\":{\"holiday\":true,\"name\":\"元旦\",\"wage\":2,\"date\":\"2023-12-30\",\"rest\":43},\"12-31\":{\"holiday\":true,\"name\":\"元旦\",\"wage\":2,\"date\":\"2023-12-31\"}}}";

    String HOLIDAY_PREFIX = "holiday_prefix_";

}
