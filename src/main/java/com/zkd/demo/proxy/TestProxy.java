package com.zkd.demo.proxy;

import cn.hutool.core.collection.CollUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class TestProxy {

    public static void main(String[] args) {
        Fruit o = (Fruit)Proxy.newProxyInstance(Apple.class.getClassLoader(), new Class[]{Fruit.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("====before=====");
                Object result = method.invoke(new Apple(),args);
                System.out.println("====after=====");
                return result;
            }
        });
        System.out.println(o.price());
    }
//    kafka 分页
//    public List<Map<String, String>> pageQueryData(DataQueryCmd dataQueryCmd) {
//        List<Map<String, String>> resultList = CollUtil.newArrayList();
//        try {
//            Properties consumerProps = new Properties();
//            consumerProps.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, this.centralhubProperties.getKafka().getExternalBoostrapServers());
//            consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
//            consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, FIXED_PREVIEW_GROUP);
//            consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//            consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//            consumerProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, dataQueryCmd.getPageSize());
//            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
//            List<TopicPartition> partitions = Optional.ofNullable(consumer.partitionsFor(dataQueryCmd.getSourceTable()))
//                    .orElse(Collections.emptyList())
//                    .stream()
//                    .map(info -> new TopicPartition(info.topic(), info.partition()))
//                    .collect(Collectors.toList());
//            Map<TopicPartition, Long> beginOffsets = consumer.beginningOffsets(partitions);
//            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(partitions);
//            if (CollUtil.isEmpty(partitions)) {
//                return resultList;
//            }
//            List<ConsumerRecord<String, String>> consumerRecordList = CollUtil.newArrayList();
//            Integer pageNum = dataQueryCmd.getPageNum();
//            int targetSize = dataQueryCmd.getPageSize();
//            //计算从第几条开始取数据
//            int pageStart = (pageNum - 1) * targetSize;
//            int partitionFlag = 0;
//            //查询某一页的数据可能跨partition，所以第一次poll的开始位点可能不是partition的startOffset
//            boolean firstFlag = true;
//            partitions = partitions.stream().sorted(Comparator.comparing(TopicPartition::partition)).collect(Collectors.toList());
//            for (TopicPartition partition : partitions) {
//                consumer.assign(Collections.singletonList(partition));
//                Long startOffset = beginOffsets.get(partition);
//                Long endOffset = endOffsets.get(partition);
//                //当前partition消息数量
//                long pmsCount = endOffset - startOffset;
//                int tempFlag = partitionFlag;
//                partitionFlag += pmsCount;
//                //如果partitionFlag小于pageStart 说明还没有到达我们需要的partition
//                if (pageStart <= partitionFlag) {
//                    if (firstFlag) {
//                        //可能有部分数据已经是上一页的数据了
//                        consumer.seek(partition, startOffset + pageStart - tempFlag);
//                        firstFlag = false;
//                    } else {
//                        //非第一次取数据都是从startOffset开始取的
//                        consumer.seek(partition, startOffset);
//                    }
//                    // 拉取消息
//                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
//                    consumer.commitSync();
//                    int count = consumerRecordList.size();
//                    int needCount = targetSize - count;
//                    List<ConsumerRecord<String, String>> tempConsumerRecordList = records.records(partition);
//                    int tempSize = tempConsumerRecordList.size();
//                    if (needCount <= tempSize) {
//                        //数据多了，截取一下
//                        consumerRecordList.addAll(tempConsumerRecordList.subList(0, needCount));
//                    } else {
//                        //数据可能没有拿够 当前数据全都要
//                        consumerRecordList.addAll(tempConsumerRecordList);
//                    }
//                    count = consumerRecordList.size();
//                    if (count == targetSize) {
//                        break;
//                    }
//                }
//            }
//            if (CollUtil.isEmpty(consumerRecordList)) {
//                return resultList;
//            }
//            List<ConsumerRecord<String, String>> recordList = CollUtil.newArrayList();
//            if (consumerRecordList.size() >= targetSize) {
//                recordList = consumerRecordList.subList(0, targetSize);
//            } else {
//                recordList = consumerRecordList;
//            }
//            for (ConsumerRecord<String, String> consumerRecordMap : recordList) {
//                Map<String, String> resultMap = new HashMap<>();
//                resultMap.put(consumerRecordMap.key(), consumerRecordMap.value());
//                long timestamp = consumerRecordMap.timestamp();
//                LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
//                resultMap.put("time", localDateTime.format(CommonConstant.YYYYMMDDHHMMSS));
//                resultList.add(resultMap);
//            }
//            log.info("resultList: {}", resultList);
//        } catch (Exception e) {
//            log.error("page preview Kafka data fail: {}", e);
//        }
//        return resultList;
//    }

}
