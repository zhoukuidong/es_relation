/*
package com.zkd.demo.dialect;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.aliyun.centralhub.schedule.connector.dbz.jdbc.JdbcSinkConnectorConfig;
import com.aliyun.centralhub.schedule.connector.dbz.jdbc.JdbcSinkRecordDescriptor;
import com.aliyun.centralhub.schedule.connector.dbz.jdbc.dialect.DatabaseDialect;
import com.aliyun.centralhub.schedule.connector.dbz.jdbc.dialect.DatabaseDialectProvider;
import com.aliyun.centralhub.schedule.connector.dbz.jdbc.dialect.GeneralDatabaseDialect;
import com.aliyun.centralhub.schedule.connector.dbz.jdbc.dialect.SqlStatementBuilder;
import com.aliyun.centralhub.schedule.connector.dbz.jdbc.relational.TableDescriptor;
import io.debezium.custom.ExpandFieldInfoFilled;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.GBasedbtDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Gbase8sDatabaseDialect extends GeneralDatabaseDialect {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralDatabaseDialect.class);

    public Gbase8sDatabaseDialect(JdbcSinkConnectorConfig config) {
        super(config);
    }

    public static class Gbase8sDatabaseDialectProvider implements DatabaseDialectProvider {
        @Override
        public boolean supports(Dialect dialect) {
            return dialect instanceof GBasedbtDialect;
        }

        @Override
        public Class<?> name() {
            return Gbase8sDatabaseDialect.class;
        }

        @Override
        public DatabaseDialect instantiate(JdbcSinkConnectorConfig config) {
            return new Gbase8sDatabaseDialect(config);
        }
    }


    @Override
    public String getUpsertStatement(TableDescriptor table, JdbcSinkRecordDescriptor record) {

        String targetTableName = table.getId().getTableName();
        String tableName2FieldName2FieldTypeJson = this.getConfig().getTableName2FieldName2FieldTypeJson();
        Map<String, String> fieldName2FieldTypeMap = new HashMap<>();
        if (Objects.nonNull(tableName2FieldName2FieldTypeJson)) {
            Map<String, Map<String, String>> tableName2FieldName2FieldTypeMap = JSON.parseObject(tableName2FieldName2FieldTypeJson, Map.class);
            fieldName2FieldTypeMap = tableName2FieldName2FieldTypeMap.get(targetTableName);
        }
        ExpandFieldInfoFilled.ExpandFieldInfo expandFieldInfo = ExpandFieldInfoFilled.buildExpandFieldInfo(this.getConfig().getExpFieldJson(), targetTableName);
        Map<String, String> expSourceFieldName2ValueMap = expandFieldInfo.getExpSourceFieldName2ValueMap();
        Map<String, String> sourceToTargetFieldMapping = this.getConfig().getSourceToTargetFieldMapping();
        final SqlStatementBuilder builder = new SqlStatementBuilder(getConfig());
        String originTableName = StringUtils.substringAfterLast(record.getTopicName(), ".");

        String identifier = toIdentifier(table.getId());

        builder.append("MERGE INTO ");
        builder.append(targetTableName);
        builder.append(" USING (SELECT ");
        builder.appendLists(originTableName, ", ",
                record.getKeyFieldNames(),
                record.getNonKeyFieldNames(),
                (name) -> columnQueryBindingFromField(name, record, table) + " " + mappingColumnNameFromField(name, record).replace(targetTableName + ".", ""));
        builder.append(" FROM dual) ").append("INCOMING ON (");
        builder.appendList(originTableName, " AND ", record.getKeyFieldNames(), (name) -> getUpsertIncomingClause(name, table, record));
        builder.append(")");
        if (!record.getNonKeyFieldNames().isEmpty()) {
            builder.append(" WHEN MATCHED THEN UPDATE SET ");
        }
        builder.appendList(originTableName, ",", record.getNonKeyFieldNames(), (name) -> getUpsertIncomingClause(name, table, record));

        SinkRecord r = record.getRecord();
        String topic = r.topic();
        String[] split = topic.split("\\.");
        String sourceTableName = split[split.length - 1]  +  ".";

        processFieldValueGbase(table, record, fieldName2FieldTypeMap, expandFieldInfo, expSourceFieldName2ValueMap, sourceToTargetFieldMapping, builder, identifier, true, sourceTableName);

        builder.append(" WHEN NOT MATCHED THEN INSERT (");
        builder.appendLists(originTableName, ",", record.getNonKeyFieldNames(), record.getKeyFieldNames(),
                (name) -> mappingColumnNameFromField(name, record).replace(targetTableName + ".", ""));

        if (CollUtil.isNotEmpty(expSourceFieldName2ValueMap)) {
            for (Map.Entry<String, String> Name2ValueEntry : expSourceFieldName2ValueMap.entrySet()) {
                String sourceFieldName = Name2ValueEntry.getKey();
                if (sourceToTargetFieldMapping.containsKey(sourceFieldName)) {
                    builder.append("," + sourceToTargetFieldMapping.get(sourceFieldName));
                }else if (sourceToTargetFieldMapping.containsKey(sourceTableName + sourceFieldName)) {
                    String[] tarFiled = sourceToTargetFieldMapping.get(sourceTableName + sourceFieldName).split("\\.");
                    builder.append("," + tarFiled[tarFiled.length-1]);
                }
            }
        }

        builder.append(") VALUES (");
        builder.appendLists(originTableName, ",", record.getNonKeyFieldNames(), record.getKeyFieldNames(),
                (name) -> mappingColumnNameFromField(name, "INCOMING.", record).replace(targetTableName + ".", ""));

        processFieldValueGbase(table, record, fieldName2FieldTypeMap, expandFieldInfo, expSourceFieldName2ValueMap, sourceToTargetFieldMapping, builder, identifier, false, sourceTableName);

        builder.append(")");
        return builder.build().replace("\"","");
    }

    */
