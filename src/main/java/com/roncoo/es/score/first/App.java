package com.roncoo.es.score.first;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class App {
    //��ȡ�ͻ��˶���
    TransportClient client;

    @Before
    public void getClient() throws UnknownHostException {
        Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();//������һ����Ⱥ

        //��ȡ�ͻ��˶���
        client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
//        System.out.println(client.toString());
    }

    //��������
    @Test
    public void createIndex() throws IOException {
        //indices(һ������)
        //��������
//        CreateIndexResponse index = client.admin().indices().prepareCreate("index").get();
//        System.out.println(index.toString());

        IndexResponse indexResponse = client.prepareIndex("test_index", "test_type", "1")
                .setSource(XContentFactory.jsonBuilder().startObject()
                        .field("date", "2020-01-01")
                        .field("title", "what??")
                        .field("name", "zhaolusi")
                        .endObject()).get();
        System.out.println(indexResponse.toString());


        //�ر���Դ
        client.close();
    }

    @Test
    public void deleteIndex() {
        DeleteIndexResponse ymd_index = client.admin().indices().prepareDelete("ymd_index").get();
        System.out.println(ymd_index.toString());
        client.close();
    }

    @Test
    public void getEmployee() {
        GetResponse getFields = client.prepareGet("test_index", "test_type", "1").get();
        System.out.println(getFields.getSourceAsString());
    }


    @Test
    public void updateEmployee() throws IOException {
        UpdateResponse response = client.prepareUpdate("test_index", "test_type", "1")
                .setDoc(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("name", "yyy")
                        .endObject()).get();

        System.out.println(response.toString());
    }
}
