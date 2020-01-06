package com.roncoo.es.score.first;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;

public class Aggr {
    public static void main(String[] args) throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch")
                .build();
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));


        SearchResponse searchResponse = client.prepareSearch("company")
                .addAggregation(AggregationBuilders
                        .terms("group_by_country")
                        .field("country")
                        .subAggregation(AggregationBuilders
                                .dateHistogram("group_by_join_date")
                                .field("join_date")
                                .dateHistogramInterval(DateHistogramInterval.YEAR)
                                .subAggregation(AggregationBuilders.avg("avg_salary").field("salary")))
                ).execute().actionGet();

        Map<String, Aggregation> aggrMap = searchResponse.getAggregations().asMap();
        StringTerms groupByCountry = (StringTerms) aggrMap.get("group_by_country");
        Iterator<Terms.Bucket> groupByCountryBucketIterator = groupByCountry.getBuckets().iterator();
        while(groupByCountryBucketIterator.hasNext()) {
            Terms.Bucket groupByCountryBucket = groupByCountryBucketIterator.next();
            System.out.println(groupByCountryBucket.getKey() + ":" + groupByCountryBucket.getDocCount());
        }

        client.close();
    }
}
