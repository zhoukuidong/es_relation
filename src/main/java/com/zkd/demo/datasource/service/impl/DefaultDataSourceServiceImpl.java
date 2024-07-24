package com.zkd.demo.datasource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.datahub.client.model.RecordSchema;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.ClientFactory;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.client.IKafka;
import com.dtstack.dtcenter.loader.dto.KafkaTopicDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.dto.source.KafkaSourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.google.common.collect.Lists;
import com.zkd.demo.basic.core.exceptions.CommonException;
import com.zkd.demo.basic.generator.NumberGenerator;
import com.zkd.demo.basic.util.Assert;
import com.zkd.demo.crypto2.type.Sm4Encryptor;
import com.zkd.demo.datasource.core.common.IDataSourceEasyService;
import com.zkd.demo.datasource.core.common.IParser;
import com.zkd.demo.datasource.core.cover.DataSourceCover;
import com.zkd.demo.datasource.core.exter.ExterDataSourceService;
import com.zkd.demo.datasource.core.meta.MetaFactory;
import com.zkd.demo.datasource.dialect.MySQLDialectGenerator;
import com.zkd.demo.datasource.dialect.PostgreSQLDialectGenerator;
import com.zkd.demo.datasource.entity.DO.TDataSourceDO;
import com.zkd.demo.datasource.entity.DO.TDataSourceLinkValueDO;
import com.zkd.demo.datasource.entity.DO.TDataSourceTypeCodeDO;
import com.zkd.demo.datasource.entity.DO.TDataSourceTypeDO;
import com.zkd.demo.datasource.entity.DTO.ConnDTO;
import com.zkd.demo.datasource.entity.DTO.Link;
import com.zkd.demo.datasource.entity.VO.DataSourceDropVO;
import com.zkd.demo.datasource.entity.VO.DataSourceLinkVO;
import com.zkd.demo.datasource.entity.VO.DataSourceListVO;
import com.zkd.demo.datasource.entity.VO.DataSourceTypeVO;
import com.zkd.demo.datasource.entity.enums.DataSourceEnum;
import com.zkd.demo.datasource.entity.enums.DeleteEnum;
import com.zkd.demo.datasource.entity.enums.NumberGeneratorEnum;
import com.zkd.demo.datasource.entity.meta.Table;
import com.zkd.demo.datasource.entity.page.PageResult;
import com.zkd.demo.datasource.entity.page.PageUtils;
import com.zkd.demo.datasource.entity.request.DataSourceDeleteRequest;
import com.zkd.demo.datasource.entity.request.DataSourceLinkRequest;
import com.zkd.demo.datasource.entity.request.DataSourceRenameRequest;
import com.zkd.demo.datasource.mapper.TDataSourceMapper;
import com.zkd.demo.datasource.mapper.TDataSourceTypeCodeMapper;
import com.zkd.demo.datasource.mapper.TDataSourceTypeMapper;
import com.zkd.demo.datasource.service.DataSourceLinkValueService;
import com.zkd.demo.datasource.service.DataSourceService;
import com.zkd.demo.mybatis.properties.CustomMybatisCryptoProperties;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zkd.demo.datasource.core.constant.Constant.DEFAULT_CODE_LENGTH;


public class DefaultDataSourceServiceImpl implements DataSourceService, DataSourceCover, IDataSourceEasyService, IParser {

    @Resource
    private TDataSourceMapper dataSourceMapper;
    @Resource
    private TDataSourceTypeMapper dataSourceTypeMapper;
    @Resource
    private TDataSourceTypeCodeMapper dataSourceTypeCodeMapper;
    @Resource
    private DataSourceLinkValueService dataSourceLinkValueService;
    @Resource
    private CustomMybatisCryptoProperties mybatisCryptoProperties;
    @Resource
    private Sm4Encryptor sm4Encryptor;
    @Resource
    private ExterDataSourceService exterDataSourceService;


    /**
     * 用户扩展
     */
    protected String getCurrentUserCode() {
        return null;
    }

