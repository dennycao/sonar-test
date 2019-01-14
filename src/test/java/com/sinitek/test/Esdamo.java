package com.sinitek.test;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Esdamo {

    @Test
    public void test1 () {
        System.out.println("start test:");

        //指定集群
        Settings settings = Settings.builder().put("cluster.name", "cluster_name").build();
        //创建访问es服务器的客户端
        try {
            TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));

        //数据查询
        GetResponse response = client.prepareGet("ecommerce", "product", "1").execute().actionGet();

        //数据显示
        System.out.println(response.getSourceAsString());

        client.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        System.out.println("start end。");

    }
}
