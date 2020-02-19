app.controller('searchController',function($scope,$location,searchService){

    //搜索
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},
        'price':'','pageNo':1,'pageSize':10,'sortValue':'','sortFiled':''}

    //加载查询字符串
    $scope.loadkeywords=function(){
        $scope.searchMap.keywords=  $location.search()['keywords'];
        $scope.search();
    }
    $scope.search=function(){
        $scope.searchMap.pageNo= parseInt($scope.searchMap.pageNo) ;
        searchService.search( $scope.searchMap ).success(
            function(response){
                $scope.resultMap=response;//搜索返回的结果
                buildPageLabel();
            }
        );
    }

    //在searchMap中添加搜索的田间
    $scope.addSearchItem=function (key,value) {
        $scope.searchMap.pageNo = 1;
        if(key == "category" || key == 'brand'||key == 'price'){
            $scope.searchMap[key]=value;
        }else {
            $scope.searchMap.spec[key]=value;
        }
        //每添加一项，就执行一次搜索
        $scope.search();
    }
    //移除searchMap中的搜索条件
    $scope.removeSearchItem=function (key) {
        $scope.searchMap.pageNo = 1;
        if(key == "category" || key == 'brand'||key == 'price'){
            $scope.searchMap[key]='';
        }else {
            delete $scope.searchMap.spec[key];
        }
        //每删除一项条件就执行一次搜索
        $scope.search();
    }

//    分页查询代码,没$scope,则代表此方法只能在这个js中被调用，相当私有方法
//    totalPages为总页数
    buildPageLabel = function () {
        $scope.pageLabel=[];//构建分页的下面的分页栏属性 1 2 3 4 5 。。。页
        var maxPageNo=$scope.resultMap.totalPages;
        var maxPageNo= $scope.resultMap.totalPages;//得到总页码
        var firstPage=1;//开始页码
        var lastPage=maxPageNo;//截止页码
         $scope.firstDot = true;//前面的小圆点
         $scope.lastDot = true;//后面的小圆点
        if(maxPageNo> 5){  //如果总页数大于5页,显示部分页码
            if($scope.searchMap.pageNo<=3){//如果当前页小于等于3
                lastPage=5; //前5页
                //则当前页为1 2  3 ，前面不显示小圆点
                $scope.firstDot = false;
            }else if( $scope.searchMap.pageNo>=lastPage-2  ){//如果当前页大于等于最大页码-2
                firstPage= maxPageNo-4;		 //后5页
                //则当只有6页，当前页为4，5 时的情况，后面不显示小圆点
                $scope.lastDot = false;

            }else{ //显示当前页为中心的5页
                firstPage=$scope.searchMap.pageNo-2;
                lastPage=$scope.searchMap.pageNo+2;
            }
        }else {
            //当页码数小于5页时，前面后面都不显示小圆点
            $scope.firstDot = false;
            $scope.lastDot = false;
        }
        //循环产生页码标签
        for(var i=firstPage;i<=lastPage;i++){
            $scope.pageLabel.push(i);
        }
    }
    //根据点击页码，循环查询
    $scope.queryByPage=function (pageNo) {
        if(pageNo<0 || pageNo > $scope.resultMap.totalPages){
            return;
        }
        $scope.searchMap.pageNo=pageNo;
       console.log($scope.searchMap);
        $scope.search();
    }
    //判断当前页是否是第一页
    $scope.isTopPage = function () {
        if($scope.searchMap.pageNo == 1){
            return true;
        }else {
            return false;
        }
    }
    //判断当前页是否是最后一页
    $scope.resultMap={"totalPages":1};//初始化没有查询是的结果，总页数 为1
    $scope.isEndPage = function () {
        if($scope.searchMap.pageNo == $scope.resultMap.totalPages){
            return true;
        }else {
            return false;
        }
    }

    //判断指定页码是否是当前页
    $scope.isPage=function (p) {
        if(parseInt(p)==parseInt($scope.searchMap.pageNo)){
            return true;
        }else {
            return false;
        }
    }
    //进行排序查询
    $scope.sortSearch = function (sortValue,sortFled) {
        $scope.searchMap.sortValue = sortValue;
        $scope.searchMap.sortFiled = sortFled;
        $scope.search();
    }

    //判断关键字是不是品牌
    $scope.keywordsIsBrand=function(){
        for(var i=0;i<$scope.resultMap.brandList.length;i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){//如果包含
                return true;
            }
        }
        return false;
    }

});