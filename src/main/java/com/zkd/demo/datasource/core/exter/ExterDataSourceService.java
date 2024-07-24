package com.zkd.demo.datasource.core.exter;

import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.zkd.demo.basic.core.exceptions.CommonException;
import com.zkd.demo.datasource.entity.enums.DataSourceEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class ExterDataSourceService {

    @Autowired
    List<ExterSourceService> exterSourceServiceList;

    public Boolean connTest(Map<String, String> dataMap, Integer sourceType) {
        try {
            DataSourceType dataSourceType = DataSourceType.getSourceType(sourceType);
            return null;
        } catch (Exception e) {
            if (!DataSourceEnum.getExterSourceValList().contains(sourceType)) {
                throw new CommonException(500, "Data source type is not supported");
            }
            for (ExterSourceService exterSourceService : exterSourceServiceList) {
                if (sourceType.equals(exterSourceService.getType())) {
                    return exterSourceService.connTest(dataMap);
                }
            }
        }
        return false;
    }

    public Object getClient(Map<String, String> dataMap, Integer sourceType) {
        try {
            DataSourceType dataSourceType = DataSourceType.getSourceType(sourceType);
            return null;
        } catch (Exception e) {
            if (!DataSourceEnum.getExterSourceValList().contains(sourceType)) {
                throw new CommonException(500, "Data source type is not supported");
            }
            for (ExterSourceService exterSourceService : exterSourceServiceList) {
                if (sourceType.equals(exterSourceService.getType())) {
                    return exterSourceService.getClient(dataMap);
                }
            }
        }
        return null;
    }
}
