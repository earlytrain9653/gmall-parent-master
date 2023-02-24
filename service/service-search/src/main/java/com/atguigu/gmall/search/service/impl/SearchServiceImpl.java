package com.atguigu.gmall.search.service.impl;
import com.google.common.collect.Lists;
import com.atguigu.gmall.search.vo.SearchResponseVo.OrderMap;

import com.atguigu.gmall.search.Goods;
import com.atguigu.gmall.search.repo.GoodsRepository;
import com.atguigu.gmall.search.vo.SearchParamVo;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import com.atguigu.gmall.search.service.SearchService;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨林
 * @create 2022-12-12 20:08 星期一
 * description:
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    GoodsRepository goodsRepository;


    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 检索商品
     * @param paramVo
     * @return
     */
    @Override
    public SearchResponseVo search(SearchParamVo paramVo) {

        //1.根据前端传递的请求参数 构建检索条件
        Query query = buildQuery(paramVo);

        //2.获取检索结果
        SearchHits<Goods> result = elasticsearchRestTemplate.search(query, Goods.class, IndexCoordinates.of("goods"));


        //根据检索到的结果  构建前端需要返回的结果
        SearchResponseVo responseVo = buildSearchResp(result,paramVo);

        return responseVo;
    }

    /**
     * 根据检索到的结果  构建前端需要返回的结果
     * @param result
     * @return
     */
    private SearchResponseVo buildSearchResp(SearchHits<Goods> result,SearchParamVo paramVo) {
        int pageSize = 10;
        SearchResponseVo responseVo = new SearchResponseVo();
        //1.检索的参数
        responseVo.setSearchParam(paramVo);
        //2.品牌面包屑
        if (!StringUtils.isEmpty(paramVo.getTrademark())){  //前端的检索条件带有品牌面包屑
            responseVo.setTrademarkParam("品牌:" + paramVo.getTrademark().split(":")[1]);
        }

        //属性面包屑
        String[] props = paramVo.getProps();
        if (props != null && props.length > 0){  //表示前端传递了属性面包屑
            List<SearchResponseVo.Props> propsList = new ArrayList<>();
            Arrays.stream(props)
                    .forEach((item) -> {
                        String[] split = item.split(":");
                        SearchResponseVo.Props props1 = new SearchResponseVo.Props();
                        props1.setAttrName(split[2]);
                        props1.setAttrValue(split[1]);
                        props1.setAttrId(Long.parseLong(split[0]));
                        propsList.add(props1);
                    });
            //遍历前端传递的所有props参数
//            List<SearchResponseVo.Props> propsList = Arrays.stream(props)
//                    .map((item) -> {
//                        String[] split = item.split(":");
//                        SearchResponseVo.Props props1 = new SearchResponseVo.Props();
//                        props1.setAttrName(split[2]);
//                        props1.setAttrValue(split[1]);
//                        props1.setAttrId(Long.parseLong(split[0]));
//                        return props1;
//                    })
//                    .collect(Collectors.toList());
            responseVo.setPropsParamList(propsList);
        }

        //品牌列表  聚合操作
        List<SearchResponseVo.Trademark> trademarkList = new ArrayList<>();
        ParsedLongTerms tmIdAgg = result.getAggregations().get("tmIdAgg");
        tmIdAgg.getBuckets().forEach((item) -> {
            SearchResponseVo.Trademark trademark = new SearchResponseVo.Trademark();
            //品牌id
            long tmId = item.getKeyAsNumber().longValue();
            //品牌name
            ParsedStringTerms tmNameAgg = item.getAggregations().get("tmNameAgg");
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
            //品牌url
            ParsedStringTerms tmLogoUrl = item.getAggregations().get("tmLogoAgg");
            String logoUrl = tmLogoUrl.getBuckets().get(0).getKeyAsString();
            trademark.setTmId(tmId);
            trademark.setTmLogoUrl(logoUrl);
            trademark.setTmName(tmName);
            trademarkList.add(trademark);
        });

        responseVo.setTrademarkList(trademarkList);

        //属性列表  聚合操作
        ParsedNested attrAgg = result.getAggregations().get("attrAgg");
        //属性id聚合
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
        List<SearchResponseVo.Attr> attrList = attrIdAgg
                .getBuckets()
                .stream()
                .map((bucket) -> {
                    SearchResponseVo.Attr attr = new SearchResponseVo.Attr();
                    //属性名
                    ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attrNameAgg");
                    String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
                    attr.setAttrName(attrName);
                    //属性值列表
                    ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attrValueAgg");
                    List<String> valueList = attrValueAgg.getBuckets()
                            .stream()
                            .map((item) -> item.getKeyAsString())
                            .collect(Collectors.toList());
                    attr.setAttrValueList(valueList);
                    //属性id
                    attr.setAttrId(bucket.getKeyAsNumber().longValue());
                    return attr;
                })
                .collect(Collectors.toList());
        responseVo.setAttrsList(attrList);

        //url参数
        String urlParam = buildUrlParam(paramVo);
        responseVo.setUrlParam(urlParam);

        //排序信息
        OrderMap orderMap = new OrderMap();
        if (!StringUtils.isEmpty(paramVo.getOrder())){  //判断前端是否传递排序规则
            String[] split = paramVo.getOrder().split(":");
            orderMap.setSort(split[0]);
            orderMap.setType(split[1]);
            responseVo.setOrderMap(orderMap);
        }

        //商品列表
        //获取检索命中的所有记录
        List<Goods> goodsList = new ArrayList<>();
        List<SearchHit<Goods>> searchHits = result.getSearchHits();
        for (SearchHit<Goods> searchHit : searchHits) {
            Goods goods = searchHit.getContent();
            //判断是否是keyword检索
            if (!StringUtils.isEmpty(paramVo.getKeyword())){
                //模糊检索带高亮显示
                String newTitle = searchHit.getHighlightField("title").get(0);
                goods.setTitle(newTitle);
            }
            goodsList.add(goods);
            responseVo.setGoodsList(goodsList);
        }

        //页码
        responseVo.setPageNo(paramVo.getPageNo());

        //总页码  总记录数%每页记录数 == 0 ？ 总记录数%每页记录数 ： 总记录数%每页记录数 + 1
        long total = result.getTotalHits();
        responseVo.setTotalPages(total % pageSize == 0 ? total / pageSize : total / pageSize + 1 );

        return responseVo;
    }

    /**
     * 根据前端带来的参数  构建原来的url
     * @param paramVo
     * @return
     */
    private String buildUrlParam(SearchParamVo paramVo) {
        StringBuilder builder = new StringBuilder("list.html?");

        //分类参数
        if (paramVo.getCategory1Id() != null) {
            builder.append("&category1Id=" + paramVo.getCategory1Id());
        }
        if (paramVo.getCategory2Id() != null) {
            builder.append("&category2Id=" + paramVo.getCategory2Id());
        }
        if (paramVo.getCategory3Id() != null) {
            builder.append("&category3Id=" + paramVo.getCategory3Id());
        }

        //keyword
        if (!StringUtils.isEmpty(paramVo.getKeyword())) {
            builder.append("&keyword=" + paramVo.getKeyword());
        }

        //品牌
        if (!StringUtils.isEmpty(paramVo.getTrademark())) {
            builder.append("&trademark=" + paramVo.getTrademark());
        }

        //属性
        if (paramVo.getProps() != null && paramVo.getProps().length > 0){
            Arrays.stream(paramVo.getProps()).forEach((item) -> {
                builder.append("&props=" + item);
            });
        }

        return builder.toString();
    }

    /**
     * 构建检索条件
     * @param paramVo
     * @return
     */
    private Query buildQuery(SearchParamVo paramVo) {

        //=======================查询开始=========================
        //构建bool查询
        BoolQueryBuilder bool = QueryBuilders.boolQuery();

        //构建bool中的查询条件
        //1.1一级分类
        if (paramVo.getCategory1Id() != null){
            TermQueryBuilder term = QueryBuilders.termQuery("category1Id", paramVo.getCategory1Id());
            bool.must(term);
        }

        //1.2二级分类
        if (paramVo.getCategory2Id() != null){
            TermQueryBuilder term = QueryBuilders.termQuery("category2Id", paramVo.getCategory2Id());
            bool.must(term);
        }

        //1.3三级分类
        if (paramVo.getCategory3Id() != null){
            TermQueryBuilder term = QueryBuilders.termQuery("category3Id", paramVo.getCategory3Id());
            bool.must(term);
        }

        //1.4关键字查询
        if (!StringUtils.isEmpty(paramVo.getKeyword())) {
            MatchQueryBuilder match = QueryBuilders.matchQuery("title", paramVo.getKeyword());
            bool.must(match);
        }

        //1.5品牌查询
        if (!StringUtils.isEmpty(paramVo.getTrademark())) {
            String[] strings = paramVo.getTrademark().split(":");
            TermQueryBuilder term = QueryBuilders.termQuery("tmId", strings[0]);
            bool.must(term);
        }

        //1.6属性查询
        String[] props = paramVo.getProps();
        //1.6.1判空
        if (props != null && props.length > 0){
            Arrays.stream(props).forEach((item) -> {
                String[] split = item.split(":");
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                //按照属性id
                TermQueryBuilder termAttrId = QueryBuilders.termQuery("attrs.attrId", split[0]);
                //按照属性值
                TermQueryBuilder termValue = QueryBuilders.termQuery("attrs.attrValue", split[1]);
                boolQuery.must(termAttrId);
                boolQuery.must(termValue);

                //开启嵌入式查询
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", boolQuery, ScoreMode.None);
                bool.must(nestedQuery);
            });
        }

        //1.创建一个原生Query
        NativeSearchQuery query = new NativeSearchQuery(bool);

        //=======================查询结束=========================

        //=======================排序开始=========================
        //排序 参数不为空
        //1:综合排序：热度分
        //2：价格排序
        //asc：升序
        //desc：降序
        if (!StringUtils.isEmpty(paramVo.getOrder())){
            String[] strings = paramVo.getOrder().split(":");
            Sort.Direction direction = "asc".equals(strings[1]) ? Sort.Direction.ASC : Sort.Direction.DESC;
            Sort sort = null;
            switch (strings[0]){
                case "1":
                    sort = Sort.by(direction,"hotScore");
                    break;
                case "2":
                    sort = Sort.by(direction,"price");
                    break;
                default:
                    sort = Sort.by(Sort.Direction.DESC,"hotScore");
            }
            query.addSort(sort);
        }

        //=======================排序结束=========================

        //=======================分页开始=========================
        //分页:注意 页码从0开始
        Integer pageNo = paramVo.getPageNo();
        Pageable pageable = PageRequest.of(pageNo - 1,10);
        query.setPageable(pageable);
        //=======================分页结束=========================

        //=======================高亮=========================
        if (!StringUtils.isEmpty(paramVo.getKeyword())){
            //构建高亮
            HighlightBuilder builder = new HighlightBuilder()
                    .field("title")
                    .preTags("<span style='color:red'>")
                    .postTags("</span>");
            HighlightQuery highlightQuery = new HighlightQuery(builder);
            query.setHighlightQuery(highlightQuery);
        }
        //=======================聚合分析开始=========================
        //==================品牌的聚合=======================
        //按照品牌id聚合
        TermsAggregationBuilder tmIdAgg = AggregationBuilders
                .terms("tmIdAgg")
                .field("tmId")
                .size(200);
        //品牌name子聚合
        TermsAggregationBuilder tmNameAgg = AggregationBuilders
                .terms("tmNameAgg")
                .field("tmName")
                .size(1);
        tmIdAgg.subAggregation(tmNameAgg);
        //品牌logo子聚合
        TermsAggregationBuilder tmLogoAgg = AggregationBuilders
                .terms("tmLogoAgg")
                .field("tmLogoUrl")
                .size(1);
        tmIdAgg.subAggregation(tmLogoAgg);

        query.addAggregation(tmIdAgg);

        //==================属性的聚合=======================
        NestedAggregationBuilder attrAgg = AggregationBuilders
                .nested("attrAgg", "attrs");
        //属性id聚合
        TermsAggregationBuilder attrIdAgg = AggregationBuilders
                .terms("attrIdAgg")
                .field("attrs.attrId")
                .size(200);
        attrAgg.subAggregation(attrIdAgg);
        query.addAggregation(attrAgg);

        //属性名聚合
        TermsAggregationBuilder attrNameAgg = AggregationBuilders
                .terms("attrNameAgg")
                .field("attrs.attrName")
                .size(200);
        attrIdAgg.subAggregation(attrNameAgg);

        //属性值聚合
        TermsAggregationBuilder attrValueAgg = AggregationBuilders
                .terms("attrValueAgg")
                .field("attrs.attrValue")
                .size(200);
        attrIdAgg.subAggregation(attrValueAgg);

        //=======================聚合分析结束=========================
        return query;
    }

    /**
     * 商品上架  将商品保存到es
     * @param goods
     */
    @Override
    public void up(Goods goods) {
        goodsRepository.save(goods);
    }

    /**
     * 商品下架
     * @param skuId
     */
    @Override
    public void down(Long skuId) {
        goodsRepository.deleteById(skuId);
    }

    /**
     * 增加热度分
     * @param skuId
     * @param score
     */
    @Override
    public void updateHotScore(Long skuId, Long score) {
        //全量更新影响网络传输效率
//        Goods goods = goodsRepository.findById(skuId).get();
//        goods.setHotScore(score);
//        goodsRepository.save(goods);

        //增量更新
        Document document = Document.create();
        document.put("hotScore",score);
        //增量更新
        UpdateQuery updateQuery = UpdateQuery.builder("" + skuId)
                .withDocAsUpsert(true)
                .withDocument(document)
                .build();
        elasticsearchRestTemplate.update(updateQuery,IndexCoordinates.of("goods"));
    }
}
