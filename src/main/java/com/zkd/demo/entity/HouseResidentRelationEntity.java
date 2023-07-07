package com.zkd.demo.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;

@Data
@Document(indexName = "house_resident_relation",shards = 3, replicas = 2)
public class HouseResidentRelationEntity {

    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "居民信息编码，关联房屋用,32位编码随机生成")
    private String residentCode;

    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "居民的姓名")
    private String residentName;

    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "证件类型,居民身份证-ID;港澳台身份证-HMTRP;护照-PR")
    private String idType;

    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "证件号码")
    private String idNumber;

    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "性别，1-男性，2-女性")
    private String sex;

    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "联系方式")
    private String phone;

    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "民族")
    private String nation;

    @Field(type = FieldType.Date,format = DateFormat.custom,pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "出生日期")
    private LocalDate birthday;

    @Field(type = FieldType.Integer)
    @ApiModelProperty(value = "年龄")
    private Integer age;

    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "居民类型，CR-户籍，NCR-非户籍")
    private String residentType;

    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "房屋编码，关联居民用,32位编码随机生成")
    private String houseCode;

    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "所属省份")
    private String provinceCode;

    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "所属市")
    private String cityCode;

    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "所属区县")
    private String countyCode;

    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "所属镇街")
    private String streetCode;

    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "所属网格，存储组织编码")
    private String gridCode;

    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    @ApiModelProperty(value = "所属网格名称")
    private String gridName;

    /**
     * @MultiField
     * 该字段既能作为text类型分词又能作为keyword类型完整匹配
     */
    @MultiField(mainField = @Field(type=FieldType.Text),otherFields = {@InnerField(suffix = "keyword", type = FieldType.Keyword,ignoreAbove = 1024)})
    @ApiModelProperty(value = "详细地址")
    private String address;


    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "社区")
    private String communityCode;

    @Field(type = FieldType.Text)
    @ApiModelProperty(value = "道路")
    private String road;

    @Field(type = FieldType.Text)
    @ApiModelProperty(value = "街巷")
    private String lanes;

    @Field(type = FieldType.Text)
    @ApiModelProperty(value = "小区（自然村）")
    private String residentialQuarters;

    @Field(type = FieldType.Text)
    @ApiModelProperty(value = "建筑物（楼栋）")
    private String building;

    @Field(type = FieldType.Text)
    @ApiModelProperty(value = "单位（单元）")
    private String buildingUnit;

    @Field(type = FieldType.Text)
    @ApiModelProperty(value = "房间（户室号）")
    private String room;



}
