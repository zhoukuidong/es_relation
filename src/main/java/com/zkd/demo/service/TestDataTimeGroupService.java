package com.zkd.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service

public class TestDataTimeGroupService {

    @Resource
    private RestHighLevelClient restHighLevelClient;



}