    /**
     * 获取当前用户所能操作的数据源列表，做数据权限控制
     * null 则表示不做权限控制
     */
    protected List<String> getDataSourceListByUserCode(HttpServletRequest request) {
        return null;
    }

    /**
     * 条件分页查询数据源列表
     * 默认查询条件为 “数据源名称”
     */
    @Override
    public PageResult<? extends DataSourceListVO> queryList(HttpServletRequest request) {
        final JSONObject postParam = getPostParamByHSR(request);

        String sourceName = postParam.getString(SOURCE_NAME);
        int pageNum = null == postParam.getInteger(PAGE_NUM) ? DEFAULT_PAGE_NUM : postParam.getInteger(PAGE_NUM);
        int pageSize = null == postParam.getInteger(PAGE_SIZE) ? DEFAULT_PAGE_SIZE : postParam.getInteger(PAGE_SIZE);

        List<String> dataSourceList = getDataSourceListByUserCode(null);

        if (null != dataSourceList && dataSourceList.size() == 0) {
            //无对应数据权限
            return new PageResult<>();
        }

        Page<TDataSourceDO> page = queryDataSourcePageByNSl(dataSourceMapper, sourceName, dataSourceList, pageNum, pageSize);

        return PageUtils.getPageResult(pageDOCoverToVO(dataSourceTypeMapper, page));
    }

    /**
     * 获取所有数据源类型
     */
    @Override
    public List<DataSourceTypeVO> queryTypeList() {
        List<TDataSourceTypeDO> dataSourceTypeDOList = queryDataSourceTypeList(dataSourceTypeMapper);
        if (CollectionUtils.isEmpty(dataSourceTypeDOList)) {
            return null;
        }
        return dataSourceTypeDOList.stream().map(this::typeDoCoverToVO).collect(Collectors.toList());
    }


    /**
     * 根据数据源类型获取数据源连接模板
     */
    @Override
    public List<Link> queryTemplateByType(String sourceTypeCode) {
        List<TDataSourceTypeCodeDO> dataSourceTypeCodeDOList = queryDataSourceTypeCodeListByTc(dataSourceTypeCodeMapper, sourceTypeCode);
        if (CollectionUtils.isEmpty(dataSourceTypeCodeDOList)) {
            return null;
        }
        return dataSourceTypeCodeDOList.stream().map(this::typeCodeCoverToLink).collect(Collectors.toList());
    }


    /**
     * 获取数据源连接信息
     */
    @Override
    public DataSourceLinkVO queryLinkDetail(String sourceCode) {
        //数据权限判定
        List<String> dataSourceList = getDataSourceListByUserCode(null);
        Assert.isTrue(null == dataSourceList || dataSourceList.contains(sourceCode), "无数据权限");

        DataSourceLinkVO result = new DataSourceLinkVO();
        List<Link> links = dataSourceLinkValueService.queryLinkListBySourceCode(sourceCode);
        //获取数据源类型
        Integer sourceType = getSourceTypeBySourceCode(sourceCode);
        return result.setSourceCode(sourceCode).setSourceType(sourceType).setLinkList(links);
    }

