package com.zkd.demo.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zkd.demo.entity.HouseResidentRelationEntity;
import com.zkd.demo.repository.HouseResidentRelationRepository;
import com.zkd.demo.vo.PageVo;
import com.zkd.demo.vo.ScorllAfterVo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class HouseResidentRelationService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private HouseResidentRelationRepository houseResidentRelationRepository;

    public void matchAllQuery() throws Exception{
        SearchRequest searchRequest = new SearchRequest("house_resident_relation");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        List<HouseResidentRelationEntity> resultList = parseSearchResponse(searchResponse,HouseResidentRelationEntity.class);
        System.out.println(resultList);
        System.out.println(resultList.size());
        System.out.println("=================>"+ JSONObject.toJSONString(searchResponse));
    }

    /**
     * 通用查询
     * @param indexName
     * @param queryBuilder
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> commonQuery(String indexName, QueryBuilder queryBuilder,String routing,Class<T> clazz) throws Exception {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);
        if(StrUtil.isNotBlank(routing)) {
            searchRequest.routing(routing);
        }
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        return parseSearchResponse(searchResponse,clazz);
    }

    public <T> List<T> pageQuery(int from,int size,String indexName, QueryBuilder queryBuilder,Class<T> clazz) throws Exception {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder).from(from).size(size);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        return parseSearchResponse(searchResponse,clazz);
    }

    public <T> List<T> pageQueryScroll(int size,String indexName, QueryBuilder queryBuilder,Class<T> clazz) throws Exception {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder).sort("id", SortOrder.ASC);
        searchSourceBuilder.size(size);  // 设置每次滚动获取的文档数量
        searchRequest.scroll(TimeValue.timeValueMinutes(3));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        String scrollId = searchResponse.getScrollId();
        List resultList = CollUtil.newArrayList();
        List<T> l = parseSearchResponse(searchResponse,clazz);
        resultList.addAll(l);
        while (true) {
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scrollId(scrollId);
            scrollRequest.scroll(TimeValue.timeValueMinutes(3));  // 设置滚动时间窗口
            searchResponse = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
            scrollId = searchResponse.getScrollId();
            SearchHits hits = searchResponse.getHits();
            if (hits.getHits().length == 0) {
                break;  // 当滚动窗口中无更多文档时退出循环
            }
            List<T> list = parseSearchResponse(searchResponse,clazz);
            resultList.addAll(list);
        }
        return resultList;
    }

    public <T> PageVo<T> scrollPage(int pageNum , int size, String scrollId, String indexName, int minutes, QueryBuilder queryBuilder,
                                    Class<T> clazz) throws IOException {
        SearchResponse searchResponse = null;

        /**
         *  游标查询的过期时间
         *  第一次查询，不带scroll_id，所以要设置scroll超时时间
         * 超时时间不要设置太短，否则会出现异常
         * 第二次查询，SearchSrollRequest
         */
        if (minutes == 0) {
            minutes = 3;
        }
        if (scrollId == null) {
            SearchRequest searchRequest = new SearchRequest(indexName);
            // 调用SearchRequest.source将查询条件设置到检索请求
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(queryBuilder);
            searchSourceBuilder.size(size).sort("id",SortOrder.ASC);
            searchRequest.source(searchSourceBuilder);
            // 设置scroll查询
            searchRequest.scroll(TimeValue.timeValueMinutes(minutes));
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            scrollId = searchResponse.getScrollId();
        } else {
            // 第二次查询的时候，直接通过scroll id查询数据
            SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
            searchScrollRequest.scroll(TimeValue.timeValueMinutes(minutes));
            // 使用RestHighLevelClient发送scroll请求
            searchResponse = restHighLevelClient.scroll(searchScrollRequest, RequestOptions.DEFAULT);
            scrollId = searchResponse.getScrollId();
        }
        return new PageVo(parseSearchResponse(searchResponse,clazz),scrollId,pageNum,size);

    }



    /**
     * 解析返回结果
     * @param searchResponse
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> parseSearchResponse(SearchResponse searchResponse,Class<T> clazz){
        List<T> resultList = CollUtil.newArrayList();
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hitList = hits.getHits();
        for (SearchHit documentFields : hitList) {
            resultList.add(JSONObject.parseObject(documentFields.getSourceAsString(),clazz));
        }
        return resultList;
    }

    public <T> ScorllAfterVo<T> scrollAfter(int size, String indexName, String sortField, Object[] sortValues,Class<T> clazz) throws Exception {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.size(size);  // 设置每次获取的文档数量
        searchSourceBuilder.sort(SortBuilders.fieldSort(sortField));  // 设置排序字段
        searchRequest.source(searchSourceBuilder);
        if(sortValues == null || sortValues.length == 0){
            return gettScorllAfterVo(clazz, searchRequest);
        }else{
            searchSourceBuilder.searchAfter(sortValues);
            return gettScorllAfterVo(clazz, searchRequest);
        }
    }

    private <T> ScorllAfterVo<T> gettScorllAfterVo(Class<T> clazz, SearchRequest searchRequest) throws IOException {
        Object[] sortValues;
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        if (!(searchHits != null && searchHits.length > 0)) {
            return new ScorllAfterVo<>();
        }
        SearchHit lastHit = searchHits[searchHits.length - 1];
        sortValues = lastHit.getSortValues();
        return new ScorllAfterVo<>(parseSearchResponse(searchResponse, clazz), sortValues);
    }


    public void matchQuery() throws Exception{
        SearchRequest searchRequest = new SearchRequest("house_resident_relation");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("address","880 Holmes Lane"));
        //.zeroTermsQuery(ZeroTermsQueryOption.ALL)
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("=================>"+ JSONObject.toJSONString(searchResponse));
    }

    public void matchQueryKeyword() throws Exception{
        SearchRequest searchRequest = new SearchRequest("house_resident_relation");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //searchSourceBuilder.query(QueryBuilders.matchQuery("address.keyword","重庆市"));
        searchSourceBuilder.query(QueryBuilders.matchQuery("address.keyword","重庆市市辖区万州区高笋塘街道东方广场社区居民委员会龙吟村"));
        //.zeroTermsQuery(ZeroTermsQueryOption.ALL)
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("=================>"+ JSONObject.toJSONString(searchResponse));
    }

    public void matchPhrase()throws Exception{
        SearchRequest searchRequest = new SearchRequest("house_resident_relation");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchPhraseQuery("address","880 Holmes Lane"));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("=================>"+ JSONObject.toJSONString(searchResponse));

    }

    public void matchPhrasePrefixQuery() throws Exception{
        SearchRequest searchRequest = new SearchRequest("house_resident_relation");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MatchPhrasePrefixQueryBuilder matchPhrasePrefixQueryBuilder = QueryBuilders.matchPhrasePrefixQuery("address", "880 Holmes");
        searchSourceBuilder.query(matchPhrasePrefixQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("=================>"+ JSONObject.toJSONString(searchResponse));
    }


    public void boolQuery() throws Exception{
        SearchRequest searchRequest = new SearchRequest("bank");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("=================>"+ JSONObject.toJSONString(searchResponse));
    }

    public void termQuery() throws Exception {
        SearchRequest searchRequest = new SearchRequest("house_resident_relation");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("address","重庆市"));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("=================>"+ JSONObject.toJSONString(searchResponse));
    }



}
