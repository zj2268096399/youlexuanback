//服务层
app.service('fileUploadService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.fileUpload=function(){
		var formData = new FormData();
		formData.append("file",file.files[0]);
		return $http({
			method:"POST",
			url:"../file/upload.do",
			data:formData,
			headers:{'Content-type':undefined},
			transformRequest:angular.identity
		});
		// headers:{'Content-type':undefined}    等价于form表单的请求格式为  multipart-form/data
	}
});