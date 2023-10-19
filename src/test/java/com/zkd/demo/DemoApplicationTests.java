package com.zkd.demo;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.zkd.demo.entity.HouseResidentRelationEntity;
import com.zkd.demo.repository.HouseResidentRelationRepository;
import com.zkd.demo.service.HouseResidentRelationService;
import com.zkd.demo.vo.PageVo;
import com.zkd.demo.vo.ScorllAfterVo;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.LongBounds;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("dev")
class DemoApplicationTests {

    @Autowired
    private HouseResidentRelationRepository houseResidentRelationRepository;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private HouseResidentRelationService houseResidentRelationService;


    @Test
    void contextLoads() {
    }

    /**
     * 索引是否存在
     * @throws Exception
     */
    @Test
    public void testIndices() throws Exception{
        IndicesClient indices = restHighLevelClient.indices();
        GetIndexRequest indexRequest = new GetIndexRequest();
        indexRequest.indices("house_resident_relation");
        boolean exists = indices.exists(indexRequest, RequestOptions.DEFAULT);
        indices.exists(indexRequest, RequestOptions.DEFAULT);
        System.out.println("===========>"+exists);
    }

    /**
     * 索引数据--插入数据
     * @throws Exception
     */
    @Test
    public void indexData() throws Exception{
        IndexRequest indexRequest = new IndexRequest("house_resident_relation");
        HouseResidentRelationEntity entity = new HouseResidentRelationEntity();
        entity.setResidentCode("REC_12345678");
        entity.setAddress("重庆市市辖区万州区高笋塘街道东方广场社区居民委员会栖霞村");
        entity.setAge(27);
        entity.setBirthday(LocalDate.of(1996,5,24));
        entity.setBuilding("珠江大厦");
        entity.setBuildingUnit("503");
        entity.setCityCode("5001002003");
        String jsonData = JSONObject.toJSONString(entity);
        indexRequest.source(jsonData, XContentType.JSON);
        //添加路由键 相同路由键值的文档被路由到同一个分片上
        indexRequest.routing("REC_12345678");
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSONString(indexResponse));
    }

    //刷新策略，用于控制在更新、索引或删除操作后，数据何时对搜索可见
    //NONE ("false")：这是刷新策略的默认值。它表示不执行刷新操作，不等待数据被刷新到索引中就立即返回响应。这种策略适用于高索引或搜索吞吐量的情况，但可能导致在更新操作后的一小段时间内数据对搜索不可见。
    //IMMEDIATE ("true")：这个刷新策略会立即执行刷新操作，确保数据在响应返回之前被刷新到索引中。这样可以保证对搜索的一致性视图，但在高索引或搜索负载下不会有良好的性能表现。
    //WAIT_UNTIL ("wait_for")：这个刷新策略会等待数据被刷新到索引中后再返回响应。它适用于需要立即对刷新后的数据进行搜索的情况。这种策略兼容高索引和搜索吞吐量，但可能会导致请求等待刷新操作完成而导致响应延迟。
    //需要根据具体的应用场景和需求选择适合的刷新策略。如果需要实时性较高的搜索结果，可以考虑使用 IMMEDIATE 或 WAIT_UNTIL 策略。如果对实时性要求不高，但对性能和吞吐量要求较高，可以使用 NONE 策略。
    @Test
    public void updateData() throws Exception{
        UpdateRequest updateRequest = new UpdateRequest("house_resident_relation", "1");
        updateRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
        Map<String, Object> updateFields = new HashMap<>();
        updateFields.put("residentName", "Giffard jack");
        updateRequest.doc(updateFields);
        UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSONString(updateResponse));
    }

    @Test
    public void deleteData() throws Exception{
        DeleteRequest deleteRequest = new DeleteRequest("house_resident_relation", "12000");
        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSONString(deleteResponse));
    }


    /**
     * matchAllQuery
     * @throws Exception
     */
    @Test
    public void matchAllQuery() throws Exception{
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        System.out.println(houseResidentRelationService.commonQuery("house_resident_relation", matchAllQueryBuilder,null, HouseResidentRelationEntity.class));
    }


    /**
     * 单个词匹配
     * @throws Exception
     */
    @Test
    public void matchQueryPer() throws Exception{
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("address","3390 Melvin Lane");
        System.out.println(JSONObject.toJSONString(houseResidentRelationService.commonQuery("house_resident_relation", matchQueryBuilder,null, HouseResidentRelationEntity.class)));
    }

    /**
     * 多词分词匹配
     * @throws Exception
     */
    @Test
    public void matchQueryMulti() throws Exception{
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("address","1 Melvin Lane");
        System.out.println(JSONObject.toJSONString(houseResidentRelationService.commonQuery("house_resident_relation", matchQueryBuilder,null, HouseResidentRelationEntity.class)));
    }

    @Test
    public void matchQueryOpAnd() throws Exception{
        //MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("address","1 Melvin Lana").operator(Operator.AND);
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("address","1 Melvin Lana").operator(Operator.OR).minimumShouldMatch("2");
        System.out.println(JSONObject.toJSONString(houseResidentRelationService.commonQuery("house_resident_relation", matchQueryBuilder, null,HouseResidentRelationEntity.class)));
    }

    @Test
    public void matchQueryKeyword() throws Exception{
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("address.keyword","1 Melvin Lane");
        //区别于下面这个
        //MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("address.keyword","Melvin Lane");
        System.out.println(JSONObject.toJSONString(houseResidentRelationService.commonQuery("house_resident_relation", matchQueryBuilder,null, HouseResidentRelationEntity.class)));
    }

    @Test
    public void queryStringQuery() throws Exception{
        //所有分词or的结果
        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery("*道东方广*");
        //.field("address");
        System.out.println(JSONObject.toJSONString(houseResidentRelationService.commonQuery("house_resident_relation", queryStringQueryBuilder, null,HouseResidentRelationEntity.class)));
    }

    @Test
    public void termExists() throws Exception {
        ExistsQueryBuilder fieldExistQueryBuilder = QueryBuilders.existsQuery("address");
        //ExistsQueryBuilder fieldExistQueryBuilder = QueryBuilders.existsQuery("addressTest");
        System.out.println(JSONObject.toJSONString(houseResidentRelationService.commonQuery("house_resident_relation", fieldExistQueryBuilder, null,HouseResidentRelationEntity.class)));
    }

    @Test
    public void termIds() throws Exception {
        IdsQueryBuilder idsQueryBuilder = QueryBuilders.idsQuery().addIds(CollUtil.newArrayList("1", "2").toArray(new String[0]));
        System.out.println(JSONObject.toJSONString(houseResidentRelationService.commonQuery("house_resident_relation", idsQueryBuilder,null, HouseResidentRelationEntity.class)));
    }

    @Test
    public void termPrefix() throws Exception {
        PrefixQueryBuilder prefixQuery = QueryBuilders.prefixQuery("address", "c");
        System.out.println(JSONObject.toJSONString(houseResidentRelationService.commonQuery("house_resident_relation", prefixQuery, null,HouseResidentRelationEntity.class)));
    }

    @Test
    public void matchPhrasePrefix() throws Exception {
        MatchPhrasePrefixQueryBuilder matchPhrasePrefixQueryBuilder = QueryBuilders.matchPhrasePrefixQuery("address", "3390 Melvin L");
        System.out.println(JSONObject.toJSONString(houseResidentRelationService.commonQuery("house_resident_relation", matchPhrasePrefixQueryBuilder,null, HouseResidentRelationEntity.class)));
    }

    @Test
    public void matchBoolPrefix() throws Exception {
        //MatchBoolPrefixQueryBuilder matchBoolPrefixQueryBuilder = QueryBuilders.matchBoolPrefixQuery("address", "3390 Melvin Lane");
        MatchBoolPrefixQueryBuilder matchBoolPrefixQueryBuilder = QueryBuilders.matchBoolPrefixQuery("address", "3 M L");
        //本质:
        //{
        //  "query": {
        //    "bool" : {
        //      "should": [
        //        { "prefix": { "address": "3" }},
        //        { "prefix": { "address": "M" }},
        //        { "prefix": { "address": "L"}}
        //      ]
        //    }
        //  }
        //}
        System.out.println(JSONObject.toJSONString(houseResidentRelationService.commonQuery("house_resident_relation", matchBoolPrefixQueryBuilder,null, HouseResidentRelationEntity.class)));
    }

    @Test
    public void termPer() throws Exception {
        //3390 Melvin Lane
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("address.keyword", "3390");
        //TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("address.keyword", "3390 Melvin Lane");
        System.out.println(JSONObject.toJSONString(houseResidentRelationService.commonQuery("house_resident_relation", termQueryBuilder,null, HouseResidentRelationEntity.class)));
    }

    @Test
    public void termsQuery() throws Exception {
        //3390 Melvin Lane
        //TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("address", new String[]{"Melvin","Lane"});
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("address.keyword", new String[]{"3390 Melvin Lane","1 Melvin Lane"});
        System.out.println(JSONObject.toJSONString(houseResidentRelationService.commonQuery("house_resident_relation", termsQueryBuilder,null, HouseResidentRelationEntity.class)));
    }

    /**
     * 1、* 代表任意字符序列（包括空字符序列）。
     * 2、? 代表任意单个字符。
     * @throws Exception
     */
    @Test
    public void wildcardQuery() throws Exception {
        //3390 Melvin Lane
        WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("address.keyword", "*lv*");
        System.out.println(JSONObject.toJSONString(houseResidentRelationService.commonQuery("house_resident_relation", wildcardQueryBuilder,null, HouseResidentRelationEntity.class)));
    }


    /**
     * 1. .*：匹配任意字符零次或多次。
     *    - 示例：regexpQuery("field", ".*test.*") 匹配包含 "test" 的字段值，如 "this is a test"、"test123"。
     * 2. ^：匹配字符串的开始位置。
     *    - 示例：regexpQuery("field", "^hello") 匹配以 "hello" 开头的字段值，如 "hello world"、"hello123"。
     * 3. $：匹配字符串的结束位置。
     *    - 示例：regexpQuery("field", "world$") 匹配以 "world" 结尾的字段值，如 "hello world"、"123world"。
     * 4. []：匹配方括号内的任意字符。
     *    - 示例：regexpQuery("field", "[abc]") 匹配包含字母 "a"、"b" 或 "c" 的字段值，如 "apple"、"banana"。
     * 5. [^]：匹配除了方括号内字符之外的任意字符。
     *    - 示例：regexpQuery("field", "[^abc]") 匹配不包含字母 "a"、"b" 或 "c" 的字段值。
     * @throws Exception
     */
    @Test
    public void regexpQuery() throws Exception {
        //1 Melvin Lane
        RegexpQueryBuilder regexpQueryBuilder = QueryBuilders.regexpQuery("address.keyword", "1.*");
        System.out.println(JSONObject.toJSONString(houseResidentRelationService.commonQuery("house_resident_relation", regexpQueryBuilder,null, HouseResidentRelationEntity.class)));
    }

    @Test
    public void fuzzyQuery() throws Exception {
        //1 Melvin Lane
        //FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("address", "Melvin");
        FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("address", "1 Melvin La").fuzziness(Fuzziness.AUTO);
        System.out.println(JSONObject.toJSONString(houseResidentRelationService.commonQuery("house_resident_relation", fuzzyQueryBuilder,null, HouseResidentRelationEntity.class)));
    }

    @Test
    public void boolMust() throws Exception {
        //1 Melvin Lane
        //BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("address", "1 Melvin La"));
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("address.keyword", "1 Melvin La"));
        System.out.println(JSONObject.toJSONString(houseResidentRelationService.commonQuery("house_resident_relation", boolQueryBuilder,null, HouseResidentRelationEntity.class)));
    }

    @Test
    public void boolMustNot() throws Exception {
        //1 Melvin Lane
        //BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("address", "1 Melvin La"));
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().mustNot(QueryBuilders.matchQuery("address.keyword", "1 Melvin La"));
        System.out.println(JSONObject.toJSONString(houseResidentRelationService.commonQuery("house_resident_relation", boolQueryBuilder,null, HouseResidentRelationEntity.class)));
    }

    @Test
    public void boolShould() throws Exception {
        //1 Melvin Lane
        //BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("address.keyword", "1 Melvin La")).filter(QueryBuilders.rangeQuery("age").gte(50).lte(60));
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("address.keyword", "1 Melvin La")).filter(QueryBuilders.rangeQuery("age").gte(50).lte(60));
        System.out.println(JSONObject.toJSONString(houseResidentRelationService.commonQuery("house_resident_relation", boolQueryBuilder,null, HouseResidentRelationEntity.class)));
    }

    @Test
    public void pageQuery() throws Exception {
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("address.keyword", "1 Melvin La");
        System.out.println(JSONObject.toJSONString(houseResidentRelationService.pageQuery(100,10,"house_resident_relation", matchQueryBuilder, HouseResidentRelationEntity.class)));
    }

    @Test
    public void pageQueryScroll() throws Exception {
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        List<HouseResidentRelationEntity> list = houseResidentRelationService.pageQueryScroll(6000, "house_resident_relation", matchAllQueryBuilder, HouseResidentRelationEntity.class);
        System.out.println(list.size());
        //System.out.println(JSONObject.toJSONString(list));
    }

    //ES的搜索是分2个阶段进行的，即Query阶段和Fetch阶段。  Query阶段比较轻量级，通过查询倒排索引，获取满足查询结果的文档ID列表。
    // 而Fetch阶段比较重，需要将每个shard的结果取回，在协调结点进行全局排序。
    // 通过From+size这种方式分批获取数据的时候，随着from加大，需要全局排序并丢弃的结果数量随之上升，性能越来越差。
    //而Scroll查询，先做轻量级的Query阶段以后，免去了繁重的全局排序过程。
    // 它只是将查询结果集，也就是doc id列表保留在一个上下文里， 之后每次分批取回的时候，
    // 只需根据设置的size，在每个shard内部按照一定顺序（默认doc_id续)， 取回这个size数量的文档即可。

    @Test
    public void scrollPage() throws Exception{
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        PageVo<HouseResidentRelationEntity>  pageVo = houseResidentRelationService.scrollPage(1,9999, "FGluY2x1ZGVfY29udGV4dF91dWlkDnF1ZXJ5VGhlbkZldGNoAxY3U1E2VWsxU1FydUwzNDFXbzNaRTJBAAAAAAACF0gWQ2ExLTl5UW9TS2VmY2gtVjVESnZMZxY3U1E2VWsxU1FydUwzNDFXbzNaRTJBAAAAAAACF0cWQ2ExLTl5UW9TS2VmY2gtVjVESnZMZxY3U1E2VWsxU1FydUwzNDFXbzNaRTJBAAAAAAACF0kWQ2ExLTl5UW9TS2VmY2gtVjVESnZMZw==","house_resident_relation",3,matchAllQueryBuilder , HouseResidentRelationEntity.class);
        if(CollUtil.isNotEmpty(pageVo.getDataList())){
            System.out.println(pageVo.getDataList().size());
            System.out.println(pageVo.getDataList().get(0));
        }
        System.out.println(pageVo.getScrollId());
    }

    @Test
    public void scrollAfter() throws Exception {
        ScorllAfterVo<HouseResidentRelationEntity> houseResidentRelationEntityScorllAfterVo = houseResidentRelationService.scrollAfter(2000, "house_resident_relation", "id", new Object[]{2000}, HouseResidentRelationEntity.class);
        List<HouseResidentRelationEntity> dataList = houseResidentRelationEntityScorllAfterVo.getDataList();
        if(CollUtil.isNotEmpty(dataList)){
            System.out.println(dataList.size());
            System.out.println(dataList.get(0));
        }
        Object[] sortValues = houseResidentRelationEntityScorllAfterVo.getSortValues();
        if(sortValues != null && sortValues.length > 0){
            System.out.println(Arrays.asList(sortValues));
        }
    }

    @Test
    public void aggregationQuery() throws Exception{
        SearchRequest searchRequest = new SearchRequest("house_resident_relation");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 构建聚合查询
        searchSourceBuilder.aggregation(AggregationBuilders.terms("age_aggs").field("age").size(100).order(BucketOrder.key(true)));
        searchRequest.source(searchSourceBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 获取聚合结果
        Aggregations aggregations = searchResponse.getAggregations();
        Terms aggregation = aggregations.get("age_aggs");
        List<? extends Terms.Bucket> buckets = aggregation.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            String key = bucket.getKeyAsString();
            long docCount = bucket.getDocCount();
            // 处理聚合结果
            System.out.println("Key: " + key + ", Doc Count: " + docCount);
        }
    }

    @Test
    public void testTimeGroup() throws Exception{
        SearchRequest searchRequest = new SearchRequest("test_time_group");
        // 创建一个日期直方图聚合
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.matchAllQuery())
                .aggregation(
                        AggregationBuilders.dateHistogram("time_histogram")
                                .field("createTime")
                                .calendarInterval(DateHistogramInterval.MINUTE) // 设置时间间隔为1分钟
                                .format("yyyy-MM-dd HH:mm") // 可选，指定返回的时间格式
                                .minDocCount(0) // 可选，设置最小文档计数，默认为0
                                .extendedBounds(new LongBounds("2023-07-27 10:20", "2023-07-27 10:30")) // 可选，设置时间范围
                )
                .size(0); // 设置返回的文档数量为0，只返回聚合结果
        searchRequest.source(searchSourceBuilder);
        // 执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 获取聚合结果
        Histogram histogram = searchResponse.getAggregations().get("time_histogram");
        List<? extends Histogram.Bucket> buckets = histogram.getBuckets();
        // 遍历输出聚合结果
        for (Histogram.Bucket entry : buckets) {
            String keyAsString = entry.getKeyAsString();
            long docCount = entry.getDocCount();
            System.out.println("时间：" + keyAsString + ", 文档数量：" + docCount);
        }
    }

    @Test
    public void testGuavaUtil(){
        System.out.println(houseResidentRelationRepository);
        Joiner joiner = Joiner.on("; ").skipNulls();
        System.out.println(joiner.join("Harry", null, "Ron", "Hermione"));
        System.out.println(Splitter.on("|").trimResults().omitEmptyStrings().splitToList("123 || zkd|789"));
        System.out.println(Splitter.on("#").withKeyValueSeparator("=").split("name=zkd#age=28#weight=59"));
    }


}
