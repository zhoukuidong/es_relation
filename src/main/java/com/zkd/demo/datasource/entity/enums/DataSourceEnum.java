package com.zkd.demo.datasource.entity.enums;

import com.alibaba.fastjson.JSON;
import com.aliyun.datahub.client.DatahubClient;
import com.aliyun.datahub.client.DatahubClientBuilder;
import com.aliyun.datahub.client.auth.AliyunAccount;
import com.aliyun.datahub.client.common.DatahubConfig;
import com.aliyun.datahub.client.util.JsonUtils;

import com.dtstack.dtcenter.loader.dto.source.*;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.google.common.collect.Lists;
import com.zkd.demo.basic.core.exceptions.CommonException;
import com.zkd.demo.datasource.core.constant.Constant;
import com.zkd.demo.datasource.entity.DTO.AliyunOssSourceDTO;
import com.zkd.demo.datasource.entity.DTO.MinioSourceDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public enum DataSourceEnum {
    /**
     * mysql
     */
    MySQL(DataSourceType.MySQL.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataMap, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
            String jdbcUrl = dataMap.getOrDefault(JDBC_URL, "");
            String username = dataMap.getOrDefault(JDBC_USERNAME, "");
            String password = dataMap.getOrDefault(JDBC_PASSWORD, "");
            Mysql5SourceDTO sourceDTO = Mysql5SourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .schema(schema)
                    .username(username)
                    .password(password)
                    .sourceType(DataSourceType.MySQL.getVal())
                    .kerberosConfig(confMap)
//                    .properties(JsonUtils.toJson(expandConfig))
                    .build();
            return sourceDTO;
        }
    },

    /**
     * sqlserver
     */
    SQLServer(DataSourceType.SQLServer.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataMap, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
            String jdbcUrl = dataMap.getOrDefault(JDBC_URL, "");
            String username = dataMap.getOrDefault(JDBC_USERNAME, "");
            String password = dataMap.getOrDefault(JDBC_PASSWORD, "");
            SqlserverSourceDTO sourceDTO = SqlserverSourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .schema(schema)
                    .username(username)
                    .password(password)
                    .sourceType(DataSourceType.SQLServer.getVal())
                    .kerberosConfig(confMap)
//                    .properties(JsonUtils.toJson(expandConfig))
                    .build();
            return sourceDTO;
        }
    },

    /**
     * oracle
     */
    Oracle(DataSourceType.Oracle.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataMap, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
            if (StringUtils.isBlank(schema)) {
                schema = dataMap.get("schema");
            }
            String jdbcUrl = dataMap.getOrDefault(JDBC_URL, "");
            String username = dataMap.getOrDefault(JDBC_USERNAME, "");
            String password = dataMap.getOrDefault(JDBC_PASSWORD, "");
            Map<String, String> propMap = new HashMap<>();
            propMap.put("remarksReporting", "true");
            OracleSourceDTO oracleSourceDTO = OracleSourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .sourceType(DataSourceType.Oracle.getVal())
                    .schema(schema)
                    .properties(JsonUtils.toJson(propMap))
//                    .properties(JsonUtils.toJson(expandConfig))
                    .build();
            return oracleSourceDTO;
        }
    },

    /**
     * 达梦数据库
     */
    DMDB(DataSourceType.DMDB.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataMap, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
            String jdbcUrl = dataMap.getOrDefault(JDBC_URL, "");
            String username = dataMap.getOrDefault(JDBC_USERNAME, "");
            String password = dataMap.getOrDefault(JDBC_PASSWORD, "");
            DmSourceDTO sourceDTO = DmSourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .schema(schema)
                    .sourceType(DataSourceType.DMDB.getVal())
                    .kerberosConfig(confMap)
                    .build();
            return sourceDTO;
        }
    },

    /**
     * maxcompute
     */
    MAXCOMPUTE(DataSourceType.MAXCOMPUTE.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataMap, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
            return OdpsSourceDTO
                    .builder()
                    .config(JSON.toJSONString(dataMap))
                    .sourceType(DataSourceType.MAXCOMPUTE.getVal())
                    .kerberosConfig(confMap)
                    .schema(schema)
                    .build();
        }
    },

    /**
     * kafka
     */
    KAFKA(DataSourceType.KAFKA.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataMap, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
            String userAuth = dataMap.getOrDefault(KAFKA_USERAUTH, "");
            boolean auth = Boolean.toString(true).equals(userAuth);
            return KafkaSourceDTO.builder()
                    .brokerUrls(dataMap.getOrDefault(KAFKA_BOOTSTRAPSERVERS, ""))
                    .username(auth ? dataMap.getOrDefault(KAFKA_USERNAME, "") : null)
                    .password(auth ? dataMap.getOrDefault(KAFKA_PASSWORD, "") : null)
                    .build();
        }
    },

    /**
     * Datahub
     */
    DATAHUB(Constant.DATAHUB_VAL) {
        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return null;
        }

        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
            return null;
        }

        @Override
        public DatahubClient getDatahubClient(Map<String, String> dataMap) {
            return DatahubClientBuilder.newBuilder()
                    .setDatahubConfig(
                            new DatahubConfig(dataMap.getOrDefault(DATAHUB_ENDPOINT, ""),
                                    new AliyunAccount(dataMap.getOrDefault(DATAHUB_ACCESSKEYID, ""),
                                            dataMap.getOrDefault(DATAHUB_ACCESSKEYSECRET, "")))
                    ).build();
        }
    },

    /**
     * PostgreSQL
     */
    PostgreSQL(DataSourceType.PostgreSQL.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataMap, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
            String jdbcUrl = dataMap.getOrDefault(JDBC_URL, "");
            String username = dataMap.getOrDefault(JDBC_USERNAME, "");
            String password = dataMap.getOrDefault(JDBC_PASSWORD, "");
            PostgresqlSourceDTO sourceDTO = PostgresqlSourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .schema(schema)
                    .username(username)
                    .password(password)
                    .sourceType(DataSourceType.PostgreSQL.getVal())
                    .build();
            return sourceDTO;
        }
    },

    /**
     * Gaussdb
     */
    Gaussdb(DataSourceType.Gaussdb.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataMap, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
            String jdbcUrl = dataMap.getOrDefault(JDBC_URL, "");
            String username = dataMap.getOrDefault(JDBC_USERNAME, "");
            String password = dataMap.getOrDefault(JDBC_PASSWORD, "");
            GaussdbSourceDTO sourceDTO = GaussdbSourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .schema(schema)
                    .username(username)
                    .password(password)
                    .sourceType(DataSourceType.Gaussdb.getVal())
                    .build();
            return sourceDTO;
        }
    },

    /**
     * GBase8s
     */
    GBase8s(DataSourceType.GBase_8s.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataMap, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
            String jdbcUrl = dataMap.getOrDefault(JDBC_URL, "");
            String username = dataMap.getOrDefault(JDBC_USERNAME, "");
            String password = dataMap.getOrDefault(JDBC_PASSWORD, "");
            GBase8sSourceDTO source = GBase8sSourceDTO.builder()
                    .url(jdbcUrl)
                    .schema(schema)
                    .username(username)
                    .password(password)
                    .build();
            return source;
        }
    },

    /**
     * OceanBase
     */
    OceanBase(DataSourceType.OceanBase.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataMap, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
            String jdbcUrl = dataMap.getOrDefault(JDBC_URL, "");
            String username = dataMap.getOrDefault(JDBC_USERNAME, "");
            String password = dataMap.getOrDefault(JDBC_PASSWORD, "");
            OceanBaseSourceDTO source = OceanBaseSourceDTO.builder()
                    .url(jdbcUrl)
                    .schema(schema)
                    .username(username)
                    .password(password)
                    .build();
            return source;
        }
    },

    /**
     * Kingbase8
     */
    Kingbase8(DataSourceType.KINGBASE8.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataMap, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
            String jdbcUrl = dataMap.getOrDefault(JDBC_URL, "");
            String username = dataMap.getOrDefault(JDBC_USERNAME, "");
            String password = dataMap.getOrDefault(JDBC_PASSWORD, "");
            KingbaseSourceDTO source = KingbaseSourceDTO.builder()
                    .url(jdbcUrl)
                    .schema(schema)
                    .username(username)
                    .password(password)
                    .build();
            return source;
        }
    },

    ALIYUN_OSS(200) {
        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataMap, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
            String endpoint = dataMap.getOrDefault(ENDPOINT, "");
            String accessKeyId = dataMap.getOrDefault(ACCESS_KEY_ID, "");
            String secretAccessKey = dataMap.getOrDefault(SECRET_ACCESS_KEY, "");
            String bucketName = dataMap.getOrDefault(BUCKET_NAME, "");
            AliyunOssSourceDTO source = new AliyunOssSourceDTO(endpoint, accessKeyId, secretAccessKey, bucketName);
            return source;
        }

    },

    MINIO(201) {
        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataMap, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
            String endpoint = dataMap.getOrDefault(ENDPOINT, "");
            String accessKey = dataMap.getOrDefault(ACCESS_KEY, "");
            String secretKey = dataMap.getOrDefault(SECRET_KEY, "");
            String bucketName = dataMap.getOrDefault(BUCKET_NAME, "");
            MinioSourceDTO source = new MinioSourceDTO(endpoint, accessKey, secretKey, bucketName);
            return source;
        }

    },

    ;

    private Integer val;

    public Integer getVal() {
        return val;
    }

    DataSourceEnum(Integer val) {
        this.val = val;
    }


    public static final String JDBC_URL = "url";
    public static final String JDBC_USERNAME = "username";
    public static final String JDBC_PASSWORD = "password";
    // ssl 认证文件路径
    public static final String SSL_LOCAL_DIR = "sslLocalDir";
    public static final String URL = "url";
    public static final String BROKER_LIST = "brokerList";
    public static final String ADDRESS = "address";


    //kafka
    public static final String KAFKA_TOPICNAME = "topicName";
    public static final String KAFKA_PARTITIONS = "partitions";
    public static final String KAFKA_REPLICATIONFACTOR = "replicationFactor";
    public static final String KAFKA_BOOTSTRAPSERVERS = "bootstrapServers";
    public static final String KAFKA_USERAUTH = "userAuth";
    public static final String KAFKA_USERNAME = "username";
    public static final String KAFKA_PASSWORD = "password";
    public static final String KAFKA_CALSSNAME = "calssname";

    //datahub
    public static final String DATAHUB_TOPICNAME = "topicName";
    public static final String DATAHUB_ENDPOINT = "endpoint";
    public static final String DATAHUB_PROJECT = "project";
    public static final String DATAHUB_ACCESSKEYID = "accessKeyID";
    public static final String DATAHUB_ACCESSKEYSECRET = "accessKeySecret";
    public static final String DATAHUB_SHARDCOUNT = "shardCount";
    public static final String DATAHUB_SHARDCOUNT_VALUE = "1";
    public static final String DATAHUB_LIFECYCLE = "lifeCycle";
    public static final String DATAHUB_LIFECYCLE_VALUE = "7";
    public static final String DATAHUB_NOTE = "note";

    //Odps
    public static final String ODPS_ENDPOINT = "endpoint";
    public static final String ODPS_PROJECT = "project";
    public static final String ODPS_ACCESSKEYID = "accessId";
    public static final String ODPS_ACCESSKEYSECRET = "accessKey";

    public static final String ENDPOINT = "endpoint";
    public static final String ACCESS_KEY = "accessKey";
    public static final String SECRET_KEY = "secretKey";
    public static final String ACCESS_KEY_ID = "accessKeyId";
    public static final String SECRET_ACCESS_KEY = "secretAccessKey";
    public static final String BUCKET_NAME = "bucketName";


    public abstract ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, Map<String, Object> expandConfig);

    public abstract ISourceDTO getSourceDTO(Map<String, String> dataMap, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig);

    DatahubClient getDatahubClient(Map<String, String> dataMap) {
        return null;
    }


    public static DataSourceEnum getDataSource(Integer val) {
        for (DataSourceEnum dataSourceEnum : values()) {
            if (dataSourceEnum.val.equals(val)) {
                return dataSourceEnum;
            }
        }
        throw new CommonException(500, "数据源类型不存在");
    }

    /**
     * 根据数据源获取对应的sourceDTO，供外调用
     */
    public static ISourceDTO getSourceDTO(Map<String, String> dataMap, Integer sourceType, Map<String, Object> confMap, Map<String, Object> expandConfig) {
        DataSourceEnum dataSourceEnum = getDataSource(sourceType);
        return dataSourceEnum.getSourceDTO(dataMap, confMap, expandConfig);
    }

    /**
     * 根据数据源获取对应的sourceDTO，供外调用
     */
    public static ISourceDTO getSourceDTO(Map<String, String> dataMap, Integer sourceType, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
        DataSourceEnum dataSourceEnum = getDataSource(sourceType);
        return dataSourceEnum.getSourceDTO(dataMap, confMap, schema, expandConfig);
    }

    public static DatahubClient getDatahubClient(Map<String, String> dataMap, Integer sourceType) {
        DataSourceEnum dataSourceEnum = getDataSource(sourceType);
        return dataSourceEnum.getDatahubClient(dataMap);
    }

    /**
     * 获取exter得源val
     */
    public static List<Integer> getExterSourceValList() {
        return Lists.newArrayList(
                ALIYUN_OSS.val,
                MINIO.val
        );
    }
}
