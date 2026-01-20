/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.assistant.agent.start.search;

import com.alibaba.assistant.agent.extension.search.model.SearchRequest;
import com.alibaba.assistant.agent.extension.search.model.SearchResultItem;
import com.alibaba.assistant.agent.extension.search.model.SearchSourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MockBaiduWebSearchProvider 单元测试
 *
 * @author Assistant Agent Team
 */
class MockBaiduWebSearchProviderTest {

    @Test
    @DisplayName("测试简单模式构造 - null API Key应使用简单模式")
    void testConstructor_withNullApiKey_shouldUseSimpleMode() {
        MockBaiduWebSearchProvider p = new MockBaiduWebSearchProvider(null);
        SearchRequest request = new SearchRequest("Spring AI Alibaba");
        request.setTopK(3);

        List<SearchResultItem> results = p.search(request);

        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    @Test
    @DisplayName("集成测试 - 使用真实API Key")
    @EnabledIfEnvironmentVariable(named = "QIANFAN_API_KEY", matches = ".+")
    void testRealApiIntegration() {
        String realApiKey = System.getenv("QIANFAN_API_KEY");
        MockBaiduWebSearchProvider realProvider = new MockBaiduWebSearchProvider(realApiKey);

        SearchRequest request = new SearchRequest("Spring AI Alibaba");
        request.setTopK(5);

        List<SearchResultItem> results = realProvider.search(request);

        assertNotNull(results);
        assertFalse(results.isEmpty(), "真实API应返回搜索结果");

        System.out.println("=== 真实API返回结果 ===");
        System.out.println("结果数量: " + results.size());

        for (SearchResultItem item : results) {
            System.out.println("---");
            System.out.println("标题: " + item.getTitle());
            System.out.println("URL: " + item.getUri());
            System.out.println("评分: " + item.getScore());
            String snippet = item.getSnippet();
            if (snippet != null && snippet.length() > 100) {
                snippet = snippet.substring(0, 100) + "...";
            }
            System.out.println("摘要: " + snippet);
        }

        // 验证第一个结果的完整性
        SearchResultItem first = results.get(0);
        assertNotNull(first.getId());
        assertEquals(SearchSourceType.WEB, first.getSourceType());
        assertNotNull(first.getTitle());
        assertFalse(first.getTitle().isEmpty());
    }
}
