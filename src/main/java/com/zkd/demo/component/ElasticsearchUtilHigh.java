package com.zkd.demo.component;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class ElasticsearchUtilHigh {

    @Autowired
    private RestHighLevelClient transportClient;

//    private static RestHighLevelClient client;

    private static final int MAX_ES_SEARCH_COUNT  = 500000;

    /**
     * @PostContruct是spring框架的注解 spring容器初始化的时候执行该方法
     */
//    @PostConstruct
//    public void init() {
//        this.client = this.transportClient;
//    }

    /**
     * 创建索引
     *
     * @param index
     * @return
     */
    public boolean createIndex(String index) {
        if (isIndexExist(index)) {
            log.info("index:{} is exits!", index);
            System.out.println("Index is exits!");
            return false;
        }
        //index名必须全小写，否则报错
        CreateIndexRequest request = new CreateIndexRequest(index);
//        request.settings(Settings.builder()
//                .put("index.routing.preference", "_primary_first")
//        );
        try {
            CreateIndexResponse indexResponse = transportClient.indices().create(request, RequestOptions.DEFAULT);
            if (indexResponse.isAcknowledged()) {
                log.info("创建索引成功");
            } else {
                log.info("创建索引失败");
            }
            return indexResponse.isAcknowledged();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 删除索引库
     *
     * @param index
     * @return
     */
    public boolean deleteIndex(String index) {
        try {
            if (!isIndexExist(index)) {
                System.out.println("Index is not exits!");
                log.info("index:{} not exits!", index);
                return true;
            }
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);

            AcknowledgedResponse deleteIndexResponse = transportClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);

            return deleteIndexResponse.isAcknowledged();
        } catch (IOException e) {
            log.error("删除索引数据失败，index{}", index);
        }
        return false;
    }

    /**
     * 自定义mapping
     *
     * {
     *   "mappings": {
     *     "doc":{
     *       "dynamic":true,
     *       "properties": {
     *         "name": {
     *           "type": "text",
     *           "analyzer": "ik_max_word",
     *                "search_analyzer": "ik_smart"
     *         },
     *         "age": {
     *           "type": "long"
     *         }
     *       }
     *     }
     *   }
     * }
     *
     * @param index
     * @return
     */
    public boolean setMapping(String index, String type, XContentBuilder mappingBuilder) {
        boolean result = false;

        try {
            PutMappingRequest putMappingRequest = new PutMappingRequest(index).type(type).source(mappingBuilder);
            transportClient.indices().putMapping(putMappingRequest, RequestOptions.DEFAULT);
            result = true;

        } catch (IOException ex) {
            log.error("自定义mapping失败：index:{}", index, ex);
        }
        return result;
    }

    public boolean setSetting(String index, Settings settings) {
        boolean result = false;

        try {
            UpdateSettingsRequest updateSettingsRequest = new UpdateSettingsRequest(index).settings(settings);
            transportClient.indices().putSettings(updateSettingsRequest, RequestOptions.DEFAULT);
            result = true;
        } catch (IOException ex) {
            log.error("自定义setting失败：index:{}", index, ex);
        }
        return result;
    }

    //判断索引是否存在
    public boolean isIndexExist(String index){
        GetIndexRequest request = new GetIndexRequest();
        request.indices(index);
        request.local(false);
        request.humanReadable(true);
        boolean exists  = false;
        try {
            exists = transportClient.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException ex) {
            log.error("查询失败：index:{}", index, ex);
        }
        return exists;
    }


    /**
     * 数据添加，正定ID
     *
     * @param jsonObject 要增加的数据
     * @param index      索引，类似数据库
     * @param type       类型，类似表
     * @param id         数据ID
     * @return
     */
    public String addData(JSONObject jsonObject, String index, String type, String id) {
//       log.info("es添加数据方法:type:"+type+"id:"+id);
        IndexRequest indexRequest = new IndexRequest(index, type, id);
        try {
            indexRequest.source(JSONObject.toJSONString(jsonObject), XContentType.JSON);
            IndexResponse indexResponse = transportClient.index(indexRequest, RequestOptions.DEFAULT);
            return indexResponse.getId();
        } catch (Exception ex) {
            log.error("插入失败：{}", jsonObject.toJSONString(), ex);
        }
        return null;
    }



    /**
     * 数据添加
     *
     * @param jsonObject 要增加的数据
     * @param index      索引，类似数据库
     * @param type       类型，类似表
     * @return
     */
    public String addData(JSONObject jsonObject, String index, String type) {
        return addData(jsonObject, index, type, UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
    }

    /**
     * 通过ID删除数据
     *
     * @param index 索引，类似数据库
     * @param type  类型，类似表
     * @param id    数据ID
     */
    public void deleteDataById(String index, String type, String id) {
        DeleteRequest request = new DeleteRequest(index, type, id);
        try {
            DeleteResponse deleteResponse = transportClient.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("删除索引数据失败，index{},id is {}", index, id);
        }

    }



//    /**
//     * 使用分词查询,并分页
//     *
//     * @param index          索引名称
//     * @param type           类型名称,可传入多个type逗号分隔
//     * @param startPage    当前页
//     * @param pageSize       每页显示条数
//     * @param query          查询条件
//     * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
//     * @param sortField      排序字段
//     * @param highlightField 高亮字段以逗号隔开
//     * @return
//     */
//    public static EsPage searchDataPage(String index, String type, int startPage, int pageSize, QueryBuilder query, String fields, String sortField, String highlightField) {
//        SearchRequestBuilder searchRequestBuilder = transportClient.prepareSearch(index);
//        if (StringUtils.isNotEmpty(type)) {
//            searchRequestBuilder.setTypes(type.split(","));
//        }
//        searchRequestBuilder.setSearchType(SearchType.QUERY_THEN_FETCH);
//
//        // 需要显示的字段，逗号分隔（缺省为全部字段）
//        if (StringUtils.isNotEmpty(fields)) {
//            searchRequestBuilder.setFetchSource(fields.split(","), null);
//        }
//        searchRequestBuilder.setFetchSource(true);
//        //分页不一致问题
//        searchRequestBuilder.setTrackScores(true);
//        //排序字段
//        if (StringUtils.isNotEmpty(sortField)) {
//            searchRequestBuilder.addSort(sortField, SortOrder.ASC);
//        }
//
//        // 高亮（xxx=111,aaa=222）
//        if (StringUtils.isNotEmpty(highlightField)) {
//            String[] lights = highlightField.split(",");
//            HighlightBuilder highlightBuilder = new HighlightBuilder();
//           // 设置高亮字段
//            for (String s : Arrays.asList(lights)) {
//                highlightBuilder.field(s).preTags("<span style='color:#FCA502' >").postTags("</span>");
//            }
//            //对于单个高亮的字段使用以下方法
////           highlightBuilder.field(highlightField);
//            searchRequestBuilder.highlighter(highlightBuilder);
//        }
//
//        //searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
//        searchRequestBuilder.setQuery(query);
//
//        // 分页应用
//        searchRequestBuilder.setFrom((startPage-1)*pageSize).setSize(pageSize);
//
//        // 设置是否按查询匹配度排序
////        searchRequestBuilder.setExplain(true);
//
//        //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询
//        log.info("查询条件{}", searchRequestBuilder);
//
//        // 执行搜索,返回搜索响应信息
//        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
//
//        long totalHits = searchResponse.getHits().totalHits;
//        long length = searchResponse.getHits().getHits().length;
//
//        System.out.println("共查询到["+totalHits+"]条数据,处理数据条数["+length+"]");
//        log.info("共查询到{}条数据，处理数据条数{}", totalHits,length);
//        if (searchResponse.status().getStatus() == 200) {
//            // 解析对象
//            List<Map<String, Object>> sourceList = setSearchResponse(searchResponse, highlightField);
//
//            return new EsPage(startPage, pageSize, (int) totalHits, sourceList);
//        }
//
//        return null;
//
//    }

    /**
     * @param indices
     * @param types
     * @param searchSourceBuilder
     * @param highlightField
     * @return
     */
    public List<Map<String, Object>> searchListData(String[] indices, String[] types, SearchSourceBuilder searchSourceBuilder, String highlightField) {
        // 组装查询条件
        SearchRequest searchRequest = new SearchRequest().indices(indices);
        // 类型名称不为空时，才组装type
        if (types != null && types.length > 0) {
            searchRequest.types(types);
        }
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        if (StrUtil.isNotEmpty(highlightField)) {
            String[] lights = highlightField.split(",");
            // 设置高亮字段
            for (String s : Arrays.asList(lights)) {
                highlightBuilder.field(s).preTags("<span style='color:#FCA502' >").postTags("</span>");
            }
            //对于单个高亮的字段使用以下方法
            highlightBuilder.fragmentSize(800000); //最大高亮分片数
            highlightBuilder.numOfFragments(0); //从第一个分片获取高亮片段
        }
        highlightBuilder.requireFieldMatch(true);
        searchRequest.source(searchSourceBuilder.highlighter(highlightBuilder));
        SearchResponse searchResponse = null;
        try {
            searchResponse = transportClient.search(searchRequest, RequestOptions.DEFAULT);
            List<Map<String, Object>> sourceList = setSearchResponse(searchResponse, highlightField);
            return sourceList;
        } catch (IOException ex) {
            log.error("查询接口结果集失败 入参:{}", JSONObject.toJSONString(searchRequest), ex);
        }
        return new ArrayList<>();
    }


    /**
     * 高亮结果集 特殊处理
     *
     * @param searchResponse
     * @param highlightField
     */
    private static List<Map<String, Object>> setSearchResponse(SearchResponse searchResponse, String highlightField) {
        List<Map<String, Object>> sourceList = new ArrayList<Map<String, Object>>();
        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            searchHit.getSourceAsMap().put("id", searchHit.getId());
            searchHit.getSourceAsMap().put("score", searchHit.getScore());

            if (StrUtil.isNotEmpty(highlightField)) {

                for (String s : Arrays.asList(highlightField.split(","))) {
                    StringBuffer stringBuffer = new StringBuffer();
                    //如果有查询的高亮字段的值不为空展示高亮
                    HighlightField field = searchHit.getHighlightFields().get(s);
                    if (field != null) {
                        Text[] text = field.getFragments();
                        if (text != null) {
                            for (Text str : text) {
                                stringBuffer.append(str.string());
                            }
                            //遍历 高亮结果集，覆盖 正常结果集
                            searchHit.getSourceAsMap().put(s, stringBuffer.toString());
                        }
                    }
                }
            }
            sourceList.add(searchHit.getSourceAsMap());
        }
        return sourceList;
    }


}

