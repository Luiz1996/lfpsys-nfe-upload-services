package com.lfpsys.lfpsys_nfe_upload_services.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.HashMap;
import java.util.Map;

import static com.lfpsys.lfpsys_nfe_upload_services.kafka.KafkaTopicNames.NFE_UPLOAD;
import static com.lfpsys.lfpsys_nfe_upload_services.kafka.KafkaTopicNames.NFE_UPLOAD_DLT;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

@EnableKafka
@Configuration
public class KafkaConfig {

  @Bean
  public KafkaAdmin kafkaAdmin() {
    return new KafkaAdmin(buildMapOfKafkaConfigs());
  }

  @Bean
  public NewTopic topicAddition() {
    return new NewTopic(NFE_UPLOAD, 1, (short) 1);
  }

  @Bean
  public ProducerFactory<String, String> producerFactory() {
    return new DefaultKafkaProducerFactory<>(buildMapOfKafkaConfigs());
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  @Value("${spring.kafka.bootstrap-servers:localhost}")
  private String kafkaBootstrapServers;

  private Map<String, Object> buildMapOfKafkaConfigs() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
    configProps.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    return configProps;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
      final ConsumerFactory<String, String> consumerFactory,
      final KafkaTemplate<String, String> kafkaTemplate) {

    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);

    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
        kafkaTemplate, (consumerRecord, ex) -> new TopicPartition(NFE_UPLOAD_DLT, 0));

    final var backOff = new ExponentialBackOff();
    backOff.setInitialInterval(1000L);
    backOff.setMultiplier(2);
    backOff.setMaxInterval(16000L);
    backOff.setMaxElapsedTime(31000L);

    DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
    factory.setCommonErrorHandler(errorHandler);

    return factory;
  }

  @Bean
  public ConsumerFactory<String, String> consumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id");

    return new DefaultKafkaConsumerFactory<>(props);
  }
}