    /**
     * 新增数据源
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String insert(DataSourceLinkRequest request) {
        String currentUserCode = getCurrentUserCode();

        //1. 创建数据源
        String sourceCode = NumberGenerator.getCode(NumberGeneratorEnum.DS, DEFAULT_CODE_LENGTH);
        dataSourceMapper.insert(new TDataSourceDO().setSourceName(request.getSourceName())
                .setSourceCode(sourceCode)
                .setSourceTypeCode(request.getSourceTypeCode())
                .setCreator(currentUserCode).setUpdator(currentUserCode)
                .setCreateAt(LocalDateTime.now()).setUpdateAt(LocalDateTime.now())
                .setDeleteFlag(DeleteEnum.N.getKey()));

        //2. 创建数据源link value
        List<TDataSourceLinkValueDO> list = Lists.newArrayList();
        request.getLinkList().forEach(item -> {
            TDataSourceLinkValueDO temp = new TDataSourceLinkValueDO().setSourceCode(sourceCode)
                    .setSourceTypeKeyCode(item.getSourceTypeKeyCode())
                    .setCreator(currentUserCode).setUpdator(currentUserCode)
                    .setCreateAt(LocalDateTime.now()).setUpdateAt(LocalDateTime.now())
                    .setLinkValue(mybatisCryptoProperties.getEnable() ? sm4Encryptor.encrypt(item.getLinkValue()) : item.getLinkValue())
                    .setDeleteFlag(DeleteEnum.N.getKey());

            list.add(temp);
        });

        if (CollectionUtils.isNotEmpty(list)) {
            dataSourceLinkValueService.saveBatch(list);
        }

        //3. 额外操作
        request.setSourceCode(sourceCode);
        insertExpandProcessor(request);

        //返回新增后的sourceCode
        return sourceCode;
    }


    /**
     * 新增数据源时的额外操作
     */
    protected void insertExpandProcessor(DataSourceLinkRequest request) {

    }


