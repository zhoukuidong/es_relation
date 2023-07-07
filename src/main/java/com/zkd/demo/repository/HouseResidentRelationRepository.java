package com.zkd.demo.repository;

import com.zkd.demo.entity.HouseResidentRelationEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface HouseResidentRelationRepository extends ElasticsearchRepository<HouseResidentRelationEntity, Long> {

}
