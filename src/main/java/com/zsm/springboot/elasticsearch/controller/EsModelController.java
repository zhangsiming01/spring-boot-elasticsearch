package com.zsm.springboot.elasticsearch.controller;

import com.alibaba.fastjson.JSONObject;
import com.zsm.springboot.elasticsearch.model.EsModel;
import com.zsm.springboot.elasticsearch.service.EsModelService;
import com.zsm.springboot.elasticsearch.to.DataTo;
import com.zsm.springboot.elasticsearch.to.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author: zhangsiming
 * @Date: 2019-10-18 15:47
 * @Description: controller接口访问
 */
@RestController
@RequestMapping("zsm")
public class EsModelController {
    @Autowired
    private EsModelService esModelService;

    @GetMapping(value = "/createIndex")
    @ApiOperation(value = "es创建索引")
    public Result<String> createIndex(@ApiParam(value = "indexName") @RequestParam String indexName) {
        if (!esModelService.isIndexExist(indexName)) {
            esModelService.createIndex(indexName);
        } else {
            return Result.error("index已存在" + indexName);
        }
        return Result.success("index创建成功" + indexName);
    }

    @DeleteMapping(value = "/deleteIndex")
    @ApiOperation(value = "es删除索引")
    public Result<String> deleteIndex(@ApiParam(value = "indexName") @RequestParam String indexName) {
        if (esModelService.isIndexExist(indexName)) {
            esModelService.deleteIndex(indexName);
        } else {
            return Result.error("index不存在" + indexName);
        }
        return Result.success("index 删除成功" + indexName);
    }

    /**
     * 通过model
     *
     * @param to
     * @param indexName
     * @param type
     * @return
     */
    @PostMapping("saveDataByModel")
    @ApiOperation("es数据添加通过model形式")
    public Result<String> saveDataByModel(@RequestBody DataTo to, @ApiParam(value = "indexName") @RequestParam String indexName, @ApiParam(value = "type") @RequestParam String type) {
        EsModel model = new EsModel();
        model.setId(String.valueOf(System.currentTimeMillis()));
        model.setAge(to.getAge());
        model.setName(to.getName());
        model.setUnitPrice(to.getUnitPrice());
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(model);
        return Result.success(esModelService.saveData(jsonObject, indexName, type, jsonObject.getString("id")));
    }


    @PostMapping("saveDataByJson")
    @ApiOperation("es数据添加通过Json形式")
    public Result<String> saveDataByJson(@RequestBody DataTo to, @ApiParam(value = "indexName") @RequestParam String indexName, @ApiParam(value = "type") @RequestParam String type) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", String.valueOf(System.currentTimeMillis()));
        jsonObject.put("age", to.getAge());
        jsonObject.put("name", to.getName());
        jsonObject.put("unitPrice", to.getUnitPrice());
        return Result.success(esModelService.saveData(jsonObject, indexName, type, jsonObject.getString("id")));
    }

    @PostMapping("updateDataById")
    @ApiOperation("es数据更新通过Json形式")
    public Result<String> updateDataById(@RequestBody DataTo to, @ApiParam(value = "indexName") @RequestParam String indexName, @ApiParam(value = "type") @RequestParam String type,
                                         @ApiParam(value = "id") @RequestParam String id) {
        if (StringUtils.isNotEmpty(id)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", String.valueOf(System.currentTimeMillis()));
            jsonObject.put("age", to.getAge());
            jsonObject.put("name", to.getName());
            jsonObject.put("unitPrice", to.getUnitPrice());
            esModelService.updateDataById(jsonObject, indexName, type, id);
            return Result.success("id:" + id);
        } else {
            return Result.error("id不能为空");
        }
    }

    /**
     * 查询数据
     * 模糊查询
     *
     * @return
     */
    @GetMapping("queryMatchData")
    @ApiOperation("es数据模糊查询")
    public Result<List<Map<String, Object>>> queryMatchData(@ApiParam(value = "indexName") @RequestParam String indexName, @ApiParam(value = "type") @RequestParam String type,
                                                            @ApiParam(value = "name") @RequestParam String name) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolean matchPhrase = false;
        if (matchPhrase == Boolean.FALSE) {
            //不进行分词搜索
            boolQuery.must(QueryBuilders.matchPhraseQuery("name", name));
        } else {
            boolQuery.must(QueryBuilders.matchQuery("name", name));
        }
        List<Map<String, Object>> list = esModelService.queryMatchData(indexName, type, boolQuery, 10, "name", null, "name");
        return Result.success(list);
    }

    @GetMapping("searchDataById")
    @ApiOperation("es数据根据id查询查询")
    public Result<Map<String, Object>> searchDataById(@ApiParam(value = "indexName", required = true) @RequestParam String indexName, @ApiParam(value = "type", required = true) @RequestParam String type,
                                                      @ApiParam(value = "id", required = true) @RequestParam String id) {
        return Result.success(esModelService.searchDataById(indexName, type, id, null));
    }
}
