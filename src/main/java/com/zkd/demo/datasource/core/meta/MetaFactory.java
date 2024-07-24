package com.zkd.demo.datasource.core.meta;

import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.zkd.demo.datasource.mapper.TDataSourceMapper;
import com.zkd.demo.datasource.mapper.TDataSourceTypeMapper;
import com.zkd.demo.datasource.service.DataSourceLinkValueService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class MetaFactory {

    @Resource
    private DataSourceLinkValueService dataSourceLinkValueService;
    @Resource
    private TDataSourceMapper dataSourceMapper;
    @Resource
    private TDataSourceTypeMapper dataSourceTypeMapper;

    private static final String RDS = "RDS";
    private static final String NO_SQL = "NO_SQL";
    private static final String MAXCOMPUTE = "MAXCOMPUTE";
    private static final String KAFKA = "KAFKA";

    private static final Map<String, MetaService> META_FACTORY_MAP = new HashMap<>();


    @PostConstruct
    public void init() {
        META_FACTORY_MAP.put(RDS, new RdsMetaService(dataSourceLinkValueService, dataSourceMapper, dataSourceTypeMapper));
        META_FACTORY_MAP.put(NO_SQL, new NoSqlMetaService());
        META_FACTORY_MAP.put(MAXCOMPUTE, new MaxcomputeMetaService(dataSourceLinkValueService, dataSourceMapper, dataSourceTypeMapper));
        META_FACTORY_MAP.put(KAFKA, new KafkaMetaService());
    }


    public static MetaService metaService(Integer type) {
        DataSourceType sourceType = DataSourceType.getSourceType(type);
        switch (sourceType) {
            case MySQL:
            case Oracle:
            case SQLServer:
            case DMDB:
            case PostgreSQL:
            case OceanBase:
            case GBase_8s:
            case KINGBASE8:
            case Gaussdb:
                return META_FACTORY_MAP.get(RDS);
            case MAXCOMPUTE:
                return META_FACTORY_MAP.get(MAXCOMPUTE);
            case KAFKA:
                return META_FACTORY_MAP.get(KAFKA);
            default:
                return META_FACTORY_MAP.get(NO_SQL);
        }
    }

}
