package com.zsm.springboot.elasticsearch.to;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * @author: zhangsiming
 * @Date: 2019-10-18 15:47
 * @Description: es数据
 */
@Data
@NoArgsConstructor
@ToString
public class DataTo {

    /**
     * 名称
     */
    private String name;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 单价
     */
    private double unitPrice;

}