    /**
     * 测试连接
     */
    @Override
    public Boolean connTest(DataSourceLinkRequest request) {
        Boolean result = false;
        //获取当前数据源类型
        TDataSourceTypeDO dataSourceTypeDO = queryDataSourceTypeByTc(dataSourceTypeMapper, request.getSourceTypeCode());

        //link list转map
        Map<String, String> map = linkListCoverToMap(request.getLinkList());
        //针对datasourcex不支持得源做适配
        Boolean aBoolean = exterDataSourceService.connTest(map, Integer.valueOf(dataSourceTypeDO.getSourceType()));
        if (null != aBoolean) {
            return aBoolean;
        }
        //获取schema
        String schema = map.getOrDefault(SCHEMA_NAME, null);
        IClient client = ClientCache.getClient(Integer.parseInt(dataSourceTypeDO.getSourceType()));
        ISourceDTO sourceDTO = DataSourceEnum.getSourceDTO(map, Integer.valueOf(dataSourceTypeDO.getSourceType()), null, schema, null);
        try {
            result = client.testCon(sourceDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommonException(500, getTestConnExceptionMessage(e));
        }
        return result;
    }

    @Override
    public Boolean connTest(Map<String, String> dataMap, Integer sourceType, Map<String, Object> expandConfig) {
        Boolean result;
        //针对datasourcex不支持得源做适配
        Boolean aBoolean = exterDataSourceService.connTest(dataMap, sourceType);
        if (null != aBoolean) {
            return aBoolean;
        }
        ISourceDTO sourceDTO = DataSourceEnum.getSourceDTO(dataMap, sourceType, null, null, expandConfig);
        IClient client = ClientCache.getClient(sourceType);
        try {
            result = client.testCon(sourceDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommonException(500, getTestConnExceptionMessage(e));
        }
        return result;
    }

    @Override
    public Boolean connTest(String sourceCode) {
        Boolean result = false;
        //获取当前数据源
        TDataSourceDO dataSourceDO = queryDataSourceBySc(dataSourceMapper, sourceCode);
        //获取该数据源的连接信息
        List<Link> links = dataSourceLinkValueService.queryLinkListBySourceCode(sourceCode);
        //获取当前数据源类型
        TDataSourceTypeDO dataSourceTypeDO = queryDataSourceTypeByTc(dataSourceTypeMapper, dataSourceDO.getSourceTypeCode());

        //link list转map
        Map<String, String> map = linkListCoverToMap(links);
        //针对datasourcex不支持得源做适配
        Boolean aBoolean = exterDataSourceService.connTest(map, Integer.parseInt(dataSourceTypeDO.getSourceType()));
        if (null != aBoolean) {
            return aBoolean;
        }
        //获取schema
        String schema = map.getOrDefault(SCHEMA_NAME, null);
        IClient client = ClientCache.getClient(Integer.parseInt(dataSourceTypeDO.getSourceType()));
        ISourceDTO sourceDTO = DataSourceEnum.getSourceDTO(map, Integer.valueOf(dataSourceTypeDO.getSourceType()), null, schema, null);
        try {
            result = client.testCon(sourceDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommonException(500, getTestConnExceptionMessage(e));
        }
        return result;
    }

    /**
     * 修改数据源
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DataSourceLinkRequest request) {
        //用户数据权限校验
        List<String> dataSourceList = getDataSourceListByUserCode(null);
        Assert.isTrue(null == dataSourceList || dataSourceList.contains(request.getSourceCode()), "无数据权限");

        List<Link> linkList = request.getLinkList();
        if (CollectionUtils.isEmpty(linkList)) {
            return;
        }
        String currentUserCode = getCurrentUserCode();
        updateDataSourceBySc(dataSourceMapper,
                new TDataSourceDO().setSourceName(request.getSourceName()).setUpdator(currentUserCode).setUpdateAt(LocalDateTime.now()),
                request.getSourceCode());
        linkList.forEach(item -> {
            TDataSourceLinkValueDO temp = new TDataSourceLinkValueDO()
                    .setSourceCode(request.getSourceCode())
                    .setSourceTypeKeyCode(item.getSourceTypeKeyCode())
                    .setUpdator(currentUserCode).setUpdateAt(LocalDateTime.now())
                    .setLinkValue(mybatisCryptoProperties.getEnable() ? sm4Encryptor.encrypt(item.getLinkValue()) : item.getLinkValue())
                    .setDeleteFlag(DeleteEnum.N.getKey());
            //修改 link value
            updateDataSourceLinkValueByScKc(dataSourceLinkValueService, temp, request.getSourceCode(), item.getSourceTypeKeyCode());
        });
        //修改额外的操作
        updateExpandProcessor(request);
    }

    /**
     * 修改数据源时的额外操作
     */
    protected void updateExpandProcessor(DataSourceLinkRequest request) {

    }

    /**
     * 删除数据源
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(DataSourceDeleteRequest request) {
        //用户数据权限校验
        List<String> dataSourceList = getDataSourceListByUserCode(null);
        Assert.isTrue(null == dataSourceList || dataSourceList.contains(request.getSourceCode()), "无数据权限");

        //删除允许校验
        if (!deleteBeforeCheck(request)) {
            return;
        }

        String currentUserCode = getCurrentUserCode();

        //数据源删除
        logicDeleteDataSourceBySc(dataSourceMapper, request.getSourceCode(), currentUserCode);
        //数据源连接信息删除
        logicDeleteDataSourceLinkValueBySc(dataSourceLinkValueService, request.getSourceCode(), currentUserCode);

        //额外的删除操作
        deleteExpandProcessor(request);

    }

    /**
     * 删除允许校验
     */
    protected Boolean deleteBeforeCheck(DataSourceDeleteRequest request) {
        return true;
    }

    /**
     * 删除数据源时的额外操作
     */
    protected void deleteExpandProcessor(DataSourceDeleteRequest request) {

    }

    /**
     * 数据源重命名
     */
    @Override
    public void rename(DataSourceRenameRequest request) {
        //用户数据权限校验
        List<String> dataSourceList = getDataSourceListByUserCode(null);
        Assert.isTrue(null == dataSourceList || dataSourceList.contains(request.getSourceCode()), "无数据权限");

        String currentUserCode = getCurrentUserCode();

        updateDataSourceBySc(dataSourceMapper,
                new TDataSourceDO()
                        .setSourceName(request.getSourceName())
                        .setUpdator(currentUserCode).setUpdateAt(LocalDateTime.now()),
                request.getSourceCode());

    }

    /**
     * 数据源下拉列表
     */
    @Override
    public List<DataSourceDropVO> queryDropList(HttpServletRequest request) {
        List<String> dataSourceList = getDataSourceListByUserCode(request);

        if (null != dataSourceList && dataSourceList.size() == 0) {
            //无对应数据权限
            return Lists.newArrayList();
        }

        return queryDataSourceListBySl(dataSourceMapper, dataSourceList)
                .stream()
                .map(this::dataSourceCoverToDropVO)
                .collect(Collectors.toList());
    }

    /**
     * 获取当前数据源的connection
     */
    @Override
    public ConnDTO getConnectionByDataResource(String sourceCode) {
        return getConnectionByDataResource(dataSourceLinkValueService, dataSourceMapper, dataSourceTypeMapper, sourceCode);
    }


    /**
     * 获取数据表名
     */
    @Override
    public List<String> getTables(String sourceCode) {
        return MetaFactory.metaService(getSourceTypeBySourceCode(sourceCode)).getTables(sourceCode);
    }

    @Override
    public Map<String, String> getTablesMap(Map<String, String> dataMap, Integer sourceType) {
        return MetaFactory.metaService(sourceType).getTablesMap(dataMap, sourceType);
    }

    /**
     * 获取表的元数据
     */
    @Override
    public Table getMetaData(String sourceCode, String tableName) {
        return MetaFactory.metaService(getSourceTypeBySourceCode(sourceCode)).getMetaData(sourceCode, tableName);
    }

    @Override
    public Table getMetaData(Map<String, String> dataMap, Integer sourceType, String tableName) {
        return MetaFactory.metaService(sourceType).getMetaData(dataMap, sourceType, tableName);
    }

    private Integer getSourceTypeBySourceCode(String sourceCode) {
        return Integer.parseInt(querySourceTypeBySourceCode(dataSourceMapper, dataSourceTypeMapper, sourceCode));
    }

    @Override
    public String getCreateTableSql(Map<String, String> dataMap, Integer sourceType, SqlQueryDTO sqlQueryDTO) {
        return MetaFactory.metaService(sourceType).getCreateTableSql(dataMap, sourceType, sqlQueryDTO);
    }

    @Override
    public List<List<Object>> getPreview(Map<String, String> dataMap, Integer sourceType, SqlQueryDTO sqlQueryDTO) {
        return MetaFactory.metaService(sourceType).getPreview(dataMap, sourceType, sqlQueryDTO);
    }

    /**
     * 执行sql查询
     */
    @Override
    public List<Map<String, Object>> executeQuery(String sql, String sourceCode) {
        ConnDTO connectionByDataResource = getConnectionByDataResource(sourceCode);
        IClient client = connectionByDataResource.getClient();
        SqlQueryDTO sqlQuery = SqlQueryDTO.builder()
                .sql(sql)
                .build();
        return client.executeQuery(connectionByDataResource.getSourceDTO(), sqlQuery);
    }

    /**
     * 执行sql查询
     */
    @Override
    public List<Map<String, Object>> executeQuery(String sql, String sourceType, List<Link> links) {
        ConnDTO connectionByLink = getConnectionByLink(sourceType, links);
        IClient client = connectionByLink.getClient();
        SqlQueryDTO sqlQuery = SqlQueryDTO.builder()
                .sql(sql)
                .build();
        return client.executeQuery(connectionByLink.getSourceDTO(), sqlQuery);
    }

    @Override
    public List<Map<String, String>> exeQuery(Map<String, String> dataMap, Integer sourceType, String sql) {
        ISourceDTO sourceDTO = DataSourceEnum.getSourceDTO(dataMap, sourceType, null, dataMap.containsKey("schema") ? dataMap.get("schema") : null, null);
        IClient client = ClientCache.getClient(sourceType);
        SqlQueryDTO queryDTO = SqlQueryDTO.builder().sql(sql).build();
        List list = client.executeQuery(sourceDTO, queryDTO);
        return list;
    }

    @Override
    public Map<String, String> executeSql(Map<String, String> dataMap, Integer sourceType, String sql) {
        ISourceDTO sourceDTO = DataSourceEnum.getSourceDTO(dataMap, sourceType, null, dataMap.containsKey("schema") ? dataMap.get("schema") : null, null);
        IClient client = ClientCache.getClient(sourceType);
        SqlQueryDTO queryDTO = SqlQueryDTO.builder().sql(sql).build();
        Map<String, String> map = client.executeSql(sourceDTO, queryDTO);
        return map;
    }

    @Override
    public String assembleLimitPageSqlBySourceCode(String sql, String sourceCode, Object offset, Object pageSize) {
        return assembleLimitPageSqlBYSourceType(sql, DataSourceType.getSourceType(getSourceTypeBySourceCode(sourceCode)), offset, pageSize);
    }

    @Override
    public String assembleLimitPageSqlBYSourceType(String sql, DataSourceType dataSourceType, Object offset, Object pageSize) {
        switch (dataSourceType) {
            case PostgreSQL:
            case Gaussdb:
                return new PostgreSQLDialectGenerator().generatePageSql(sql, offset, pageSize);
            case MySQL:
            case Oracle:
            case DMDB:
            default:
                return new MySQLDialectGenerator().generatePageSql(sql, offset, pageSize);
        }
    }

    @Override
    public Boolean createTopic(Map<String, String> dataMap, Integer sourceType, RecordSchema schema) {
        try {
            ISourceDTO sourceDTO = DataSourceEnum.getSourceDTO(dataMap, sourceType, null, null, null);
            IKafka client = ClientCache.getKafka(sourceType);
            KafkaTopicDTO topicDTO = KafkaTopicDTO.builder()
                    .partitions(Integer.parseInt(dataMap.getOrDefault(DataSourceEnum.KAFKA_PARTITIONS, "1")))
                    .replicationFactor(Short.valueOf(dataMap.getOrDefault(DataSourceEnum.KAFKA_REPLICATIONFACTOR, "1")))
                    .topicName(dataMap.getOrDefault(DataSourceEnum.KAFKA_TOPICNAME, ""))
                    .build();
            client.createTopic(sourceDTO, topicDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommonException(500, getTestConnExceptionMessage(e));
        }
        return true;
    }

    @Override
    public Boolean deleteTopic(Map<String, String> dataMap, Integer sourceType) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader classLoad = ClientFactory.getClassLoader(DataSourceType.getSourceType(sourceType).getPluginName());
            Thread.currentThread().setContextClassLoader(classLoad);
            ISourceDTO sourceDTO = DataSourceEnum.getSourceDTO(dataMap, sourceType, null, null, null);

            Class<?> clazz = classLoad.loadClass(dataMap.getOrDefault(DataSourceEnum.KAFKA_CALSSNAME, ""));
            Object obj = clazz.newInstance();
            Method method = clazz.getDeclaredMethod("deleteTopics", KafkaSourceDTO.class, String.class);
            Object[] paras = {sourceDTO, dataMap.getOrDefault(DataSourceEnum.KAFKA_TOPICNAME, "")};
            method.invoke(obj, paras);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommonException(500, getTestConnExceptionMessage(e));
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
        return Boolean.TRUE;
    }

    @Override
    public Object getClient(Map<String, String> dataMap, Integer sourceType) {
        //针对datasourcex不支持得源做适配
        Object client = exterDataSourceService.getClient(dataMap, sourceType);
        if (null != client) {
            return client;
        }
        return ClientCache.getClient(sourceType);
    }

    @Override
    public Object getClient(String sourceCode) {

        //获取当前数据源
        TDataSourceDO dataSourceDO = queryDataSourceBySc(dataSourceMapper, sourceCode);
        //获取该数据源的连接信息
        List<Link> links = dataSourceLinkValueService.queryLinkListBySourceCode(sourceCode);
        //获取当前数据源类型
        TDataSourceTypeDO dataSourceTypeDO = queryDataSourceTypeByTc(dataSourceTypeMapper, dataSourceDO.getSourceTypeCode());

        //link list转map
        Map<String, String> map = linkListCoverToMap(links);

        return getClient(map, Integer.valueOf(dataSourceTypeDO.getSourceType()));
    }
}
