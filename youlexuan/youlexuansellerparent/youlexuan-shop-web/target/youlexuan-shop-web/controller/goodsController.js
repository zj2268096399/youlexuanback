 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location  ,goodsService,itemCatService,typeTemplateService,fileUploadService){

	$controller('baseController',{$scope:$scope});//继承

    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}
		);
	}

	//分页
	$scope.findPage=function(page,rows){
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}
		);
	}

	//查询实体
	$scope.findOne=function(){
	    var id = $location.search()['id'];
		goodsService.findOne(id).success(
			function(response){
			    console.log(response);
				$scope.entity= response;
				//这个是编辑框的回显
				editor.html($scope.entity.tbGoodsDesc.introduction);

                //商品的图片
                $scope.entity.tbGoodsDesc.itemImages=JSON.parse(response.tbGoodsDesc.itemImages);
                //商品的扩展属性
                $scope.entity.tbGoodsDesc.customAttributeItems=JSON.parse(response.tbGoodsDesc.customAttributeItems) ;

                //商品的选项规格
				$scope.entity.tbGoodsDesc.specificationItems=JSON.parse(response.tbGoodsDesc.specificationItems);

				//商品的规格列表，是一个List集合需要遍历
                for(var i = 0; i < $scope.entity.itemList.length; i++){
                    $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
                }
			}
		);
	}

	//判断规格是否选中
    $scope.isEnableSpecChecked = function(specName,optionName){
        var items = $scope.entity.tbGoodsDesc.specificationItems;
        var obj = $scope.findValueByKey(items,'attributeName',specName);
        if(obj == null){
            return false;
        }else{
            if(obj.attributeValue.indexOf(optionName) >= 0){
                return true;
            }else{
                return false;
            }
        }
    }

	//保存
	$scope.save=function(){
        var serviceObject;
        // 获取富文本编辑器的内容（前提是要对富文本编辑器初始化）
        $scope.entity.tbGoodsDesc.introduction=editor.html();
        // 应为在controller.js中把数据库获取的json字符串转成了对象，当需要添加数据到数据库
        //  时，同时需要再把转过的对象在还原为json字符串
        $scope.entity.tbGoodsDesc.customAttributeItems = JSON.stringify($scope.entity.tbGoodsDesc.customAttributeItems);
        $scope.entity.tbGoodsDesc.itemImages = JSON.stringify($scope.entity.tbGoodsDesc.itemImages);
        $scope.entity.tbGoodsDesc.specificationItems = JSON.stringify($scope.entity.tbGoodsDesc.specificationItems);
        // 转化spec
        if($scope.entity.tbGoods.isEnableSpec == 1){
            for(var i = 0; i < $scope.entity.itemList.length;i++){
                $scope.entity.itemList[i].spec  = JSON.stringify($scope.entity.itemList[i].spec);
            }
        }

	    if($scope.entity.tbGoods.id !=null){
           serviceObject=goodsService.update( $scope.entity ); //修改
        }else{
            serviceObject=goodsService.add( $scope.entity  );//增加
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    //重新查询
                  window.location.href="goods.html";
                }else{
                    alert(response.msg);
                }
            }
        )

	}


	//批量删除
	$scope.dele=function(){
		if(confirm("是否确认删除!")){
            //获取选中的复选框
            goodsService.dele( $scope.selectIds ).success(
                function(response){
                    if(response.success){
                        $scope.reloadList();//刷新列表
                    }else {
                        alert(response.msg);
                    }
                }
            );
		}

	}

	$scope.searchEntity={};//定义搜索对象

	//搜索
	$scope.search=function(page,rows){
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}
		);
	}

	//商品管理的一级二级三级目录的对应的name的回显
	$scope.itemCatList=[];
	$scope.findItemCat=function(){
	    itemCatService.findAll().success(
	        function (response) {
	           for(var i = 0 ; i < response.length ; i++){

                     $scope.itemCatList[response[i].id]=response[i].name;
                }
            }
        )
    }



    //获取分类，当parentId = 0 时，为一级，
    $scope.itemCatOneList=function () {

        itemCatService.itemCatListByPid(0).success(
            function (response) {
                $scope.itemCatOneList=response;
            })
    }

    //获取二级分类
    $scope.$watch('entity.tbGoods.category1Id',function (oldValue,newValue) {
        $scope.itemCatThreeList=[];
    	if(oldValue){
            itemCatService.itemCatListByPid(oldValue).success(
                function (response) {
                    $scope.itemCatTwoList=response;

                })
		}
    })

	//获取三级分类
    $scope.$watch('entity.tbGoods.category2Id',function (oldValue,newValue) {

        if(oldValue){
            itemCatService.itemCatListByPid(oldValue).success(
                function (response) {
                    $scope.itemCatThreeList=response;

                })
        }
    })

	//通过选定的三级分类来查询itemCat标的模板分类TypeId,
    $scope.$watch('entity.tbGoods.category3Id',function (oldValue,newValue) {
        //将规格取消
        $scope.entity.itemList=[];
        //取消选中的框
        $(".spec").prop("checked",false);
        //将规格列表取消
        $scope.entity.tbGoodsDesc.specificationItems=[];
        if(oldValue){
            itemCatService.findOne(oldValue).success(
                function (response) {
                    $scope.entity.tbGoods.typeTemplateId=response.typeId;
                })
        }
    })

	//知道木块的id后就可以通过模块id查询tb_type_template表中的brand_id 中有模块中的品牌
	//和扩展属性 也就是spec_id
    $scope.$watch('entity.tbGoods.typeTemplateId',function (oldValue,newValue) {
        if(oldValue){
            // 在查询模板的时候已经是最后一级，没有父id了，直接通过id查询结果
            typeTemplateService.findOne(oldValue).success(
                function (response) {
                    // 模板id查询品牌
                    $scope.brandList = JSON.parse(response.brandIds)

console.log("需要改变")
                    if($location.search()['id'] == null){
                        $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
                    }
                    // 根据模板id查询扩展属性


                }
            );
            // 查询规格列表
            typeTemplateService.selectSpecList(oldValue).success(
                function (response) {
                    $scope.specList = response;
                }
            )
        }
    });


    $scope.entity = {tbGoodsDesc:{itemImages:[],specificationItems:[]}}
    //通过选中的规格更新规格列表
    $scope.updateCheckedValue = function($event,name,value){
        var object = $scope.findValueByKey($scope.entity.tbGoodsDesc.specificationItems,'attributeName',name);
        //看当前的obj是否存在，存在就继续进行判断，不存在则为specificationItems初始化
        if(object != null){
            //如果选中了规格选项，就添加到specificationItems列表中
            if($event.target.checked){
                object.attributeValue.push(value);
            }else{ //取消选中则从列表中删除
                object.attributeValue.splice(object.attributeValue.indexOf(value),1);
                // 取消勾选，如果全部取消了，应在在大的集合中移除小的集合
                if(object.attributeValue.length ==0){
                    $scope.entity.tbGoodsDesc.specificationItems.splice($scope.entity.tbGoodsDesc.specificationItems.indexOf(object,1));
                }
            }
        }else{
            //对象不存在，则进行初始化
            $scope.entity.tbGoodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
        }
    }

    //创建一个列表来装规格
    $scope.createSpecList=function(){
        $scope.entity.itemList = [{spec:{}}];
        var items = $scope.entity.tbGoodsDesc.specificationItems;
        for(var i = 0; i <items.length; i++){
            $scope.entity.itemList = $scope.addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
        }
    }
    $scope.addColumn = function(list,name,value){
        var newList  =[];
        for(var i = 0; i < list.length; i++){
            var row = list[i];
            for(var j = 0; j < value.length; j++ ){
                // 下载的row是一个json字符串，需要把它转成对象
                var newRow = JSON.parse(JSON.stringify(row));
                newRow.spec[name] = value[j];
                // 第一次遍历
                //  i = 0   row   pingmuchicun 4.5寸  5寸
                //  j = 0   4.5寸
                //  j = 1   5寸
                // 第二次遍历
                //  i = 1   row   jishenneicun 16G  32G   64G
                //  j = 0   16G
                //  j = 1    32G
                //  j = 2    64G
                newList.push(newRow);
            }
        }
        return newList;
    }





    // 图片上传
    $scope.fileUpload = function () {
        fileUploadService.fileUpload().success(
            function (response) {
                if(response.success){
                    $scope.entity_image.url = response.msg;
                }else{
                    alert(response.msg);
                }
            }
        )
    }
    //  图片列表
    // 保存图片到集合
    $scope.addImage = function () {
        $scope.entity.tbGoodsDesc.itemImages.push($scope.entity_image);
        console.log($scope.entity.tbGoodsDesc.itemImages)
    }
    // 从集合中移除图片
    $scope.deleteImage = function (index) {
        $scope.entity.tbGoodsDesc.itemImages.splice(index,1);
    }

    //修改商品的单签状态，0 为申请，1申请中，2，3 审核通过，4 已驳回
    $scope.updateStatus=function (status) {
        goodsService.updateStatus($scope.selectIds,status).success(
            function (response) {
               if (response.success){
                   $scope.reloadList();
                   $scope.selectIds=[];
               } else {
                   alert(response.msg);
               }
            }
        )
    }

});