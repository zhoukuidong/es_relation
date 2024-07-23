package com.zkd.demo.executor.enums;

/**
 * 枚举
*/
public enum RejectedTypeEnum {
    ABORT_POLICY("AbortPolicy"),
    CALLER_RUNS_POLICY("CallerRunsPolicy"),
    DISCARD_OLDEST_POLICY("DiscardOldestPolicy"),
    DISCARD_POLICY("DiscardPolicy");

    private final String name;

    private RejectedTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
