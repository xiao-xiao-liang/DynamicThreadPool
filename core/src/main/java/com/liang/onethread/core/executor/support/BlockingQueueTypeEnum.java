package com.liang.onethread.core.executor.support;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * 阻塞队列类型枚举
 * <p>
 * 定义动态线程池支持的阻塞队列类型，每种队列类型都实现了不同的任务存储策略。
 * 通过此枚举可以灵活选择适合业务场景的队列实现。
 * <p>
 * 支持的队列类型：
 * <ul>
 *     <li><b>ArrayBlockingQueue</b> - 基于数组的有界队列，FIFO 顺序，创建时必须指定容量</li>
 *     <li><b>LinkedBlockingQueue</b> - 基于链表的可选有界队列，FIFO 顺序，吞吐量通常较高</li>
 *     <li><b>LinkedBlockingDeque</b> - 基于链表的双端阻塞队列，支持在首尾进行操作</li>
 *     <li><b>SynchronousQueue</b> - 不存储元素的同步队列，每个插入操作必须等待对应的移除操作</li>
 *     <li><b>LinkedTransferQueue</b> - 基于链表的无界传输队列，支持生产者直接传递给消费者</li>
 *     <li><b>PriorityBlockingQueue</b> - 支持优先级排序的无界队列，元素按优先级出队</li>
 * </ul>
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 创建指定容量的 ArrayBlockingQueue
 * BlockingQueue<Runnable> queue = BlockingQueueTypeEnum.createBlockingQueue("ArrayBlockingQueue", 1024);
 *
 * // 在构建器中使用
 * ThreadPoolExecutorBuilder.builder()
 *     .workQueueType(BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE)
 *     .workQueueCapacity(2048)
 *     .build();
 * }</pre>
 *
 * @see java.util.concurrent.BlockingQueue
 */
public enum BlockingQueueTypeEnum {

    /**
     * {@link ArrayBlockingQueue}
     */
    ARRAY_BLOCKING_QUEUE("ArrayBlockingQueue") {
        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new ArrayBlockingQueue<>(capacity);
        }

        @Override
        <T> BlockingQueue<T> of() {
            return new ArrayBlockingQueue<>(DEFAULT_CAPACITY);
        }
    },

    /**
     * {@link LinkedBlockingQueue}
     */
    LINKED_BLOCKING_QUEUE("LinkedBlockingQueue") {
        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new LinkedBlockingQueue<>(capacity);
        }

        @Override
        <T> BlockingQueue<T> of() {
            return new LinkedBlockingQueue<>();
        }
    },

    /**
     * {@link LinkedBlockingDeque}
     */
    LINKED_BLOCKING_DEQUE("LinkedBlockingDeque") {
        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new LinkedBlockingDeque<>(capacity);
        }

        @Override
        <T> BlockingQueue<T> of() {
            return new LinkedBlockingDeque<>();
        }
    },

    /**
     * {@link SynchronousQueue}
     */
    SYNCHRONOUS_QUEUE("SynchronousQueue") {
        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new SynchronousQueue<>();
        }

        @Override
        <T> BlockingQueue<T> of() {
            return new SynchronousQueue<>();
        }
    },

    /**
     * {@link LinkedTransferQueue}
     */
    LINKED_TRANSFER_QUEUE("LinkedTransferQueue") {
        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new LinkedTransferQueue<>();
        }

        @Override
        <T> BlockingQueue<T> of() {
            return new LinkedTransferQueue<>();
        }
    },

    /**
     * {@link PriorityBlockingQueue}
     */
    PRIORITY_BLOCKING_QUEUE("PriorityBlockingQueue") {
        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new PriorityBlockingQueue<>(capacity);
        }

        @Override
        <T> BlockingQueue<T> of() {
            return new PriorityBlockingQueue<>();
        }
    };

    BlockingQueueTypeEnum(String name) {
        this.name = name;
    }

    @Getter
    private final String name;

    private static final int DEFAULT_CAPACITY = 4096;

    /**
     * 创建指定实现类型的 {@code BlockingQueue}，并指定其初始容量。
     * 该方法为抽象方法，由子类提供具体实现。
     *
     * @param capacity 队列的容量
     * @param <T>      {@code BlockingQueue} 中元素的类型
     * @return 指定类型 {@code T} 的 {@code BlockingQueue} 视图
     */
    abstract <T> BlockingQueue<T> of(Integer capacity);

    /**
     * 创建指定实现类型的 {@code BlockingQueue}，不限制容量。
     * 该方法为抽象方法，由子类提供具体实现。
     *
     * @param <T> {@code BlockingQueue} 中元素的类型
     * @return 指定类型 {@code T} 的 {@code BlockingQueue} 视图
     */
    abstract <T> BlockingQueue<T> of();

    private static final Map<String, BlockingQueueTypeEnum> NAME_TO_ENUM_MAP;

    static {
        final BlockingQueueTypeEnum[] values = BlockingQueueTypeEnum.values();
        NAME_TO_ENUM_MAP = new HashMap<>(values.length);
        for (BlockingQueueTypeEnum v : values)
            NAME_TO_ENUM_MAP.put(v.name, v);
    }

    /**
     * 根据给定的 {@link BlockingQueueTypeEnum#name BlockingQueueTypeEnum.name}
     * 和容量创建一个 {@code BlockingQueue}。
     *
     * @param blockingQueueName {@link BlockingQueueTypeEnum#name BlockingQueueTypeEnum.name}
     * @param capacity          {@code BlockingQueue} 的容量
     * @param <T>               {@code BlockingQueue} 中元素的类型
     * @return 指定类型 {@code T} 的 {@code BlockingQueue} 视图
     * @throws IllegalArgumentException 如果未找到匹配的队列类型
     */
    public static <T> BlockingQueue<T> createBlockingQueue(String blockingQueueName, Integer capacity) {
        final BlockingQueue<T> of = of(blockingQueueName, capacity);
        if (of != null)
            return of;

        throw new IllegalArgumentException("No matching type of blocking queue was found: " + blockingQueueName);
    }

    /**
     * 根据给定的 {@link BlockingQueueTypeEnum#name BlockingQueueTypeEnum.name}
     * 和容量创建一个 {@code BlockingQueue}。
     *
     * @param blockingQueueName {@link BlockingQueueTypeEnum#name BlockingQueueTypeEnum.name}
     * @param capacity          {@code BlockingQueue} 的容量
     * @param <T>               {@code BlockingQueue} 中元素的类型
     * @return 指定类型 {@code T} 的 {@code BlockingQueue} 视图
     */
    private static <T> BlockingQueue<T> of(String blockingQueueName, Integer capacity) {
        final BlockingQueueTypeEnum typeEnum = NAME_TO_ENUM_MAP.get(blockingQueueName);
        if (typeEnum == null)
            return null;

        return Objects.isNull(capacity) ? typeEnum.of() : typeEnum.of(capacity);
    }
}
