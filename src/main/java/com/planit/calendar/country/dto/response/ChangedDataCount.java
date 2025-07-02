package com.planit.calendar.country.dto.response;

import lombok.Getter;

@Getter
public class ChangedDataCount {

    private int totalAddCount;
    private int totalUpdateCount;
    private int totalDeleteCount;

    public ChangedDataCount() {
        this.totalAddCount = 0;
        this.totalUpdateCount = 0;
        this.totalDeleteCount = 0;
    }

    public void incrementTotalCount(ChangeType type) {
        switch (type) {
            case ChangeType.ADD:
                this.totalAddCount++;
                break;
            case ChangeType.UPDATE:
                this.totalUpdateCount++;
                break;
            case ChangeType.DELETE:
                this.totalDeleteCount++;
                break;
        }
    }
}