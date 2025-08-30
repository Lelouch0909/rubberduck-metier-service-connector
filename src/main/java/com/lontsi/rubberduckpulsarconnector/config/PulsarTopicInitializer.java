package com.lontsi.rubberduckpulsarconnector.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.common.naming.TopicName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class PulsarTopicInitializer {

    private final PulsarAdmin pulsarAdmin;

    @Value("${pulsar.topic-messages}")
    private String topicMessages;

    @Value("${pulsar.topic-responses}")
    private String topicResponses;

    @Value("${pulsar.topic-partitions}")
    private int numPartitions;

    @Value("${pulsar.topic.force-delete-non-partitioned:true}")
    private boolean forceDeleteNonPartitioned;

    @PostConstruct
    public void initializeTopics() {
        try {
            ensurePartitionedTopic(topicMessages);
            ensurePartitionedTopic(topicResponses);
        } catch (Exception e) {
            log.error("❌ Failed to initialize Pulsar topics", e);
            throw new RuntimeException(e);
        }
    }

    private void ensurePartitionedTopic(String rawTopic) throws PulsarAdminException {
        String fullTopicName = normalize(rawTopic); // persistent://tenant/namespace/name
        TopicName tn = TopicName.get(fullTopicName);
        String namespace = tn.getNamespace();

        // 1. Liste des topics partitionnés (vue logique)
        List<String> partitionedTopics = pulsarAdmin.topics().getPartitionedTopicList(namespace);
        boolean isAlreadyPartitioned = partitionedTopics.contains(fullTopicName);

        if (isAlreadyPartitioned) {
            int currentPartitions = pulsarAdmin.topics()
                    .getPartitionedTopicMetadata(fullTopicName).partitions;
            if (currentPartitions < numPartitions) {
                pulsarAdmin.topics().updatePartitionedTopic(fullTopicName, numPartitions);
                log.info("🔁 Updated partition count of [{}] from {} → {}", fullTopicName, currentPartitions, numPartitions);
            } else {
                log.info("ℹ️ Topic [{}] already partitioned with {} (target = {})", fullTopicName, currentPartitions, numPartitions);
            }
            return;
        }

        // 2. Vérifier si un topic NON partitionné existe déjà
        // getList(namespace) retourne les topics physiques (non partitionnés + PARTITIONS individuelles)
        List<String> physicalTopics = pulsarAdmin.topics().getList(namespace);
        boolean nonPartitionedExists = physicalTopics.contains(fullTopicName);

        if (nonPartitionedExists) {
            log.warn("⚠️ Topic [{}] existe comme NON partitionné. Conversion demandée en {} partitions.", fullTopicName, numPartitions);
            if (!forceDeleteNonPartitioned) {
                throw new IllegalStateException("Topic non partitionné présent: " + fullTopicName +
                        " (conversion bloquée car forceDeleteNonPartitioned=false)");
            }
            // Vérifier qu'il n'y a pas de risques (optionnel : on pourrait interroger les stats)
            deleteNonPartitioned(fullTopicName);
            createPartitioned(fullTopicName);
            return;
        }

        // 3. Pas présent du tout → créer directement
        createPartitioned(fullTopicName);
    }

    private void createPartitioned(String fullTopicName) throws PulsarAdminException {
        try {
            pulsarAdmin.topics().createPartitionedTopic(fullTopicName, numPartitions);
            log.info("✅ Created partitioned topic [{}] with {} partitions", fullTopicName, numPartitions);
        } catch (PulsarAdminException e) {
            // Fallback supplémentaire (rare race condition)
            if (isConflict(e)) {
                log.warn("Race condition / conflict lors de la création de [{}]. Retente logique de conversion.", fullTopicName);
                // Re-appel récursif de logique (limité)
                ensurePartitionedTopic(fullTopicName);
            } else {
                throw e;
            }
        }
    }

    private void deleteNonPartitioned(String fullTopicName) throws PulsarAdminException {
        try {
            // delete(topic, force, deleteSchema)
            pulsarAdmin.topics().delete(fullTopicName, true, true);
            log.info("🗑️ Deleted NON-partitioned topic [{}]", fullTopicName);
        } catch (PulsarAdminException e) {
            log.error("❌ Échec suppression du topic non partitionné [{}]", fullTopicName, e);
            throw e;
        }
    }

    private boolean isConflict(PulsarAdminException e) {
        int code;
        try {
            code = e.getStatusCode();
        } catch (Throwable ignored) {
            try {
                code = e.getStatusCode();
            } catch (Throwable ignored2) {
                code = -1;
            }
        }
        return code == 409;
    }

    private String normalize(String topic) {
        if (topic.startsWith("persistent://")) return topic;
        // Valeurs par défaut tenant/namespace selon ton usage
        return "persistent://public/default/" + topic;
    }
}