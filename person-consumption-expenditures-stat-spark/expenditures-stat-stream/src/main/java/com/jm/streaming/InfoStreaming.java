package com.jm.streaming;

import com.jm.common.HBaseClient;
import com.jm.key.PersonConsumptionKey;
import com.jm.model.SimpleConsumptionModel;
import com.jm.request.ExpendituresInfoRequest;
import com.jm.service.StatService;
import com.jm.utils.DateUtils;
import com.jm.utils.JSONUtil;
import com.jm.utils.MyStringUtil;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.*;
import scala.Tuple2;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import com.jm.utils.MyProperties;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.util.*;

/**
 * @Description: $description$
 * @Author: Jomin
 * @Date: $time$ $date$
 */
public class InfoStreaming {
    public static Logger log = Logger.getLogger(InfoStreaming.class);

    public static void main(String[] args) throws Exception {
        String configFile = "test.properties";
        MyProperties myProperties = new MyProperties();
        final Properties serverProps = myProperties.getProperties(configFile);

        JavaStreamingContext javaStreamingContext = createContext(serverProps);

        javaStreamingContext.start();
        try {
            javaStreamingContext.awaitTermination();
        } catch (Exception localException) {
        }
        javaStreamingContext.close();

    }

    static public JavaStreamingContext createContext(final Properties serverProps) {

        System.out.println("=====================createContext=================================");

        final String topic = serverProps.getProperty("kafka.topic");
        Set<String> topicSet = new HashSet();
        topicSet.add(topic);

        final String groupId = serverProps.getProperty("kafka.groupId");
        //获取批次的时间间隔，比如5s
        final Long streamingInterval = Long.parseLong(serverProps.getProperty("streaming.interval"));
        //获取kafka broker列表
        final String brokerList = serverProps.getProperty("bootstrap.servers");

        //从hbase中获取每个分区的消费到的offset位置
        //Map<TopicPartition, Long> consumerOffsetsLong = getConsumerOffsets(serverProps, topic, groupId);
        //printOffset(consumerOffsetsLong);
        //组合kafka参数
        final Map<String, Object> kafkaParams = new HashMap();
        kafkaParams.put("bootstrap.servers", brokerList);
        kafkaParams.put("group.id", groupId);
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("auto.offset.reset", "earliest");
        kafkaParams.put("enable.auto.commit", false);

        //创建sparkconf
        SparkConf sparkConf = new SparkConf().setAppName("InfoStreaming");
        //本地测试
        sparkConf.setMaster("local[2]");
        //sparkConf.set("spark.streaming.stopGracefullyOnShutdown", "true");
        sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        //sparkConf.set("spark.kryo.registrator", "com.djt.stream.registrator.MyKryoRegistrator");
        sparkConf.set("spark.streaming.kafka.maxRatePerPartition", "10000");

        Map<TopicPartition, Long> consumerOffsetsLong = getConsumerOffsets(serverProps, topic, groupId);

        //需要把每个批次的offset保存到此变量
        final AtomicReference<OffsetRange[]> offsetRanges = new AtomicReference();

        //streamingInterval指每隔多长时间执行一个批次
        JavaStreamingContext javaStreamingContext =
                new JavaStreamingContext(sparkConf, Durations.seconds(streamingInterval));

        //注： 这里的 KafkaUtils 的版本是 spark-streaming-kafka-0-10_2.11 的2.3.0 在 POM 的版本里面看
        JavaDStream<ConsumerRecord<String, String>> kafkaMessageDstream = KafkaUtils.createDirectStream(javaStreamingContext,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.<String, String>Subscribe(topicSet, kafkaParams, consumerOffsetsLong)
        );

        JavaDStream<ConsumerRecord<String, String>> kafkaMessageDStreamTransform =
                kafkaMessageDstream.transform(new Function2<JavaRDD<ConsumerRecord<String, String>>, Time, JavaRDD<ConsumerRecord<String, String>>>() {
                    @Override
                    public JavaRDD<ConsumerRecord<String, String>> call(JavaRDD<ConsumerRecord<String, String>> javaRDD, Time time) throws Exception {
                        OffsetRange[] offsets = ((HasOffsetRanges) javaRDD.rdd()).offsetRanges();
                        offsetRanges.set(offsets);
                        System.out.println("=====================kafkaMessageDStreamTransform=================================");
                        System.out.println("javaRDD : " + javaRDD.toString());
                        return javaRDD;
                    }
                });

        //将每条用户行为转换成键值对，键是我们自定义的key,值是使用应用的时长，并统计时长
        JavaPairDStream<PersonConsumptionKey, Long> javaPairDStream =
                kafkaMessageDStreamTransform.flatMapToPair(new PairFlatMapFunction<ConsumerRecord<String, String>, PersonConsumptionKey, Long>() {
                    @Override
                    public Iterator<Tuple2<PersonConsumptionKey, Long>> call(ConsumerRecord<String, String> record) throws Exception {
                        List<Tuple2<PersonConsumptionKey, Long>> list = new ArrayList();
                        System.out.println("=====================kafkaMessageDStreamTransform.flatMapToPair=================================");
                        ExpendituresInfoRequest requestModel;
                        try {
                            requestModel = JSONUtil.json2Object(record.value(), ExpendituresInfoRequest.class);
                        } catch (Exception e) {
                            log.error("event body is Invalid,message=" + record.value(), e);
                            return list.iterator();
                        }

                        if (requestModel == null) {
                            return list.iterator();
                        }
                        System.out.println("=====================requestModel=================================" + requestModel.toString());

                        List<SimpleConsumptionModel> singleList = requestModel.getSimpleConsumptionList();

                        System.out.println("===================== singleList =================================" + singleList.toString());

                        try {
                            for (SimpleConsumptionModel singleModel : singleList) {

                                System.out.println("===================== simpleConsumptionModel =================================" + singleModel.toString());

                                PersonConsumptionKey key = new PersonConsumptionKey();
                                key.setPersonalIdentificationNumber(Long.parseLong(singleModel.getPersonalIdentificationNumber()));
                                //key.setCreatTime(DateUtils.getDateStringByMillisecond(DateUtils.HOUR_FORMAT, Long.parseLong(singleModel.getCreateTime())));
                                key.setCreatTime("201901111012");
                                key.setConsumptionType(singleModel.getConsumptionType());

                                System.out.println("=====================key=================================" + key.toString());

                                Tuple2<PersonConsumptionKey, Long> t = new Tuple2(key, singleModel.getAmount() / 1000);

                                list.add(t);

                            }
                        } catch (Exception e) {
                            log.error("error :", e);
                        }
                        System.out.println("=====================list=================================" + list.toString());
                        return list.iterator();
                    }
                }).reduceByKey(new Function2<Long, Long, Long>() {
                    @Override
                    public Long call(Long long1, Long long2) throws Exception {
                        return long1 + long2;
                    }
                });

        //将每个用户的统计时长写入hbase
        javaPairDStream.foreachRDD(new VoidFunction<JavaPairRDD<PersonConsumptionKey, Long>>() {
            @Override
            public void call(JavaPairRDD<PersonConsumptionKey, Long> rdd) throws Exception {
                System.out.println("=====================javaPairDStream.foreachRDD================================");
                rdd.foreachPartition(new VoidFunction<Iterator<Tuple2<PersonConsumptionKey, Long>>>() {
                    @Override
                    public void call(Iterator<Tuple2<PersonConsumptionKey, Long>> it) throws Exception {
                        System.out.println("===================== rdd.foreachPartition================================");

                        StatService service = StatService.getInstance(serverProps);

                        while (it.hasNext()) {
                            Tuple2<PersonConsumptionKey, Long> t = it.next();
                            PersonConsumptionKey key = t._1();

                            SimpleConsumptionModel model = new SimpleConsumptionModel();
                            model.setPersonalIdentificationNumber(MyStringUtil.getFixedLengthStr(String.valueOf(key.getPersonalIdentificationNumber()), 10));
                            model.setCreateTime(key.getCreateTime());
                            model.setConsumptionType(key.getConsumptionType());
                            model.setAmount(t._2());
                            System.out.println("===========================================================");
                            System.out.println("===================== model================================" + model.toString());
                            System.out.println("===========================================================");
                            service.addPersonConsumptionHistory(model);
                        }
                    }
                });

                //kafka offset写入hbase
                offsetToHbase(serverProps, offsetRanges, topic, groupId);
            }
        });


        return javaStreamingContext;
    }

