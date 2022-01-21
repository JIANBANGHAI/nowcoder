package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTest {

    @Autowired
    private kafkaProducer kafkaProducer;

    @Test
    public void kafkaTest(){
        kafkaProducer.sendMessage("你好","在吗");
        kafkaProducer.sendMessage("我在","来啊了");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

@Component
class kafkaProducer{
    @Autowired
    private KafkaTemplate kafkaTemplate;
    public void sendMessage(String msg,String content){
        kafkaTemplate.send(msg,content);
    }
}

@Component
class kafkaConsumer{
    @KafkaListener(topics = "test")
    public void reverce( ConsumerRecord record){
        System.out.println(record.value());
    }
}
