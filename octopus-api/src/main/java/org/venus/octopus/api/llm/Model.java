package org.venus.octopus.api.llm;

/**
 * 通用大模型基础抽象接口
 *
 * @param <TReq>
 *            模型要求的规范请求体类型
 * @param <TRes>
 *            模型响应的安全封装类型
 */
public interface Model<TReq, TRes> {

    /**
     * 执行底层模型调用
     *
     * @param request
     *            标准化入参
     * @return 响应结果
     */
    TRes call(TReq request);

}
