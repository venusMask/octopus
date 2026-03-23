package org.venus.octopus.api.llm;

import java.util.List;

/**
 * 嵌入(Embedding)模型接口
 */
public interface EmbeddingModel extends Model<EmbeddingRequest, EmbeddingResponse> {

    @Override
    EmbeddingResponse call(EmbeddingRequest request);

    default EmbeddingResponse call(List<String> inputs) {
        return call(EmbeddingRequest.builder().inputs(inputs).build());
    }

    default EmbeddingResponse call(String input) {
        return call(List.of(input));
    }
}
