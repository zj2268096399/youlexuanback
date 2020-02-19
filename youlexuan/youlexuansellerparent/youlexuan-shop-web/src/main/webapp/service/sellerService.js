//服务层
app.service('sellerService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../seller/findAll.do');		
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../seller/findPage.do?page='+page+'&rows='+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../seller/findOne.do?sellerId='+id);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../seller/add.do',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../seller/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../seller/delete.do?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../seller/search.do?page='+page+"&rows="+rows, searchEntity);
	}

	//查询所有的品牌
	this.selectsellerOption = function () {
		return $http.get('../seller/selectsellerOption.do');
    }

    //校验原始密码是否正确
	this.checkOldPass = function (entity) {
		return $http.post('../seller/checkOldPass.do',entity);
    }

    //修改用户密码
	this.updatePassword=function (entity,newPass) {
		return $http.post('../seller/updatePassword.do?newPass='+newPass,entity);
    }
});