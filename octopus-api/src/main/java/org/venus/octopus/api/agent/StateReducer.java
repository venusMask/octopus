package org.venus.octopus.api.agent;

/**
 * 状态合并函数式接口
 * <p>
 * 定义同一键在多次状态更新时的合并方式。例如消息列表应追加而非覆盖：
 * </p>
 *
 * <pre>{@code
 * // 消息列表追加策略
 * StateReducer<List<Message>> appendReducer = (existing, newValue) -> {
 *     List<Message> merged = new ArrayList<>(existing);
 *     merged.addAll(newValue);
 *     return merged;
 * };
 * }</pre>
 *
 * @param <T>
 *            值类型
 */
@FunctionalInterface
public interface StateReducer<T> {

    /**
     * 合并两个值
     *
     * @param existing
     *            当前已有的值
     * @param newValue
     *            新到来的值
     * @return 合并后的值
     */
    T reduce(T existing, T newValue);
}