/*

    private void processFieldValueGbase(TableDescriptor table, JdbcSinkRecordDescriptor record, Map<String, String> fieldName2FieldTypeMap,
                                         ExpandFieldInfoFilled.ExpandFieldInfo expandFieldInfo, Map<String, String> expSourceFieldName2ValueMap,
                                         Map<String, String> sourceToTargetFieldMapping, SqlStatementBuilder builder, String identifier, boolean updateFlag, String sourceTableName) {
        if (CollUtil.isNotEmpty(expSourceFieldName2ValueMap)) {
            for (Map.Entry<String, String> Name2ValueEntry : expSourceFieldName2ValueMap.entrySet()) {
                String sourceFieldName = Name2ValueEntry.getKey();
                if (sourceToTargetFieldMapping.containsKey(sourceFieldName)) {
                    dealProcessFiled(record, fieldName2FieldTypeMap, expandFieldInfo, sourceToTargetFieldMapping, builder, identifier,
                            updateFlag, Name2ValueEntry, sourceFieldName, sourceTableName, false);
                }
                if (sourceToTargetFieldMapping.containsKey(sourceTableName + sourceFieldName)) {
                    dealProcessFiled(record, fieldName2FieldTypeMap, expandFieldInfo, sourceToTargetFieldMapping, builder, identifier,
                            updateFlag, Name2ValueEntry, sourceFieldName,  sourceTableName, true);
                }
            }
        }
    }

    private void dealProcessFiled(JdbcSinkRecordDescriptor record, Map<String, String> fieldName2FieldTypeMap, ExpandFieldInfoFilled.ExpandFieldInfo expandFieldInfo, Map<String, String> sourceToTargetFieldMapping, SqlStatementBuilder builder,
                                  String identifier, boolean updateFlag, Map.Entry<String, String> Name2ValueEntry, String sourceFieldName, String sourceTableName, Boolean isMulti) {
        String targetField = sourceToTargetFieldMapping.get(isMulti ? sourceTableName + sourceFieldName : sourceFieldName);
        targetField = isMulti ?  targetField.split("\\.")[1] : targetField;
        switch (sourceFieldName){
            case ExpandFieldInfoFilled.LOGCREATETIME:
                //数据读取时间
                Long timestamp = record.getRecord().timestamp();
                if (Objects.isNull(timestamp)) {
                    timestamp = System.currentTimeMillis();
                }
                String logCreateTime = ExpandFieldInfoFilled.parseSysTime(timestamp, expandFieldInfo.getSzgtCdcLogCreateTimeType());
                if (updateFlag) {
                    buildFieldByFieldTypeUpdate(builder, fieldName2FieldTypeMap, logCreateTime, targetField, identifier);
                } else {
                    buildFieldByFieldTypeInsert(builder, fieldName2FieldTypeMap, logCreateTime, targetField);
                }
                break;
            case ExpandFieldInfoFilled.LOGWRITETIME:
                //数据写入时间
                long currentTimeMillis = System.currentTimeMillis();
                String logWriteTime = ExpandFieldInfoFilled.parseSysTime(currentTimeMillis, expandFieldInfo.getSzgtCdcLogWriteTimeType());
                if (updateFlag) {
                    buildFieldByFieldTypeUpdate(builder, fieldName2FieldTypeMap, logWriteTime, targetField, identifier);
                } else {
                    buildFieldByFieldTypeInsert(builder, fieldName2FieldTypeMap, logWriteTime, targetField);
                }

                break;
            case ExpandFieldInfoFilled.SZGT_CDC_OP:
                //op
                Struct kafkaValue = (Struct) record.getRecord().value();
                if (Objects.nonNull(kafkaValue)) {
                    String op = "";
                    try {
                        op = (String) kafkaValue.get(ExpandFieldInfoFilled.KAFKA_OP);
                    } catch (Exception e) {
                        //全量同步的时候没有__op会走到这
                        op = "r";
                    }
                    if (updateFlag) {
                        builder.append("," + identifier + "." + targetField + "=" + "'" + op + "'");
                    } else {
                        builder.append("," + "'" + op + "'");
                    }
                } else {
                    //自定义拓展字段
                    if (updateFlag) {
                        builder.append("," + identifier + "." + targetField + "=" + "'r'");
                    } else {
                        builder.append("," + "'r'");
                    }
                }
                break;
            default:
                if (updateFlag) {
                    buildFieldByFieldTypeUpdate(builder, fieldName2FieldTypeMap, Name2ValueEntry.getValue(), targetField, identifier);
                } else {
                    buildFieldByFieldTypeInsert(builder, fieldName2FieldTypeMap, Name2ValueEntry.getValue(), targetField);
                }
                break;
        }
    }


    */
