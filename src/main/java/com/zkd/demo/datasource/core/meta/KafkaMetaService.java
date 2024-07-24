package com.zkd.demo.datasource.core.meta;


import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IKafka;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.zkd.demo.datasource.entity.enums.DataSourceEnum;
import com.zkd.demo.datasource.entity.meta.Table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class KafkaMetaService implements MetaService{

    @Override
    public List<String> getTables(String sourceCode) {
        return null;
    }

    @Override
    public Map<String, String> getTablesMap(Map<String, String> dataMap, Integer sourceType) {
        Map<String, String> res = new HashMap<>();
        IKafka client = ClientCache.getKafka(sourceType);
        ISourceDTO sourceDTO = DataSourceEnum.getSourceDTO(dataMap, sourceType, null, null, null);
        List list = client.getTopicList(sourceDTO);
        Optional.ofNullable(list).ifPresent(o->{
            o.forEach(v->res.putIfAbsent(v.toString(), ""));
        });
        return res;
    }

    @Override
    public Table getMetaData(String sourceCode, String tableName) {
        return null;
    }

    @Override
    public Table getMetaData(Map<String, String> dataMap, Integer sourceType, String tableName) {
        return null;
    }
}
