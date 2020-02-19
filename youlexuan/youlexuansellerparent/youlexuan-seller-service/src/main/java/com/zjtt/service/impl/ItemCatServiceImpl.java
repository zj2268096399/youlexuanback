package com.zjtt.service.impl;
import java.util.List;
import java.util.Map;

import com.zjtt.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zjtt.mapper.TbItemCatMapper;
import com.zjtt.pojo.TbItemCat;
import com.zjtt.pojo.TbItemCatExample;
import com.zjtt.pojo.TbItemCatExample.Criteria;

import com.zjtt.util.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


/**
 * 商品类目服务实现层
 * @author Administrator
 *
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbItemCat> findAll() {
		return itemCatMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbItemCat> page=   (Page<TbItemCat>) itemCatMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbItemCat itemCat) {
		itemCatMapper.insert(itemCat);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat itemCat){
		itemCatMapper.updateByPrimaryKey(itemCat);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbItemCat findOne(Long id){
		return itemCatMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			itemCatMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbItemCatExample example=new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		
		if(itemCat!=null){			
						if(itemCat.getName()!=null && itemCat.getName().length()>0){
				criteria.andNameLike("%"+itemCat.getName()+"%");
			}	
		}
		
		Page<TbItemCat> page= (Page<TbItemCat>)itemCatMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<TbItemCat> itemCatListByPid(long parentId) {
		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		//运行商品分类列表的时候，把全部的分类数据存储到redis缓存
		List<TbItemCat> all = findAll();
		//循环遍历全部分类数据

		if(all!=null&&all.size()>0) {
			for (TbItemCat itemCat : all) {
				if(itemCat!=null&&itemCat.getName()!=null&&itemCat.getTypeId()>0) {
					redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
				}
			}
			System.out.println("更新全部分类数据到redis缓存");
		}
		return itemCatMapper.selectByExample(example);
	}

	@Override
	public void deleteItemCat(long[] ids) {
		for (long id: ids ) {
			itemCatMapper.deleteItemCat(id);
		}
	}

	@Override
	public List<Map> getCategory(long parentId) {
		return itemCatMapper.getCategory(parentId);
	}
}
