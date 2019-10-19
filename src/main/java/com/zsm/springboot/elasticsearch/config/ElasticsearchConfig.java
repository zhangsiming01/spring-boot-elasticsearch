package com.zsm.springboot.elasticsearch.config;


import org.elasticsearch.client.transport.TransportClient;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.net.InetAddress;


/**
 * @author: zhangsiming
 * @Date: 2019-10-18 15:47
 * @Description: 用于定义配置文件类,可替换为xml配置文件
 */
@Configuration
public class ElasticsearchConfig {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchConfig.class);
    /**
     * elk集群地址
     */
    @Value("${elasticsearch.ip}")
    private String hostName;

    /**
     * 端口
     */
    @Value("${elasticsearch.port}")
    private String port;

    /**
     * 集群名称
     */
    @Value("${elasticsearch.cluster.name}")
    private String clusterName;

    /**
     * 连接池
     */
    @Value("${elasticsearch.pool}")
    private String poolSize;

    /**
     * Bean name default  函数名字
     *
     * @return
     */
    @Bean(name = "transportClient")
    public TransportClient transportClient(){
        logger.info("Elasticsearch初始化开始..........");
        TransportClient transportClient = null;
        try{
            /**
             * 配置信息
             * cluster.name 集群名称
             * client.transport.sniff 增加嗅探机制，找到ES集群
             * thread_pool_search.size  增加线程池个数 ，暂时设置为5
             */
            Settings esSetting = Settings.builder()
                    .put("cluster.name", clusterName)
                    .put("client.transport.sniff", true)
                    .put("thread_pool.search.size", Integer.parseInt(poolSize))
                    .build();
            /**
             * 配置信息Settings自定义bu
             */
            transportClient = new PreBuiltTransportClient(esSetting);
            TransportAddress transportAddress = new TransportAddress(InetAddress.getByName(hostName),Integer.valueOf(port));
            transportClient.addTransportAddresses(transportAddress);
        }catch (Exception e){
            logger.error("elasticsearch TransportClient create error!!",e);
        }
        return transportClient;
    }
}
