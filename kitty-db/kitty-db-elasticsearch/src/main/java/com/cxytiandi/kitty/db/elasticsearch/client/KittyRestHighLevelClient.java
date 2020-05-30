package com.cxytiandi.kitty.db.elasticsearch.client;

import com.cxytiandi.kitty.common.cat.CatTransactionManager;
import com.cxytiandi.kitty.common.json.JsonUtils;
import com.cxytiandi.kitty.common.page.Page;
import com.cxytiandi.kitty.db.elasticsearch.constant.ElasticSearchConstant;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;

/**
 * ES REST 客户端
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-03-09 22:32
 */
public class KittyRestHighLevelClient {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public UpdateResponse update(UpdateRequest updateRequest) {
        return this.doUpdate(updateRequest, RequestOptions.DEFAULT);
    }

    public UpdateResponse update(UpdateRequest updateRequest, RequestOptions options) {
        return this.doUpdate(updateRequest, options);
    }

    public <T> UpdateResponse update(String index, String type, String id, T entity) {
        UpdateRequest updateRequest = new UpdateRequest(index, type, id);
        updateRequest.doc(JsonUtils.toJson(entity), XContentType.JSON);
        return update(updateRequest, RequestOptions.DEFAULT);
    }

    public <T> UpdateResponse update(String index, String type, String id, T entity, RequestOptions options) {
        UpdateRequest updateRequest = new UpdateRequest(index, type, id);
        updateRequest.doc(JsonUtils.toJson(entity), XContentType.JSON);
        return update(updateRequest, options);
    }

    public UpdateResponse update(String index, String type, String id, Map<String, Object> document) {
        UpdateRequest updateRequest = new UpdateRequest(index, type, id);
        updateRequest.doc(JsonUtils.toJson(document), XContentType.JSON);
        return update(updateRequest, RequestOptions.DEFAULT);
    }

    public UpdateResponse update(String index, String type, String id, Map<String, Object> document, RequestOptions options) {
        UpdateRequest updateRequest = new UpdateRequest(index, type, id);
        updateRequest.doc(JsonUtils.toJson(document), XContentType.JSON);
        return update(updateRequest, options);
    }