/*

    private void buildFieldByFieldTypeUpdate(SqlStatementBuilder builder, Map<String, String> fieldName2FieldTypeMap, String targetValue, String targetFieldName, String identifier) {
        if (CollUtil.isEmpty(fieldName2FieldTypeMap)) {
            builder.append("," + identifier + "." + targetFieldName + "=" + "'" + targetValue + "'");
        } else {
            String fieldType = fieldName2FieldTypeMap.get(targetFieldName);
            if (StringUtils.contains(fieldType, ExpandFieldInfoFilled.OracleTimeType.TIMESTAMP.getType())) {
                builder.append("," + identifier + "." + targetFieldName + "=" + ExpandFieldInfoFilled.OracleTimeType.TIMESTAMP.getType() + "'" + targetValue + "'");
            } else if (StringUtils.equals(fieldType, ExpandFieldInfoFilled.OracleTimeType.DATE.getType())) {
                builder.append("," + identifier + "." + targetFieldName + "=" + ExpandFieldInfoFilled.OracleTimeType.DATE.getType() + "'" + targetValue + "'");
            } else {
                builder.append("," + identifier + "." + targetFieldName + "=" + "'" + targetValue + "'");
            }
        }
    }


    */
/*

    private void buildFieldByFieldTypeInsert(SqlStatementBuilder builder, Map<String, String> fieldName2FieldTypeMap, String targetValue, String targetFieldName) {
        builder.append("," + "'" + targetValue + "'");
    }

    private String getUpsertIncomingClause(String fieldName, TableDescriptor table, JdbcSinkRecordDescriptor record) {
        String columnName = mappingColumnNameFromField(fieldName, record);
        columnName = columnName.replace(table.getId().getTableName() + ".", "");
        return table.getId().getTableName() + "." + columnName + "=INCOMING." + columnName;
    }


}
*/
