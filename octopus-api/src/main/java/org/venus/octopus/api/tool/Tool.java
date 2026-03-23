package org.venus.octopus.api.tool;

import java.util.Map;

/**
 * 工具接口
 * <p>
 * Agent 可调用的工具的统一接口。工具封装了特定的外部能力（如搜索、计算、HTTP 请求等），
 * 通过 {@link ToolSpec} 向 LLM 描述自身，LLM 根据 ToolSpec 生成调用参数。
 * </p>
 */
public interface Tool {

    /**
     * 获取工具名称（全局唯一标识）
     */
    String getName();

    /**
     * 获取工具规格描述（用于生成 LLM function calling 配置）
     */
    ToolSpec getSpec();

    /**
     * 执行工具
     *
     * @param args LLM 生成的参数，key 为参数名，value 为参数值
     * @return 工具执行结果（字符串格式，将作为 ToolMessage 内容返回给 LLM）
     * @throws org.venus.octopus.common.exception.ToolException 若执行失败
     */
    String execute(Map<String, Object> args);
}
