// 共用的js
app.controller("baseController",function ($scope) {

    // 2、加载数据
    $scope.reloadList = function(){
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    }
    // 1、配置分页的控件
    $scope.paginationConf= {
        currentPage:1,
        totalItems:100,
        itemsPerPage:10,
        perPageOptions:[10, 20, 30],
        onChange:function () {
            //  加载数据
            $scope.reloadList();
        }
    }

    //  获取选中的id
    // 定义一个存放选中id的集合
    $scope.selectIds = [];
    $scope.updateIds = function ($event,id) {
        // 如果单选全部选中，把全选的状态选中

        $("#selall").prop('checked',$('.selectOne:checked').length == $('.selectOne').length);
        if($event.target.checked){
            // 如果复选框被选中，就把id添加到selectIds来
            $scope.selectIds.push(id);  // push  把结果添加到集合中
        }else{
            // 如果取消选择，要selectIds中的对应的id移除
            var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx,1); // splice(移除数据对应的下标，移除的个数)
        }
    }

    // 全选
    $scope.selectAll = function () {

        // 先判断全选按钮，是否选中
        if($("#selall").prop('checked')){
            // 为了防止有重复数据（比如，原来单选的时候在$scope.selectIds中已经有数据了，在全选的时候会把原来的数据重复添加）
            $scope.selectIds = [];
            $('.selectOne').each(function () {
                // 让每个复选框勾选，然后取出每一个复选框的值
                $(this).prop('checked',true);
                $scope.selectIds.push(parseInt($(this).val()))
            })
        }else{
            // $(".selectOne").each(function () {
            // 	// 移除单选的选中状态
            // 	$(this).removeAttr('checked');
            // 	$scope.selectIds = [];
            // })
            $(".selectOne").removeAttr("checked");
            $scope.selectIds = [];
        }
    }

    //暂时用于模块管理部分的json类型转为string类型
    $scope.jsonToString = function (jsonStr,key) {
        // 把json字符串变成json对象
        var json =  JSON.parse(jsonStr);
        var value = "";
        for(var i = 0; i < json.length; i++){
            if(i > 0){
                value += ",";
            }
            value += json[i][key];
        }
        return value;
    }

    //从集合中通过key值获取到value
    $scope.findValueByKey = function (list,key,value) {
        for(var i = 0; i < list.length; i++){
            if(list[i][key] == value){
                return list[i];
            }
        }
        return null;
    }



})