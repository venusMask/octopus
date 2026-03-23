package org.venus.octopus.common.annotation;

import java.lang.annotation.*;

/**
 * 标注一个类为图节点组件
 * <p>
 * 被该注解标注的类将被框架识别为可注册的节点，支持按名称查找和注册。
 * </p>
 *
 * <p>
 * 使用示例：
 * </p>
 * 
 * <pre>
 * {
 *     &#64;code
 *     &#64;NodeComponent("my-agent")
 *     public class MyAgentNode implements Node<MapAgentState> {
 *         @Override
 *         public MapAgentState process(MapAgentState state) {
 *             // 节点逻辑...
 *             return state;
 *         }
 *     }
 * }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NodeComponent {

    /**
     * 节点名称，如果不指定则使用类名（首字母小写）
     */
    String value() default "";
}
