package org.venus.octopus.api.llm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.venus.octopus.common.exception.OctopusException;

import java.util.Map;
import java.util.ServiceLoader;

/**
 * 模型构建管理器
 * <p>
 * 提供简单的静态门面以发现并聚合被 SPI 加载的 {@link ChatModelFactory}。 允许用户通过仅仅提供 `providerName`
 * 来直接取得就绪的 {@link ChatModel} 实例。
 * </p>
 */
public final class ChatModels {

    private static final Logger log = LoggerFactory.getLogger(ChatModels.class);

    private ChatModels() {
    }

    /**
     * 从当前类路径中通过 SPI 获取指定厂商的模型实例
     *
     * @param providerName
     *            厂商唯一标识（如 "openai"）
     * @param properties
     *            配置属性字典（用于透传 api_key 等必要信息）
     * @return 初始化就绪的构建模型
     * @throws OctopusException
     *             说明对应的厂商 SPI 插件不存在或初始化失败
     */
    public static ChatModel create(String providerName, Map<String, Object> properties) {
        if (providerName == null || providerName.isBlank()) {
            throw new IllegalArgumentException("providerName cannot be empty");
        }

        // 懒汉式 ServiceLoader 查找
        ServiceLoader<ChatModelFactory> loader = ServiceLoader.load(ChatModelFactory.class);

        for (ChatModelFactory factory : loader) {
            log.debug("Found ChatModelFactory via SPI: [{}]", factory.providerName());
            if (providerName.equalsIgnoreCase(factory.providerName())) {
                log.info("Matching provider [{}] found. Delegating creation to factory instance.", providerName);
                return factory.createModel(properties);
            }
        }

        throw new OctopusException("SPI Loading Failed: 无法在 classpath 下找到名为 '" + providerName
                + "' 的 ChatModelFactory 实现。请确保对应的 vendor-plugin 已被引入依赖！");
    }
}
