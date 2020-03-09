package com.cxytiandi.kitty.db.elasticsearch.client;

import com.cxytiandi.kitty.common.cat.CatTransactionManager;
import com.cxytiandi.kitty.common.json.JsonUtils;
import com.dianping.cat.Cat;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-03-09 18:32
 */
public class KittyRestHighLevelClient {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    private final String ES_CAT_TYPE = "ElasticSearch";

    public UpdateResponse update(UpdateRequest updateRequest, RequestOptions options) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("updateRequest", updateRequest.toString());
            return CatTransactionManager.newTransaction(() -> {
                try {
                    return restHighLevelClient.update(updateRequest, options);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, ES_CAT_TYPE, "update", data);
        } catch (Exception e) {
            Cat.logError(e);
            throw new RuntimeException(e);
        }
    }

    public SearchResponse search(SearchRequest searchRequest, RequestOptions options) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("searchRequest", searchRequest.toString());
            return CatTransactionManager.newTransaction(() -> {
                try {
                    return restHighLevelClient.search(searchRequest, options);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, ES_CAT_TYPE, "search");
        } catch (Exception e) {
            Cat.logError(e);
            throw new RuntimeException(e);
        }
    }

    public IndexResponse index(IndexRequest indexRequest, RequestOptions options) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("indexRequest", indexRequest.toString());
            return CatTransactionManager.newTransaction(() -> {
                try {
                    return restHighLevelClient.index(indexRequest, options);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, ES_CAT_TYPE, "index");
        } catch (Exception e) {
            Cat.logError(e);
            throw new RuntimeException(e);
        }
    }

    public DeleteResponse delete(DeleteRequest deleteRequest, RequestOptions options) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("deleteRequest", deleteRequest.toString());
            return CatTransactionManager.newTransaction(() -> {
                try {
                    return restHighLevelClient.delete(deleteRequest, options);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, ES_CAT_TYPE, "delete");
        } catch (Exception e) {
            Cat.logError(e);
            throw new RuntimeException(e);
        }
    }

    public <T> IndexResponse index(String index, String type, T entity) {
        IndexRequest indexRequest = new IndexRequest(index, type);
        indexRequest.source(JsonUtils.toJson(entity), XContentType.JSON);
        IndexResponse indexResponse = index(indexRequest, RequestOptions.DEFAULT);
        return indexResponse;
    }

    public <T> IndexResponse index(String index, String type, T entity, String id) {
        IndexRequest indexRequest = new IndexRequest(index, type, id);
        indexRequest.source(JsonUtils.toJson(entity), XContentType.JSON);
        IndexResponse indexResponse = index(indexRequest, RequestOptions.DEFAULT);
        return indexResponse;
    }

}