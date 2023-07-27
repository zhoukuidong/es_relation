package com.zkd.demo.repository;

import com.zkd.demo.entity.TestDataTimeGroupEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TestDataTimeGroupEntityRepository extends ElasticsearchRepository<TestDataTimeGroupEntity, Long> {
}
