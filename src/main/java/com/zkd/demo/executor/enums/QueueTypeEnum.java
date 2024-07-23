package com.zkd.demo.executor.enums;

import com.zkd.demo.executor.ex.CusDsExecutorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * 枚举
*/
public enum QueueTypeEnum {
    ARRAY_BLOCKING_QUEUE(1, "ArrayBlockingQueue"),
    LINKED_BLOCKING_QUEUE(2, "LinkedBlockingQueue"),
    PRIORITY_BLOCKING_QUEUE(3, "PriorityBlockingQueue"),
    DELAY_QUEUE(4, "DelayQueue"),
    SYNCHRONOUS_QUEUE(5, "SynchronousQueue"),
    LINKED_TRANSFER_QUEUE(6, "LinkedTransferQueue"),
    LINKED_BLOCKING_DEQUE(7, "LinkedBlockingDeque"),
    ;

    private static final Logger log = LoggerFactory.getLogger(QueueTypeEnum.class);
    private final Integer code;
    private final String name;

    private QueueTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static BlockingQueue<Runnable> buildBq(String name, int capacity) {
        return buildBq(name, capacity, false);
    }

    public static BlockingQueue<Runnable> buildBq(String name, int capacity, boolean fair) {
        BlockingQueue<Runnable> blockingQueue = null;
        if (Objects.equals(name, ARRAY_BLOCKING_QUEUE.getName())) {
            blockingQueue = new ArrayBlockingQueue(capacity);
        } else if (Objects.equals(name, LINKED_BLOCKING_QUEUE.getName())) {
            blockingQueue = new LinkedBlockingQueue(capacity);
        } else if (Objects.equals(name, PRIORITY_BLOCKING_QUEUE.getName())) {
            blockingQueue = new PriorityBlockingQueue(capacity);
        } else if (Objects.equals(name, DELAY_QUEUE.getName())) {
            blockingQueue = new DelayQueue();
        } else if (Objects.equals(name, SYNCHRONOUS_QUEUE.getName())) {
            blockingQueue = new SynchronousQueue(fair);
        } else if (Objects.equals(name, LINKED_TRANSFER_QUEUE.getName())) {
            blockingQueue = new LinkedTransferQueue();
        } else if (Objects.equals(name, LINKED_BLOCKING_DEQUE.getName())) {
            blockingQueue = new LinkedBlockingDeque(capacity);
        }
        if (blockingQueue != null) {
            return (BlockingQueue)blockingQueue;
        } else {
            log.error("Cannot find specified BlockingQueue {}", name);
            throw new CusDsExecutorException("Cannot find specified BlockingQueue " + name);
        }
    }

    public Integer getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }
}