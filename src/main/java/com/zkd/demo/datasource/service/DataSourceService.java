package com.zkd.demo.datasource.service;

import com.aliyun.datahub.client.model.*;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.zkd.demo.datasource.entity.DTO.ConnDTO;
import com.zkd.demo.datasource.entity.DTO.Link;
import com.zkd.demo.datasource.entity.VO.DataSourceDropVO;
import com.zkd.demo.datasource.entity.VO.DataSourceLinkVO;
import com.zkd.demo.datasource.entity.VO.DataSourceListVO;
import com.zkd.demo.datasource.entity.VO.DataSourceTypeVO;
import com.zkd.demo.datasource.entity.meta.Table;
import com.zkd.demo.datasource.entity.page.PageResult;
import com.zkd.demo.datasource.entity.request.DataSourceDeleteRequest;
import com.zkd.demo.datasource.entity.request.DataSourceLinkRequest;
import com.zkd.demo.datasource.entity.request.DataSourceRenameRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface DataSourceService {

    String SOURCE_NAME = "sourceName";
    String PAGE_NUM = "pageNum";
    String PAGE_SIZE = "pageSize";
    Integer DEFAULT_PAGE_NUM = 1;
    Integer DEFAULT_PAGE_SIZE = 10;

    /**
     * 条件分页查询数据源列表
     */
    PageResult<? extends DataSourceListVO> queryList(HttpServletRequest request);

    /**
     * 获取所有数据源类型
     */
    List<DataSourceTypeVO> queryTypeList();

    /**
     * 根据数据源类型获取数据源连接模板
     */
    List<Link> queryTemplateByType(String sourceTypeCode);

    /**
     * 获取数据源连接信息
     */
    DataSourceLinkVO queryLinkDetail(String sourceCode);

    /**
     * 新增数据源
     */
    String insert(DataSourceLinkRequest request);

    /**
     * 测试连接
     */
    Boolean connTest(DataSourceLinkRequest request);

    Boolean connTest(Map<String, String> dataMap, Integer sourceType, Map<String, Object> expandConfig);

    /**
     * 测试连接
     */
    Boolean connTest(String sourceCode);

    /**
     * 修改数据源
     */
    void update(DataSourceLinkRequest request);

    /**
     * 删除数据源
     */
    void delete(DataSourceDeleteRequest request);

    /**
     * 数据源重命名
     */
    void rename(DataSourceRenameRequest request);

    /**
     * 数据源下拉列表
     */
    List<DataSourceDropVO> queryDropList(HttpServletRequest request);

    /**
     * 获取当前数据源的connection
     */
    ConnDTO getConnectionByDataResource(String sourceCode);

    /**
     * 获取数据表名
     */
    List<String> getTables(String sourceCode);

    /**
     * 获取数据表-注释
     */
    Map<String, String> getTablesMap(Map<String, String> dataMap, Integer sourceType);

    /**
     * 获取表的元数据
     */
    Table getMetaData(String sourceCode, String tableName);

    /**
     * 获取表的元数据
     */
    Table getMetaData(Map<String, String> dataMap, Integer sourceType, String tableName);

    /**
     * 执行sql查询
     */
    List<Map<String, Object>> executeQuery(String sql, String sourceCode);

    /**
     * 执行sql查询
     */
    List<Map<String, Object>> executeQuery(String sql, String sourceType, List<Link> links);

    /**
     * 执行sql查询
     *
     * @param dataMap
     * @param sourceType
     * @param sql
     * @return
     */
    List<Map<String, String>> exeQuery(Map<String, String> dataMap, Integer sourceType, String sql);

    Map<String, String> executeSql(Map<String, String> dataMap, Integer sourceType, String sql);

    /**
     * 基于数据源类型生成分页sql
     *
     * @param sql        原始SQL
     * @param sourceCode 数据源编码
     * @param offset     偏移量
     * @param pageSize   分页大小
     * @return 包含分页limit
     */
    String assembleLimitPageSqlBySourceCode(String sql, String sourceCode, Object offset, Object pageSize);

    /**
     * 基于数据源类型生成分页sql
     *
     * @param sql            原始SQL
     * @param dataSourceType 数据源类型
     * @param offset         偏移量
     * @param pageSize       分页大小
     * @return 包含分页limit
     */
    String assembleLimitPageSqlBYSourceType(String sql, DataSourceType dataSourceType, Object offset, Object pageSize);


    /**
     * 创建topic
     *
     * @param dataMap
     * @param sourceType
     * @param schema
     * @return
     */
    Boolean createTopic(Map<String, String> dataMap, Integer sourceType, RecordSchema schema);

    /**
     * 删除topic
     *
     * @param dataMap
     * @param sourceType
     * @return
     */
    Boolean deleteTopic(Map<String, String> dataMap, Integer sourceType);

    /**
     * datahub-查询所有topic
     *
     * @return
     */
    default Map<String, String> listTopic(Map<String, String> dataMap, Integer sourceType) {
        return null;
    }

    /**
     * datahub-查询topic详情
     *
     * @return
     */
    default GetTopicResult getTopic(Map<String, String> dataMap, Integer sourceType) {
        return null;
    }

    /**
     * datahub-查询所有Project
     *
     * @return
     */
    default ListProjectResult listProject(Map<String, String> dataMap, Integer sourceType) {
        return null;
    }

    default AppendFieldResult appendField(Map<String, String> dataMap, Integer sourceType, String projectName, String topicName, Field field) {
        return null;
    }

    default AppendFieldResult appendField(Map<String, String> dataMap, Integer sourceType, String projectName, String topicName, List<Field> fields) {
        return null;
    }

    default GetRecordsResult getRecords(Map<String, String> dataMap, Integer sourceType, String projectName, String topicName, int limit) {
        return null;
    }

    default GetRecordsResult getRecords(Map<String, String> dataMap, Integer sourceType, String projectName, String topicName, Long timestamp, int limit) {
        return null;
    }

    /**
     * 获取DDL语句
     *
     * @return
     */
    default String getCreateTableSql(Map<String, String> dataMap, Integer sourceType, SqlQueryDTO sqlQueryDTO) {
        return null;
    }

    /**
     * 获取预览数据
     *
     * @return
     */

    default List<List<Object>> getPreview(Map<String, String> dataMap, Integer sourceType, SqlQueryDTO sqlQueryDTO) {
        return Collections.emptyList();
    }

    default Object getClient(Map<String, String> dataMap, Integer sourceType) {
        return null;
    }

    default Object getClient(String sourceCode) {
        return null;
    }

}
