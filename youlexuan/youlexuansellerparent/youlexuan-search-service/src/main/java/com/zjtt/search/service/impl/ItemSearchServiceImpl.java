package com.zjtt.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.zjtt.pojo.TbItem;

import com.zjtt.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        //关键字的空格处理
        String keyWords = (String) searchMap.get("keywords");
        searchMap.put("keywords",keyWords.replace(" ",""));
        //这是一个map集合，用来装前台的搜索框的关键字 keywords
        Map<String ,Object> map = new HashMap<>();
        //获得高亮查询的结果
        map.putAll(searchList(searchMap));
        //获得分组查询的结果
        List categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);
        if(categoryList.size()>0){
         map.putAll( searchBrandAndSpecList((String) categoryList.get(0)));
        }

        //3、根据商品类目查询对应的品牌、规格
        //读取分类名称
        String categoryName=(String) searchMap.get("category");
        if(!"".equals(categoryName)) {
            //按照分类名称重新读取对应品牌、规格
            map.putAll(searchBrandAndSpecList(categoryName));
        }else {
            if (categoryList.size() > 0) {
                Map mapBrandAndSpec = searchBrandAndSpecList((String) categoryList.get(0));
                map.putAll(mapBrandAndSpec);
            }
        }
        return map;
    }



    private  Map  searchList(Map searchMap){

        Map map= new HashMap();


////        这个用简单的查询
//        Query query = new SimpleQuery();
//        // 添加查询的条件，
//        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
//        query.addCriteria(criteria);
//        //查询的语句，查询
//        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
//        map.put("rows", page.getContent());

//        简单的高亮部分的查询替代SimpleQuery查询
        HighlightQuery hlQuery = new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions();
        //设置高亮需要处理的字段
        highlightOptions.addField("item_title");
        //设置前缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //设置后缀
        highlightOptions.setSimplePostfix("</em>");

        //将高亮加入查询中
        hlQuery.setHighlightOptions(highlightOptions);

        // 1.1添加查询的条件，按照关键字查询
        Criteria criterias = new Criteria("item_keywords").is(searchMap.get("keywords"));
        hlQuery.addCriteria(criterias);

        //1.2 按照category查询,设置过滤项
        if(!"".equals(searchMap.get("category"))){

            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFacetQuery(filterCriteria);
            hlQuery.addFilterQuery(filterQuery);
        }
        //1,3按照brand查询,设置过滤项
        if(!"".equals(searchMap.get("brand"))){

            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFacetQuery(filterCriteria);
            hlQuery.addFilterQuery(filterQuery);
        }
        //1,4按照brand查询,设置过滤项

        if(searchMap.get("spec") != null){
           Map<String ,String> specMap=(Map<String, String>) searchMap.get("spec");
            for (String key:specMap.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_"+ Pinyin.toPinyin(key,"").toLowerCase()).is(specMap.get(key));
                FilterQuery filterQuery = new SimpleFacetQuery(filterCriteria);
                hlQuery.addFilterQuery(filterQuery);
            }
        }
        //1,5按照price查询,设置过滤项
        if(!"".equals(searchMap.get("price"))){
            String[] prices = ((String) searchMap.get("price")).split("-");
            if(!"0".equals(prices[0])){//如果价格的前一个数大于0，就加过滤项 》price【0】 500-1000
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);
                FilterQuery filterQuery = new SimpleFacetQuery(filterCriteria);
                hlQuery.addFilterQuery(filterQuery);
            }
            if(!"*".equals(prices[1])){
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(prices[1]);
                FilterQuery filterQuery = new SimpleFacetQuery(filterCriteria);
                hlQuery.addFilterQuery(filterQuery);
            }
        }

//        1.6 分页查询
        Integer pageNo= (Integer) searchMap.get("pageNo");//提取页码
        if(pageNo==null){
            pageNo=1;//默认第一页
        }
        Integer pageSize=(Integer) searchMap.get("pageSize");//每页记录数
        if(pageSize==null){
            pageSize=20;//默认20
        }
        hlQuery.setOffset((pageNo-1)*pageSize);//查询的初始下标为， 第一页 0 ，第二页 10 ，第三页 20
        hlQuery.setRows(pageSize);//分页，每页的数据量

        //1.7 按照字段排序，升序或者降序
        String sortValue= (String) searchMap.get("sortValue");//ASC  DESC
        String sortField= (String) searchMap.get("sortFiled");//排序字段
        if(sortValue!=null && !sortValue.equals("")){
            if(sortValue.equals("ASC")){
                Sort sort=new Sort(Sort.Direction.ASC, "item_"+sortField);
                hlQuery.addSort(sort);
            }
            if(sortValue.equals("DESC")){
                Sort sort=new Sort(Sort.Direction.DESC, "item_"+sortField);
                hlQuery.addSort(sort);
            }
        }

        //上solr数据库中进行查询，发出带高亮数据查询请求
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(hlQuery,TbItem.class);
        //获取查询的结果，并且放在list集合中
        List<TbItem> list = highlightPage.getContent();
        //便利高亮查询获取的集合，这个时候item_title,还没有加上高亮
        for (TbItem item : list){
            //item 中的将item_title 变成高亮的item_title
            List<HighlightEntry.Highlight> highlights = highlightPage.getHighlights(item);
            if(highlights!=null&&highlights.size()>0) {
                //获取第一个字段高亮对象
                List<String> highlightSnipplets = highlights.get(0).getSnipplets();
                //使用高亮结果替换商品标题
                item.setTitle(highlightSnipplets.get(0));

            }
        }
        map.put("rows", highlightPage.getContent());
        map.put("totalPages",highlightPage.getTotalPages());//总页数
        map.put("total",highlightPage.getTotalElements());//总数据量
        return map;
    }

    private List searchCategoryList(Map searchMap){
        List<String> list = new ArrayList();
        Query query = new SimpleQuery();

        //添加查询的条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

//设置分组选项  注意商品分类不能设置分词，要不然分组结果会失败
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        //得到分组也
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query,TbItem.class);

        //得到分组的集合
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();

        for(GroupEntry<TbItem> entry:content){
            list.add(entry.getGroupValue());//将分组结果的名称封装到返回值中
        }

        return list;
    }

    private Map searchBrandAndSpecList(String category){
        Map map = new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if(typeId != null){
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList",specList);
        }

        return map;
    }

    @Override
    public void importList(List<TbItem> list) {
        for(TbItem item:list){
            System.out.println(item.getTitle());
            Map<String,String> specMap = JSON.parseObject(item.getSpec(),Map.class);//从数据库中提取规格json字符串转换为map
            Map map = new HashMap();
            for(String key : specMap.keySet()) {
                map.put("item_spec_"+Pinyin.toPinyin(key, "").toLowerCase(), specMap.get(key));
            }

            item.setSpecMap(map);	//给带动态域注解的字段赋值

        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsIdList) {
        System.out.println("删除商品ID"+goodsIdList);
        Query query=new SimpleQuery();
        Criteria criteria=new Criteria("item_goods_id").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
