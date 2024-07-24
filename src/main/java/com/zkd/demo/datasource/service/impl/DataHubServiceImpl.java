package com.zkd.demo.datasource.service.impl;

import com.aliyun.datahub.client.DatahubClient;
import com.aliyun.datahub.client.exception.DatahubClientException;
import com.aliyun.datahub.client.exception.ResourceNotFoundException;
import com.aliyun.datahub.client.model.*;
import com.zkd.demo.datasource.entity.enums.DataSourceEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DataHubServiceImpl extends DefaultDataSourceServiceImpl {
    @Override
    public Boolean connTest(Map<String, String> dataMap, Integer sourceType, Map<String, Object> expandConfig) {
        DatahubClient client = DataSourceEnum.getDatahubClient(dataMap, sourceType);
        try {
            client.listProject();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean createTopic(Map<String, String> dataMap, Integer sourceType, RecordSchema schema) {
        try {
            DatahubClient client = DataSourceEnum.getDatahubClient(dataMap, sourceType);
            deleteTopic(dataMap, sourceType);
            String note = dataMap.getOrDefault(DataSourceEnum.DATAHUB_NOTE, "");
            client.createTopic(dataMap.getOrDefault(DataSourceEnum.DATAHUB_PROJECT, ""),
                    dataMap.getOrDefault(DataSourceEnum.DATAHUB_TOPICNAME, ""),
                    Integer.parseInt(dataMap.getOrDefault(DataSourceEnum.DATAHUB_SHARDCOUNT, "")),
                    Integer.parseInt(dataMap.getOrDefault(DataSourceEnum.DATAHUB_LIFECYCLE, "")),
                    RecordType.TUPLE, schema, StringUtils.isNotBlank(note) ? note : "");
        } catch (Exception e) {
            throw e;
        }
        return true;
    }

    @Override
    public Boolean deleteTopic(Map<String, String> dataMap, Integer sourceType) {
        try {
            DatahubClient client = DataSourceEnum.getDatahubClient(dataMap, sourceType);
            client.deleteTopic(dataMap.getOrDefault(DataSourceEnum.DATAHUB_PROJECT, ""), dataMap.getOrDefault(DataSourceEnum.DATAHUB_TOPICNAME, ""));
        } catch (ResourceNotFoundException e) {
            return true;
        } catch (Exception e) {
            throw e;
        }
        return true;
    }

    @Override
    public Map<String, String> listTopic(Map<String, String> dataMap, Integer sourceType) {
        Map<String, String> res = new HashMap<>();
        try {
            DatahubClient client = DataSourceEnum.getDatahubClient(dataMap, sourceType);
            ListTopicResult listTopicResult = client.listTopic(dataMap.getOrDefault(DataSourceEnum.DATAHUB_PROJECT, ""));
            Optional.ofNullable(listTopicResult.getTopicNames()).ifPresent(o -> {
                o.forEach(v -> res.putIfAbsent(v, ""));
            });
        } catch (DatahubClientException e) {
            throw e;
        }
        return res;
    }

    @Override
    public GetTopicResult getTopic(Map<String, String> dataMap, Integer sourceType) {
        DatahubClient client = DataSourceEnum.getDatahubClient(dataMap, sourceType);
        GetTopicResult topic = client.getTopic(dataMap.getOrDefault(DataSourceEnum.DATAHUB_PROJECT, ""),
                dataMap.getOrDefault(DataSourceEnum.DATAHUB_TOPICNAME, ""));
        return topic;
    }

    @Override
    public ListProjectResult listProject(Map<String, String> dataMap, Integer sourceType) {
        DatahubClient client = DataSourceEnum.getDatahubClient(dataMap, sourceType);
        try {
            return client.listProject();
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public AppendFieldResult appendField(Map<String, String> dataMap, Integer sourceType, String projectName, String topicName, Field field) {
        DatahubClient client = DataSourceEnum.getDatahubClient(dataMap, sourceType);
        try {
            AppendFieldResult result = client.appendField(projectName, topicName, field);
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public AppendFieldResult appendField(Map<String, String> dataMap, Integer sourceType, String projectName, String topicName, List<Field> fields) {
        DatahubClient client = DataSourceEnum.getDatahubClient(dataMap, sourceType);
        return client.appendField(projectName, topicName, fields);
    }

    @Override
    public  GetRecordsResult getRecords(Map<String, String> dataMap, Integer sourceType, String projectName, String topicName, int limit) {
        DatahubClient client = DataSourceEnum.getDatahubClient(dataMap, sourceType);
        RecordSchema recordSchema = client.getTopic(projectName, topicName).getRecordSchema();
        String cursor = client.getCursor(projectName, topicName, "0", CursorType.OLDEST).getCursor();
        GetRecordsResult records = client.getRecords(projectName, topicName, "0", recordSchema,
                cursor, limit);

        return records;
    }

    @Override
    public GetRecordsResult getRecords(Map<String, String> dataMap, Integer sourceType, String projectName, String topicName, Long timestamp, int limit) {
        DatahubClient client = DataSourceEnum.getDatahubClient(dataMap, sourceType);
        RecordSchema recordSchema = client.getTopic(projectName, topicName).getRecordSchema();
        String cursor = client.getCursor(projectName, topicName, "0", CursorType.SYSTEM_TIME,timestamp).getCursor();
        GetRecordsResult records = client.getRecords(projectName, topicName, "0", recordSchema,
                cursor, limit);
        return records;
    }
}
