package org.venus.octopus.api.graph;

/**
 * 图常量与基础定义接口
 * <p>
 * 定义图流程编排中的特殊节点名称常量：
 * <ul>
 *   <li>{@link #START} — 图的入口节点，所有图的起始边必须从 START 出发</li>
 *   <li>{@link #END} — 图的终止节点，到达 END 时图执行结束</li>
 * </ul>
 * </p>
 */
public interface Graph {

    /**
     * 图的入口节点名称
     */
    String START = "__start__";

    /**
     * 图的终止节点名称
     */
    String END = "__end__";
}
