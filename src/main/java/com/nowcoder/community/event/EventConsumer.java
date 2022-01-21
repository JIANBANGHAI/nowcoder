package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.LoginStatus;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.MessageService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements LoginStatus {
    @Autowired
    private MessageService messageService;
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    @KafkaListener(topics = {TOPIC_TYPE_COMMENT,TOPIC_TYPE_LIKE,TOPIC_TYPE_FOLLOW})
    public void reverce(ConsumerRecord record){
        if (record==null || record.value()==null){
            logger.error("消息顯示爲空");
        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if (event==null){
            logger.error("消息格式異常");
        }
        Message message = new Message();
        message.setFromId(SYSTEM_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String,Object> map = new HashMap<>();
        map.put("entityType",event.getEntityType());
        map.put("entityId",event.getEntityId());
        map.put("userId",event.getUserId());

        if (!event.getData().isEmpty()){
            for(Map.Entry<String,Object> m:event.getData().entrySet()){
                map.put(m.getKey(),m.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(map));
        messageService.insertStatus(message);
    }
}
