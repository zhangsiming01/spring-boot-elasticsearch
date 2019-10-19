package com.zsm.springboot.elasticsearch.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zsm.springboot.elasticsearch.service.EsModelService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: zhangsiming
 * @Date: 2019-10-18 15:47
 * @Description: es Model 接口实现类
 */
@Configuration
public class EsModelServiceImpl implements EsModelService {
    private static  final Logger logger = LoggerFactory.getLogger(EsModelServiceImpl.class);


    @Autowired
    private TransportClient transportClient;

    private TransportClient client;
    @PostConstruct
    public void init(){
        client = this.transportClient;
    }

    /**
     * 创建索引
     * @param index
     * @return
     */
    @Override
    public boolean createIndex(String index) {
        if (!isIndexExist(index)){
            logger.info("index is not exits!");
        }
        CreateIndexResponse indexResponse = client.admin().indices().prepareCreate(index).execute().actionGet();
        logger.info("执行建立成功？"+indexResponse.isAcknowledged());
        return indexResponse.isAcknowledged();
    }

    /**
     *  验证index是否存在
     * @param index
     * @return
     */
    @Override
    public boolean isIndexExist(String index){
        IndicesExistsResponse indicesExistsResponse = client.admin().indices().exists(new IndicesExistsRequest(index)).actionGet();
        if (indicesExistsResponse.isExists()){
            logger.info("index["+index+"] is exist!!!!!!");
        }else {
            logger.info("index["+index+"] is not exist!!!!!!");
        }
        return indicesExistsResponse.isExists();
    }

    @Override
    public boolean deleteIndex(String index) {
        if (!isIndexExist(index)){
            logger.info("Index is not exits!!!");
        }
        DeleteIndexResponse deleteIndexResponse = client.admin().indices().prepareDelete(index).execute().actionGet();
        if (deleteIndexResponse.isAcknowledged()){
            logger.info("delete index["+index+"] successfully !!!!!");
        }else {
            logger.info("Fail to delete index " + index );
        }
        return deleteIndexResponse.isAcknowledged();
    }

    @Override
    public String saveData(JSONObject jsonObject, String index, String type, String id) {
        IndexResponse response = client.prepareIndex(index,type,id).setSource(jsonObject).get();
        logger.info("saveData response status:{},id:{}",response.status().getStatus(),response.getId());
        return response.getId();
    }

    @Override
    public void updateDataById(JSONObject jsonObject, String index, String type, String id) {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(index).type(type).id(id).doc(jsonObject);
        client.update(updateRequest);
    }

    @Override
    public List<Map<String, Object>>  queryMatchData(String index, String type, QueryBuilder query, Integer size,
                                                     String fields, String sortField, String highlightField) {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
        if (StringUtils.isNotEmpty(type)) {
            searchRequestBuilder.setTypes(type.split(","));
        }
        if (StringUtils.isNotEmpty(highlightField)) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            // 设置高亮字段
            highlightBuilder.field(highlightField);
            searchRequestBuilder.highlighter(highlightBuilder);
        }
        searchRequestBuilder.setQuery(query);
        if (StringUtils.isNotEmpty(fields)) {
            searchRequestBuilder.setFetchSource(fields.split(","), null);
        }
        searchRequestBuilder.setFetchSource(true);
        if (StringUtils.isNotEmpty(sortField)) {
            searchRequestBuilder.addSort(sortField, SortOrder.DESC);
        }
        if (size != null && size > 0) {
            searchRequestBuilder.setSize(size);
        }
        //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询
        logger.info("\n{}", searchRequestBuilder);
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        long totalHits = searchResponse.getHits().totalHits;
        long length = searchResponse.getHits().getHits().length;
        logger.info("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);
        if (searchResponse.status().getStatus() == 200) {
            // 解析对象
            return setSearchResponse(searchResponse, highlightField);
        }
        return null;
    }

    @Override
    public Map<String, Object> searchDataById(String index, String type, String id, String fields) {
        GetRequestBuilder getRequestBuilder = client.prepareGet(index, type, id);
        if (StringUtils.isNotEmpty(fields)){
            getRequestBuilder.setFetchSource(fields.split(","),null);
        }
        GetResponse getResponse = getRequestBuilder.execute().actionGet();
        return getResponse.getSource();
    }

    private static List<Map<String, Object>> setSearchResponse(SearchResponse searchResponse, String highlightField) {
        List<Map<String, Object>> sourceList = new ArrayList<Map<String, Object>>();
        StringBuffer stringBuffer = null;
        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            searchHit.getSourceAsMap().put("id", searchHit.getId());
            if (StringUtils.isNotEmpty(highlightField)) {
                System.out.println("遍历 高亮结果集，覆盖 正常结果集" + searchHit.getSourceAsMap());
                Text[] text = searchHit.getHighlightFields().get(highlightField).getFragments();
                if (text != null) {
                    for (Text str : text) {
                        stringBuffer = new StringBuffer();
                        stringBuffer.append(str.string());
                    }
                //遍历 高亮结果集，覆盖 正常结果集
                    searchHit.getSourceAsMap().put(highlightField, stringBuffer.toString());
                }
            }
            sourceList.add(searchHit.getSourceAsMap());
        }
        return sourceList;
    }
}
