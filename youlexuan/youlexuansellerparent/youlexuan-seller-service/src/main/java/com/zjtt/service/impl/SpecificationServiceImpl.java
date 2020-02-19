package com.zjtt.service.impl;
import java.util.List;
import java.util.Map;

import com.zjtt.mapper.TbSpecificationOptionMapper;
import com.zjtt.pojo.TbSpecificationOption;
import com.zjtt.pojo.TbSpecificationOptionExample;
import com.zjtt.service.SpecificationService;
import com.zjtt.util.Specfication;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zjtt.mapper.TbSpecificationMapper;
import com.zjtt.pojo.TbSpecification;
import com.zjtt.pojo.TbSpecificationExample;
import com.zjtt.pojo.TbSpecificationExample.Criteria;

import com.zjtt.util.PageResult;
import org.springframework.stereotype.Service;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;

	@Autowired
	private TbSpecificationOptionMapper tbSpecificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}
	/**
	 * 增加
	 */
	@Override
	public void add(Specfication specfication) {
		specificationMapper.insert(specfication.getTbSpecification());

		for(TbSpecificationOption specificationOption : specfication.getSpectionOptionList()){
			specificationOption.setSpecId(specfication.getTbSpecification().getId());
			tbSpecificationOptionMapper.insert(specificationOption);
		}
	}




	
	/**
	 * 修改
	 */
	@Override
	public void update(Specfication specification){
		//修改specification表
		specificationMapper.updateByPrimaryKey(specification.getTbSpecification());
		//先删除选项表中的
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(specification.getTbSpecification().getId());
		tbSpecificationOptionMapper.deleteByExample(example);
		//再进行添加操作
		for(TbSpecificationOption specificationOption : specification.getSpectionOptionList()){
			specificationOption.setSpecId(specification.getTbSpecification().getId());
			tbSpecificationOptionMapper.insert(specificationOption);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specfication findOne(Long id){
		Specfication specfication = new Specfication();
		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(id);
		List<TbSpecificationOption> tbSpecificationOptions = tbSpecificationOptionMapper.selectByExample(example);
		specfication.setTbSpecification(tbSpecification);
		specfication.setSpectionOptionList(tbSpecificationOptions);


		return  specfication;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			specificationMapper.deleteByPrimaryKey(id);
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(id);
			tbSpecificationOptionMapper.deleteByExample(example);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> selectSpecfications() {
		return specificationMapper.selectSpecfications();
	}
}
