package com.atguigu.gmall.search;

import com.atguigu.gmall.search.bean.Person;
import com.atguigu.gmall.search.service.SearchService;
import com.atguigu.gmall.search.vo.SearchParamVo;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-12 11:18 星期一
 * description:
 */
@SpringBootTest
public class ElasticSearchTest {

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    public void CreateIndex(){
        //创建索引
        boolean test = elasticsearchRestTemplate.createIndex("test");
        System.out.println(test);
    }

    @Test
    public void testDeleteIndex(){
        //删除索引
        boolean test = elasticsearchRestTemplate.deleteIndex("test");
        System.out.println(test);
    }

    @Test
    public void testCrateIndex2(){
        //操作索引之创建索引
        boolean person = elasticsearchRestTemplate.indexOps(IndexCoordinates.of("person")).create();
        System.out.println(person);
    }

    @Test
    public void testPost(){
        IndexQuery person1 = new IndexQueryBuilder()
                .withId("1")
                .withObject(new Person(1L, "张三", 18 )).build();
        elasticsearchRestTemplate.index(person1,IndexCoordinates.of("person"));
    }


    @Test
    public void testQuery1(){
        Person person = elasticsearchRestTemplate.get("1", Person.class,IndexCoordinates.of("person"));
        System.out.println(person);
    }

    @Test
    public void testDelete(){
        String person = elasticsearchRestTemplate.delete("1", IndexCoordinates.of("person"));
        System.out.println(person);
    }

    @Test
    public void testDeleteIndex2(){
        boolean person = elasticsearchRestTemplate.indexOps(IndexCoordinates.of("person")).delete();
        System.out.println(person);
    }

    @Test
    public void testSearchAll(){
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        RangeQueryBuilder age = QueryBuilders.rangeQuery("age").lte(20);
        boolQuery.must(age);
        MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("name", "张");
        boolQuery.must(matchQuery);
        Query query = new NativeSearchQuery(boolQuery);
        SearchHits<Person> person = elasticsearchRestTemplate.search(query, Person.class, IndexCoordinates.of("person"));
        System.out.println("检索结果：" + person);
        List<SearchHit<Person>> searchHits = person.getSearchHits();
        System.out.println(searchHits);
    }


    /**
     * 测试检索功能
     */
    @Autowired
    SearchService searchService;
    @Test
    public void testSearch(){
        SearchParamVo searchParamVo = new SearchParamVo();
        searchParamVo.setCategory3Id(61L);
        SearchResponseVo search = searchService.search(searchParamVo);

    }
}
