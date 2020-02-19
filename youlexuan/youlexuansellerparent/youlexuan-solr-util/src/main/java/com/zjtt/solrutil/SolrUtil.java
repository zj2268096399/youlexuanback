package com.zjtt.solrutil;

import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.zjtt.mapper.TbItemMapper;
import com.zjtt.pojo.TbItem;
import com.zjtt.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    public void importItemData(){
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> itemList = tbItemMapper.selectByExample(example);

        for(TbItem item:itemList){
            System.out.println(item.getTitle());
            //读取规格数据，字符串，转换成json对象
            Map<String,String> specMap = JSON.parseObject(item.getSpec(),Map.class);
            //创建一个新map集合存储拼音
            Map<String,String> mapPinyin=new HashMap<>();
            //遍历map，替换key从汉字变为拼音.Solr7不支持
            for(String key :specMap.keySet()){
                mapPinyin.put(Pinyin.toPinyin(key,"").toLowerCase(),specMap.get(key));
            }
            item.setSpecMap(mapPinyin);
        }
//保存集合数据到solr
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();

    }

    public static void main(String[] args) {
        ApplicationContext app = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");

        SolrUtil solrUtil = (SolrUtil) app.getBean("solrUtil");
        solrUtil.importItemData();
    }
}
