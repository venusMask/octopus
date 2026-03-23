package org.venus.octopus.api.llm;

import org.venus.octopus.common.utils.AssertUtils;

import java.util.ArrayList;
import java.util.List;

public class EmbeddingResponse {

    private final List<List<Double>> embeddings;
    private final TokenUsage tokenUsage;

    public EmbeddingResponse(List<List<Double>> embeddings, TokenUsage tokenUsage) {
        AssertUtils.notNull(embeddings, "Embeddings Cannot be null");
        this.embeddings = new ArrayList<>();
        for (List<Double> emb : embeddings) {
            this.embeddings.add(new ArrayList<>(emb));
        }
        this.tokenUsage = tokenUsage != null ? tokenUsage : TokenUsage.EMPTY;
    }

    public List<List<Double>> getEmbeddings() {
        return embeddings;
    }

    public TokenUsage getTokenUsage() {
        return tokenUsage;
    }
}
