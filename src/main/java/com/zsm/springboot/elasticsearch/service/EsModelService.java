package com.zsm.springboot.elasticsearch.service;

import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.List;
import java.util.Map;

/**
 * @author: zhangsiming
 * @Date: 2019-10-18 15:47
 * @Description: es 接口定义类
 */
public interface EsModelService {
    /**
     *  创建索引
     * @param index
     * @return
     */
    boolean createIndex(String index);

    /**
     *  判断索引是否存在
     * @param index
     * @return
     */
    boolean isIndexExist(String index);

    /**
     *  删除索引
     * @param index
     * @return
     */
    boolean deleteIndex(String index);

    /**
     *  添加数据，正定id
     * @param jsonObject 新增的数据
     * @param index 索引，类似数据库
     * @param type 类型， 类似表
     * @param id  数据id
     * @return
     */
    String saveData(JSONObject jsonObject, String index, String type, String id);

    /**
     *  通过id 更新数据
     * @param jsonObject
     * @param index
     * @param type
     * @param id
     */
    void updateDataById(JSONObject jsonObject, String index, String type, String id);

    /**
     * 模糊查询
     * @param index
     * @param type
     * @param query
     * @param size
     * @param fields
     * @param sortField
     * @param highlightField
     * @return
     */
    List<Map<String, Object>> queryMatchData(String index, String type, QueryBuilder query, Integer size,
                                             String fields, String sortField, String highlightField);

    /**
     * 根据id获取数据
     * @param index
     * @param type
     * @param id
     * @param fields
     * @return
     */
    Map<String, Object> searchDataById(String index, String type, String id, String fields);
}
