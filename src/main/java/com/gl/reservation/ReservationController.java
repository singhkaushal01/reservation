package com.gl.reservation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class ReservationController {
    @Autowired
    private ReservationRepository repository;

    @Autowired
    private ReplyingKafkaTemplate<String, Object, Object> template;

    private static final String RESERVATION_TOPIC_NAME = "reservation_topic";
    private static final String REPLY_TOPIC_NAME = "profile_topic";

    @RequestMapping(value = "/reservation/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getReservationDetails(@PathVariable String id) throws Exception {
        Optional<ReservationEntity> entity = repository.findById(id);

        if (entity.isPresent()) {
            String userId = entity.get().getUserId();
            Object reply = kafkaRequestReply(userId);
            log.debug("Reply received: {}", reply.toString());
            return new ResponseEntity<>(formatResponse(reply, entity.get()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Reservation Not Found", HttpStatus.NOT_FOUND);
        }
    }

    private Object kafkaRequestReply(String request) throws Exception {
        ProducerRecord<String, Object> record = new ProducerRecord<>(RESERVATION_TOPIC_NAME, request);
        RequestReplyFuture<String, Object, Object> replyFuture = template.sendAndReceive(record);
        SendResult<String, Object> sendResult = replyFuture.getSendFuture().get(10, TimeUnit.SECONDS);
        ConsumerRecord<String, Object> consumerRecord = replyFuture.get(10, TimeUnit.SECONDS);
        return consumerRecord.value();
    }

    private String formatResponse(Object profileResponse, ReservationEntity reservation){
        JSONObject response = new JSONObject(reservation);
        JSONObject profile = new JSONObject(profileResponse.toString());
        response.put("userProfile", profile);
        return response.toString();
    }

}
