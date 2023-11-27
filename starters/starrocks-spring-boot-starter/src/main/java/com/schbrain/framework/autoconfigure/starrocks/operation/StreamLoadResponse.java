package com.schbrain.framework.autoconfigure.starrocks.operation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author liaozan
 * @since 2023/11/27
 */
@Data
public class StreamLoadResponse {

    private static final String SUCCESS = "Success";

    @JsonProperty("Label")
    public String label;

    @JsonProperty("TxnId")
    private Long txnId;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("NumberTotalRows")
    private Integer numberTotalRows;

    @JsonProperty("NumberLoadedRows")
    private Integer numberLoadedRows;

    @JsonProperty("NumberFilteredRows")
    private Integer numberFilteredRows;

    @JsonProperty("NumberUnselectedRows")
    private Integer numberUnselectedRows;

    @JsonProperty("LoadBytes")
    private Integer loadBytes;

    @JsonProperty("LoadTimeMs")
    private Integer loadTimeMs;

    @JsonProperty("BeginTxnTimeMs")
    private Integer beginTxnTimeMs;

    @JsonProperty("StreamLoadPlanTimeMs")
    private Integer streamLoadPlanTimeMs;

    @JsonProperty("ReadDataTimeMs")
    private Integer readDataTimeMs;

    @JsonProperty("WriteDataTimeMs")
    private Integer writeDataTimeMs;

    @JsonProperty("CommitAndPublishTimeMs")
    private Integer commitAndPublishTimeMs;

    public boolean isSuccess() {
        return SUCCESS.equals(status);
    }

    public boolean isFailed() {
        return !isSuccess();
    }

}
