package com.zkd.demo.datasource.core.exter;

import java.util.Map;


public interface ExterSourceService {

    Boolean connTest(Map<String, String> dataMap);

    Integer getType();

    Object getClient(Map<String, String> dataMap);
}
