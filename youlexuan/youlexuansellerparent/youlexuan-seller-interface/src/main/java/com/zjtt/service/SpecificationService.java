package com.zjtt.service;
import java.util.List;
import java.util.Map;

import com.zjtt.pojo.TbSpecification;

import com.zjtt.util.PageResult;
import com.zjtt.util.Specfication;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SpecificationService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSpecification> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(Specfication specification);
	
	
	/**
	 * 修改
	 */
	public void update(Specfication specification);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public Specfication findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize);

	List<Map> selectSpecfications();
}
