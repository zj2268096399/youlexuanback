 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller   ,typeTemplateService,brandService,specificationService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity=response;
				$scope.entity.specIds=JSON.parse( $scope.entity.specIds);
                $scope.entity.brandIds=JSON.parse($scope.entity.brandIds);
                $scope.entity.customAttributeItems=JSON.parse($scope.entity.customAttributeItems);

			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象
        // 因为在现在的$scope.entity是个json对象，而数据库需要的是json字符串，所以需要进行转换
        $scope.entity.specIds = JSON.stringify($scope.entity.specIds);
        $scope.entity.brandIds = JSON.stringify($scope.entity.brandIds);
        $scope.entity.customAttributeItems = JSON.stringify($scope.entity.customAttributeItems);
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){
		if(confirm("是否确认删除？")){
            //获取选中的复选框
            typeTemplateService.dele( $scope.selectIds ).success(
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
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	var brandList ={data:[]};
	$scope.selectBrandOption = function(){
		brandService.selectBrandOption().success(
			function (response) {
                $scope.brandList = {data:response};
        })
	}
    $scope.specficationList = {data:[]};
    $scope.selectSpecfications = function () {
        specificationService.selectSpecfications().success(
            function (response) {
                $scope.specficationList = {data:response};
            }
        )}


        $scope.addTable = function () {
		$scope.entity.customAttributeItems.push({});
    }

    $scope.removeTable = function (index) {
        $scope.entity.customAttributeItems.splice(index,1);
    }
});	