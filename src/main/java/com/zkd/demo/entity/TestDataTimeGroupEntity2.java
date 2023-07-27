package com.zkd.demo.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * 测试迁移
 * POST _reindex
 * {
 *   "source": {
 *     "index": "test_time_group"
 *   },
 *   "dest": {
 *     "index": "test_time_group_2"
 *   }
 * }
 *
 *
 * POST /_aliases
 * {
 *   "actions": [
 *     {
 *       "add": {
 *         "index": "test_time_group_2",
 *         "alias": "test_time_group"
 *       }
 *     }
 *   ]
 * }
 */
@Data
@Document(indexName = "test_time_group_2",shards = 3, replicas = 2)
public class TestDataTimeGroupEntity2 {

    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "业务编码,32位编码随机生成")
    private String bizCode;

    @Field(type = FieldType.Date,format = DateFormat.custom,pattern = "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis")
    @ApiModelProperty(value = "姓名")
    private LocalDateTime name;

    @Field(type = FieldType.Date,format = DateFormat.custom,pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "时间日期")
    private LocalDateTime createTime;

}
