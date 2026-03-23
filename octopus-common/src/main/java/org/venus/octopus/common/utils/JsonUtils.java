package org.venus.octopus.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.venus.octopus.common.exception.OctopusException;

import java.io.IOException;

/**
 * 全局 JSON 序列化/反序列化工具类
 * <p>
 * 封装了 Jackson 的基础配置，支持 JDK8 时间格式转换，忽略未知字段等容错能力。 供整个框架（如模型通信参数封装，内存缓存序列化）使用。
 * </p>
 */
public final class JsonUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        // 注册对 JDK8 时间类型（LocalDate、LocalDateTime等）的支持
        MAPPER.registerModule(new JavaTimeModule());
        // 关闭解析时未知的属性报错
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Date 类的默认序列化为 timestamp 关闭，采用 iso_8601
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // 忽略 Null 值，避免生成的 JSON 带有多余的 null 字段
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private JsonUtils() {
    }

    /**
     * 将对象转换成 JSON 字符串
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON: {}", e.getMessage(), e);
            throw new OctopusException("JSON Serialization error", e);
        }
    }

    /**
     * 将 JSON 字符串转换成指定的 Java 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            log.error("Failed to deserialize JSON to {}: {}", clazz.getName(), e.getMessage(), e);
            throw new OctopusException("JSON Deserialization error", e);
        }
    }

    /**
     * 将 JSON 字符串转换成复杂的 Java 泛型对象集合 (如 List<User>)
     */
    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return MAPPER.readValue(json, typeRef);
        } catch (IOException e) {
            log.error("Failed to deserialize JSON with TypeReference: {}", e.getMessage(), e);
            throw new OctopusException("JSON Deserialization error", e);
        }
    }

    /**
     * 获取全局底层的 ObjectMapper 供高阶场景使用
     */
    public static ObjectMapper getMapper() {
        return MAPPER;
    }
}
