package com.atguigu.gulimall.gulimallsearch;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.gulimallsearch.config.GulimallElasticSearchConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GulimallSearchApplicationTests {

	@Autowired
	private RestHighLevelClient client;

	@Test
	public void contextLoads() {
		System.out.println(client);
	}

	/**
	 * 测试存储数据到es
	 */
	@Test
	public void indexData() throws IOException {
		IndexRequest indexRequest = new IndexRequest("users");
		indexRequest.id("1"); //数据的id
//        indexRequest.source("userName","zhangsan","age","18","gender","男");
		User user = new User();
		user.setUserName("周杰伦");
		user.setAge(18);
		user.setGender("男");
		String jsonString = JSON.toJSONString(user);
		indexRequest.source(jsonString, XContentType.JSON); //要保存的内容

		// 执行操作
		IndexResponse index = client.index(indexRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

		// 提取有用的响应数据
		System.out.println(index);
	}

	@Data
	class User{
		private String userName;
		private String gender;
		private Integer age;
	}

	@Test
	public void searchData() throws IOException {
		//1、创建检索请求
		SearchRequest searchRequest = new SearchRequest();
		//指定索引
		searchRequest.indices("bank");
		//指定DSL，检索条件
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		//1.1)、构造检索条件
		sourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
		//1.2)、按照年龄的值分布进行聚合
		TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
		sourceBuilder.aggregation(ageAgg);
		//1.3)、
		AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
		sourceBuilder.aggregation(balanceAvg);

		System.out.println("检索条件==>" + sourceBuilder.toString());


		searchRequest.source(sourceBuilder);

		//2、执行检索
		SearchResponse searchResponse = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

		//3、分析结果  searchResponse
		System.out.println(searchResponse.toString());

		//3.1)、获取所有查到的数据
		SearchHits hits = searchResponse.getHits();
		SearchHit[] searchHits = hits.getHits();
		/**
		 * "_index" : "bank",
		 *         "_type" : "account",
		 *         "_id" : "20",
		 *         "_score" : 1.0,
		 *         "_source" : {
		 */
		for(SearchHit hit : searchHits){
			String string = hit.getSourceAsString();
			Account account = JSON.parseObject(string, Account.class);
			System.out.println("account: " + account);
		}

		//3.2)、获取这次检索到的分析信息
		Aggregations aggregations = searchResponse.getAggregations();
//        for(Aggregation aggregation : aggregations.asList()){
//            System.out.println("当前聚合：" + aggregation.getName());
//        }
		Terms ageAgg1 = aggregations.get("ageAgg");
		for (Terms.Bucket bucket : ageAgg1.getBuckets()) {
			String keyAsString = bucket.getKeyAsString();
			System.out.println("年龄：" + keyAsString + "==>" + bucket.getDocCount());
		}

		Avg balanceAvg1 = aggregations.get("balanceAvg");
		System.out.println("平均薪资：" + balanceAvg1.getValue());

	}

	@ToString
	@Data
	static class Account
	{
		private int account_number;

		private int balance;

		private String firstname;

		private String lastname;

		private int age;

		private String gender;

		private String address;

		private String employer;

		private String email;

		private String city;

		private String state;

	}


}
