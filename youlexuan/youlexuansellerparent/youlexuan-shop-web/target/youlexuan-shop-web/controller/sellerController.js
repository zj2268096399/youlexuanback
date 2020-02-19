 //控制层 
app.controller('sellerController' ,function($scope,$controller   ,sellerService,loginService){
	
	$controller('baseController',{$scope:$scope});//继承
    //读取列表数据绑定到表单中
    $scope.getName=function(){
        loginService.getName().success(
            function(response){
                $scope.loginname=response.name;
            }
        );
    }



    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		sellerService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		sellerService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){

        loginService.getName().success(
        	function (response) {
                sellerService.findOne(response.name).success(
                    function(responses){
                        $scope.entity= responses;
                    }
                );
            }
		)


		/**/
	}
	
	//保存 
	$scope.save=function(){
		console.log( $scope.entity )
		sellerService.add($scope.entity).success(
			function(response){
				if(response.success){
					location.href="/shoplogin.html";
				}else{
					alert(response.msg);
				}
			}
		);
	}

	//修改
	$scope.update = function(){
    	console.log($scope.entity);
        sellerService.update( $scope.entity ).success(
    		function (response) {
                if(response.success){
                    window.location.reload();
                }else{
                    alert(response.msg);
                }
            }
		)
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框
		if(confirm("是否确认删除？")){
            sellerService.dele( $scope.selectIds ).success(
                function(response){
                    if(response.success){
                        $scope.reloadList();//刷新列表
                        $scope.selectIds=[];
                    }
                }
            );
		}

	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		sellerService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//校验原始密码
	$scope.checkOldPass =function (id) {
		$scope.entity.sellerId = id;
		sellerService.checkOldPass($scope.entity).success(
			function (response) {
				if(!response.success){
                   alert(response.msg);
                    $scope.entity.password="";
				}
        })
    }
    
    $scope.checkRePassword=function () {
		if($scope.rePassword != $scope.newPassword ){
			alert("两次密码不一致");
            $scope.rePassword="";
            $scope.newPassword="";
		}
    }

    //修改密码
	$scope.updatePassword=function () {
		sellerService.updatePassword($scope.entity,$scope.newPassword).success(
			function (response) {
                if(response.success){
                    window.parent.location.href="/logout";
                }else{
                    alert(response.msg);
                }
            }
		)
    }
});	