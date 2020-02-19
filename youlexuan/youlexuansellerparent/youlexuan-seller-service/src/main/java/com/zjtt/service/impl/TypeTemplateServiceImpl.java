package com.zjtt.service.impl;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.zjtt.mapper.TbSpecificationOptionMapper;
import com.zjtt.pojo.TbSpecificationOption;
import com.zjtt.pojo.TbSpecificationOptionExample;
import com.zjtt.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zjtt.mapper.TbTypeTemplateMapper;
import com.zjtt.pojo.TbTypeTemplate;
import com.zjtt.pojo.TbTypeTemplateExample;
import com.zjtt.pojo.TbTypeTemplateExample.Criteria;

import com.zjtt.util.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;

	@Autowired
	private TbSpecificationOptionMapper tbSpecificationOptionMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	private void saveToRedis(){
		//获取模板数据
		List<TbTypeTemplate> typeTemplateList = findAll();
		//循环模板
		for(TbTypeTemplate typeTemplate :typeTemplateList){
			//存储品牌列表
			List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
			redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(), brandList);
			//存储规格列表
			List<Map> specList = selectSpecList(typeTemplate.getId());//根据模板ID查询规格列表、包括规格选项
			redisTemplate.boundHashOps("specList").put(typeTemplate.getId(), specList);
		}
	}
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}		
	}

	/**
	 * 模糊查询
	 * @param typeTemplate
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
		@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();
		
		if(typeTemplate!=null){			
						if(typeTemplate.getName()!=null && typeTemplate.getName().length()>0){
				criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}			if(typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0){
				criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}			if(typeTemplate.getBrandIds()!=null && typeTemplate.getBrandIds().length()>0){
				criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}			if(typeTemplate.getCustomAttributeItems()!=null && typeTemplate.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}	
		}
		
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)typeTemplateMapper.selectByExample(example);
			saveToRedis();//存入数据到缓存
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 获取所有的类别
	 * @return
	 */
	@Override
	public List<Map> selectTypeTemplate() {
		return typeTemplateMapper.selectTypeTemplate();
	}

	/**
	 * 获取商品上传的规格
	 * @param id
	 * @return
	 */
	@Override
	public List<Map> selectSpecList(long id) {
		TbTypeTemplate template = typeTemplateMapper.selectByPrimaryKey(id);
		// 对应数据库字段后去的specIds是一个json格式的字符串，要把json字符转成json对象，方便我们去speId中的值
		String specIds = template.getSpecIds();
		List<Map> maps = JSON.parseArray(specIds, Map.class);
		for(Map map : maps){
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			Long specId = new Long((Integer) map.get("id"));
			criteria.andSpecIdEqualTo(specId);
			List<TbSpecificationOption> specificationOptions = tbSpecificationOptionMapper.selectByExample(example);
			map.put("option",specificationOptions);
		}
		return maps;
	}
}
