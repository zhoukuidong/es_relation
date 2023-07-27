package com.zkd.demo.repository;

import com.zkd.demo.entity.TestDataTimeGroupEntity;
import com.zkd.demo.entity.TestDataTimeGroupEntity2;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TestDataTimeGroupEntityRepository2 extends ElasticsearchRepository<TestDataTimeGroupEntity2, Long> {
}
