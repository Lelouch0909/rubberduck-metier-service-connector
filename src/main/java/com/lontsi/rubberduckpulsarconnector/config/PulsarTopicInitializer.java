package com.lontsi.rubberduckpulsarconnector.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class PulsarTopicInitializer {

    private final PulsarAdmin pulsarAdmin;

    @Value("${pulsar.topic-messages}")
    private String TOPIC_MESSAGES;

    @Value("${pulsar.topic-responses}")
    private String TOPIC_RESPONSES;

    @Value("${pulsar.topic-partitions}")
    private int NUM_PARTITIONS;

    @PostConstruct
    public void initializeTopics() {
        try {
            ensurePartitionedTopic(TOPIC_MESSAGES);
            ensurePartitionedTopic(TOPIC_RESPONSES);
        } catch (Exception e) {
            log.error("❌ Failed to initialize Pulsar topics", e);
            throw new RuntimeException(e);
        }
    }

    private void ensurePartitionedTopic(String topic) throws PulsarAdminException {
        String fullTopicName = topic.startsWith("persistent://") ? topic : "persistent://public/default/" + topic;
        List<String> existingTopics = pulsarAdmin.topics().getPartitionedTopicList("public/default");

        log.info("Existing topics: " + existingTopics);
        boolean topicExists = existingTopics.stream().anyMatch(t -> Objects.equals(t, fullTopicName));

        if (!topicExists) {
            pulsarAdmin.topics().createPartitionedTopic(fullTopicName, NUM_PARTITIONS);
            log.info("✅ Created topic [{}] with {} partitions", fullTopicName, NUM_PARTITIONS);
        } else {
            int currentPartitions = pulsarAdmin.topics().getPartitionedTopicMetadata(fullTopicName).partitions;
            if (currentPartitions < NUM_PARTITIONS) {
                pulsarAdmin.topics().updatePartitionedTopic(fullTopicName, NUM_PARTITIONS);
                log.info("🔁 Updated topic [{}] from {} to {} partitions", fullTopicName, currentPartitions, NUM_PARTITIONS);
            } else {
                log.info("ℹ️ Topic [{}] already has {} partitions (≥ {})", fullTopicName, currentPartitions, NUM_PARTITIONS);
            }
        }
    }
}