    private UpdateResponse doUpdate(UpdateRequest updateRequest, RequestOptions options) {
        Map<String, Object> catData = new HashMap<>(1);
        catData.put(ElasticSearchConstant.UPDATE_REQUEST, updateRequest.toString());
        return CatTransactionManager.newTransaction(() -> {
            try {
                return restHighLevelClient.update(updateRequest, options);
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, ElasticSearchConstant.ES_CAT_TYPE, ElasticSearchConstant.UPDATE, catData);
    }

    public SearchResponse search(SearchRequest searchRequest, RequestOptions options) {
        Map<String, Object> catData = new HashMap<>(1);
        catData.put(ElasticSearchConstant.SEARCH_REQUEST, searchRequest.toString());
        return CatTransactionManager.newTransaction(() -> {
            try {
                return restHighLevelClient.search(searchRequest, options);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, ElasticSearchConstant.ES_CAT_TYPE, ElasticSearchConstant.SEARCH, catData);
    }

    public <T> Page<T> searchByPage(SearchRequest searchRequest, Class<T> entityClass) {
        return searchByPage(searchRequest, entityClass, RequestOptions.DEFAULT);
    }

    public <T> Page<T> searchByPage(int page, int pageSize, SearchRequest searchRequest, Class<T> entityClass) {
        searchRequest.source().from(Page.page2Start(page, pageSize)).size(pageSize);
        return searchByPage(searchRequest, entityClass, RequestOptions.DEFAULT);
    }

    public <T> Page<T> searchByPage(SearchRequest searchRequest, Class<T> entityClass, RequestOptions options) {
        Map<String, Object> catData = new HashMap<>(1);
        catData.put(ElasticSearchConstant.SEARCH_REQUEST, searchRequest.toString());
        return CatTransactionManager.newTransaction(() -> {
            try {
                SearchResponse searchResponse = restHighLevelClient.search(searchRequest, options);
                long totalHits = searchResponse.getHits().getTotalHits();
                List<T> datas = buildSearchResult(searchResponse, entityClass);
                SearchSourceBuilder searchSourceBuilder = searchRequest.source();
                return new Page(searchSourceBuilder.from(), searchSourceBuilder.size(), datas, totalHits);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, ElasticSearchConstant.ES_CAT_TYPE, ElasticSearchConstant.SEARCH, catData);
    }

    public <T> List<T> search(SearchRequest searchRequest, Class<T> entityClass, RequestOptions options) {
        Map<String, Object> catData = new HashMap<>(1);
        catData.put(ElasticSearchConstant.SEARCH_REQUEST, searchRequest.toString());
        return CatTransactionManager.newTransaction(() -> {
            try {
                SearchResponse searchResponse = restHighLevelClient.search(searchRequest, options);
                List<T> datas = buildSearchResult(searchResponse, entityClass);
                return datas;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, ElasticSearchConstant.ES_CAT_TYPE, ElasticSearchConstant.SEARCH, catData);
    }

    private <T> List<T> buildSearchResult(SearchResponse searchResponse, Class<T> entityClass) {
        List<T> datas = new ArrayList<>();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            String source = hit.getSourceAsString();
            datas.add(JsonUtils.toBean(entityClass, source));
        }
        return datas;
    }

    public <T> List<T> search(SearchRequest searchRequest, Class<T> entityClass) {
        return search(searchRequest, entityClass, RequestOptions.DEFAULT);
    }

    public IndexResponse index(IndexRequest indexRequest) {
        return index(indexRequest, RequestOptions.DEFAULT);
    }

    public IndexResponse index(IndexRequest indexRequest, RequestOptions options) {
        Map<String, Object> catData = new HashMap<>(1);
        catData.put(ElasticSearchConstant.INDEX_REQUEST, indexRequest.toString());
        return CatTransactionManager.newTransaction(() -> {
            try {
                return restHighLevelClient.index(indexRequest, options);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, ElasticSearchConstant.ES_CAT_TYPE, ElasticSearchConstant.INDEX, catData);
    }

    public <T> IndexResponse index(String index, String type, T entity) {
        IndexRequest indexRequest = new IndexRequest(index, type);
        indexRequest.source(JsonUtils.toJson(entity), XContentType.JSON);
        IndexResponse indexResponse = index(indexRequest, RequestOptions.DEFAULT);
        return indexResponse;
    }

    public <T> IndexResponse index(String index, String type, T entity, RequestOptions options) {
        IndexRequest indexRequest = new IndexRequest(index, type);
        indexRequest.source(JsonUtils.toJson(entity), XContentType.JSON);
        IndexResponse indexResponse = index(indexRequest, options);
        return indexResponse;
    }

    public <T> IndexResponse index(String index, String type, T entity, String id) {
        IndexRequest indexRequest = new IndexRequest(index, type, id);
        indexRequest.source(JsonUtils.toJson(entity), XContentType.JSON);
        IndexResponse indexResponse = index(indexRequest, RequestOptions.DEFAULT);
        return indexResponse;
    }

    public <T> IndexResponse index(String index, String type, T entity, String id, RequestOptions options) {
        IndexRequest indexRequest = new IndexRequest(index, type, id);
        indexRequest.source(JsonUtils.toJson(entity), XContentType.JSON);
        IndexResponse indexResponse = index(indexRequest, options);
        return indexResponse;
    }

    public DeleteResponse delete(DeleteRequest deleteRequest) {
       return delete(deleteRequest, RequestOptions.DEFAULT);
    }

    public DeleteResponse delete(DeleteRequest deleteRequest, RequestOptions options) {
        Map<String, Object> catData = new HashMap<>(1);
        catData.put(ElasticSearchConstant.DELETE_REQUEST, deleteRequest.toString());
        return CatTransactionManager.newTransaction(() -> {
            try {
                return restHighLevelClient.delete(deleteRequest, options);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, ElasticSearchConstant.ES_CAT_TYPE, ElasticSearchConstant.DELETE, catData);
    }

    public DeleteResponse delete(String index, String type, String id) {
        DeleteRequest deleteRequest = new DeleteRequest(index, type, id);
        return delete(deleteRequest, RequestOptions.DEFAULT);
    }

    public DeleteResponse delete(String index, String type, String id, RequestOptions options) {
        DeleteRequest deleteRequest = new DeleteRequest(index, type, id);
        return delete(deleteRequest, options);
    }

    public CountResponse count(CountRequest countRequest, RequestOptions options) {
        Map<String, Object> catData = new HashMap<>(1);
        catData.put(ElasticSearchConstant.COUNT_REQUEST, countRequest.toString());
        return CatTransactionManager.newTransaction(() -> {
            try {
                return restHighLevelClient.count(countRequest, options);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, ElasticSearchConstant.ES_CAT_TYPE, ElasticSearchConstant.COUNT, catData);
    }

    public CountResponse count(CountRequest countRequest) {
       return count(countRequest, RequestOptions.DEFAULT);
    }

    public long countResult(CountRequest countRequest) {
        return count(countRequest, RequestOptions.DEFAULT).getCount();
    }

    public long countResult(CountRequest countRequest, RequestOptions options) {
        return count(countRequest, options).getCount();
    }

    public boolean existsSource(GetRequest getRequest, RequestOptions options) {
        Map<String, Object> catData = new HashMap<>(1);
        catData.put(ElasticSearchConstant.EXISTS_SOURCE_REQUEST, getRequest.toString());
        return CatTransactionManager.newTransaction(() -> {
            try {
                return restHighLevelClient.existsSource(getRequest, options);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, ElasticSearchConstant.ES_CAT_TYPE, ElasticSearchConstant.EXISTS_SOURCE, catData);
    }

    public boolean existsSource(GetRequest getRequest) {
       return existsSource(getRequest, RequestOptions.DEFAULT);
    }
}