 //商品类目控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){

		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			$scope.entity.parentId = $scope.parentId;
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){

					//重新查询 
                    //$scope.reloadList();重新加载
                    // window.location.reload();//刷新
					$scope.itemCatListByPid(0); //重新加载页面
				}else{
					alert(response.msg);
				}
			}		
		);
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

    //搜索
    $scope.search=function(page,rows){
        itemCatService.search(page,rows,$scope.searchEntity).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }

    $scope.parentId = "0";
    $scope.itemCatListByPid=function (pid) {
    	$scope.parentId = pid;
		itemCatService.itemCatListByPid(pid).success(
			function (response) {
                $scope.list=response;
            })
    }

    //设置级别
	$scope.grade = 1;
    $scope.setGrade=function (grade) {
		$scope.grade = grade;
    }

    //查询级别

    $scope.getItemCatList = function (p_entity) {
        if($scope.grade == 1){
            $scope.entity_2 = null;  // 第二级置空
            $scope.entity_3 = null;  // 第三级置空

        }
        if($scope.grade == 2){
            // 第二级显示
            $scope.entity_2 = p_entity;
            // 第三级置空
            $scope.entity_3 = null;
        }
        if($scope.grade == 3){
            // 第三级显示
            $scope.entity_3 = p_entity;

        }
        $scope.itemCatListByPid(p_entity.id);
    }

    //获取类型模板
	$scope.typeTemplateList = {data:[]};
    $scope.selectTypeTemplate = function () {
		typeTemplateService.selectTypeTemplate().success(
			function (response) {
                $scope.typeTemplateList={data:response};
            }
		)
    }

    $scope.deleteItemCat = function () {
    	itemCatService.deleteItemCat( $scope.selectIds).success(
    		function (response) {
				if(response.success){
                    window.location.reload();
				}else {
					alert(response.msg);
				}
            }
		)

    }


    
});	