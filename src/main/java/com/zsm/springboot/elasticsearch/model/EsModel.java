package com.zsm.springboot.elasticsearch.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
/**
 * @author: zhangsiming
 * @Date: 2019-10-18 15:47
 * @Description: es 实体类
 *   @Data @NoArgsConstructor 注解的类，编译后会自动给我们加上下列方法：
 * 所有属性的get和set方法
 * toString 方法
 * hashCode方法
 * equals方法
 *  @ToString 任何类定义可以使用@ToString让 lombok 生成toString()方法的实现
 *  @NoArgsConstructor ： 生成一个无参数的构造方法
 *  @AllArgsContructor： ?会生成一个包含所有变量
 *  @RequiredArgsConstructor： 会生成一个包含常量，和标识了NotNull的变量的构造方法。生成的构造方法是私有的private
 */
@Data
@NoArgsConstructor
@ToString
public class EsModel {
    /**
     * id
     */
    private String id;
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