    /**
     * 将offset写入hbase
     */
    public static void offsetToHbase(Properties props, final AtomicReference<OffsetRange[]> offsetRanges, final String topic, String groupId) {

        System.out.println("=====================offsetToHbase=================================");

        String tableName = "pce_topic_offset";
        Table table = HBaseClient.getInstance(props).getTable(tableName);
        String rowKey = topic + ":" + groupId;

        for (OffsetRange or : offsetRanges.get()) {
            try {
                Put put = new Put(Bytes.toBytes(rowKey));
                put.addColumn(Bytes.toBytes("offset"), Bytes.toBytes(String.valueOf(or.partition())), Bytes.toBytes(String.valueOf(or.untilOffset())));
                table.put(put);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                HBaseClient.closeTable(table);
            }
        }
    }

    /*
     * 从hbase中获取kafka每个分区消费到的offset,以便继续消费
     * */
    public static Map<TopicPartition, Long> getConsumerOffsets(Properties props, String topic, String groupId) {

        System.out.println("=====================begin getConsumerOffsets=================================");

        Set<String> topicSet = new HashSet<String>();
        topicSet.add(topic);
        String tableName = "pce_topic_offset";
        Table table = HBaseClient.getInstance(props).getTable(tableName);
        String rowKey = topic + ":" + groupId;

        Map<TopicPartition, Long> map = new HashMap();

        Get get = new Get(Bytes.toBytes(rowKey));
        try {
            Result result = table.get(get);
            if (result.isEmpty()) {
                return map;
            } else {
                for (Cell cell : result.rawCells()) {
                    String family = Bytes.toString(CellUtil.cloneFamily(cell));

                    if (!"offset".equals(family)) {
                        continue;
                    }

                    int partition = Integer.parseInt(Bytes.toString(CellUtil.cloneQualifier(cell)));
                    long offset = Long.parseLong(Bytes.toString(CellUtil.cloneValue(cell)));
                    TopicPartition topicPartition = new TopicPartition(topic, partition);
                    map.put(topicPartition, offset);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("=====================End getConsumerOffsets=================================");
        return map;
    }


}
