package nl.luminis.articles.elasticsearch.integration.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ClusterHealth {

    private String clusterName;
    private String status;
    private int numberOfNodes;
    private Object numberOfDataNodes;
    private Boolean timedOut;
    private int activePrimaryShards;
    private int activeShards;
    private int relocatingShards;
    private int initializingShards;
    private int unassignedShards;
    private int delayedUnassignedShards;
    private int numberOfPendingTasks;
    private int numberOfInFlightFetch;
    private int taskMaxWaitingInQueueMillis;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
