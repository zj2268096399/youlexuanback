//服务层
app.service('categoryService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.getCategory=function(id){
		return $http.get('../cate/getCategory.do?parentId='+id);
	}

});