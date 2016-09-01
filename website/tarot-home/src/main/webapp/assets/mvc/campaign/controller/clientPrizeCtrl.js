angular.module('myee', [])
    .controller('clientPrizeCtrl', clientPrizeCtrl);

/**
 * clientPrizeCtrl - controller
 */
clientPrizeCtrl.$inject = ['$scope', 'Constants','cTables','cfromly','toaster','$resource'];
function clientPrizeCtrl($scope, Constants,cTables,cfromly,toaster,$resource) {
    var mgrData = {
        fields: [
            {
                key: 'name',
                type: 'c_input',
                templateOptions: {type: 'text', label: '奖券名称', required: true, placeholder: '奖券名称'},
                hideExpression: function ($viewValue, $modelValue, scope) {
                    if (scope.model.type != '2') {
                        return false;
                    } else {
                        return true;
                    }
                }
            },
            {
                key: 'type',
                type: 'c_select',
                className:'c_select',
                templateOptions: {
                    required: true,
                    label: '奖券类型',
                    options:  [{name:"手机",value: 0},{name:"验证码",value: 1},{name:"谢谢惠顾",value: 2}]
                }
            },
            {
                key: 'total',
                type: 'c_input',
                templateOptions: {type: 'text', label: '奖券数量', required: true, placeholder: '奖券数量'},
                hideExpression: function ($viewValue, $modelValue, scope) {
                    if (scope.model.type != '1') {
                        return false;
                    } else {
                        return true;
                    }
                }
            },
            {
                key: 'startDate',
                type: 'datepicker',
                templateOptions: {
                    label: '开始日期',
                    type: 'text',
                    required:true ,
                    datepickerPopup: 'yyyy-MM-dd',
                    datepickerOptions: {
                        format: 'yyyy-MM-dd'
                    }
                }
            },
            {
                key: 'endDate',
                type: 'datepicker',
                templateOptions: {
                    label: '结束日期',
                    type: 'text',
                    required:true ,
                    datepickerPopup: 'yyyy-MM-dd',
                    datepickerOptions: {
                        format: 'yyyy-MM-dd'
                    }
                }
            },

            {
                id: 'smallImage',
                key: 'smallPic',
                type: 'upload',
                name: 'img',
                templateOptions: {type: 'file', label: '奖券小图标', placeholder: '奖券小图标'}
            },
            {
                key: 'imagesSmall',
                type: 'c_images',
                templateOptions: {label: '奖券小图标预览', Multi: false}
            },
            {
                id: 'bigImage',
                key: 'bigPic',
                type: 'upload',
                name: 'img',
                templateOptions: {type: 'file', label: '奖券大图标', placeholder: '奖券大图标'}
            },
            {
                key: 'imagesBig',
                type: 'c_images',
                templateOptions: {label: '奖券大图标预览', Multi: false}
            },
            {
                key: 'description',
                type: 'c_textarea',
                ngModelAttrs: {
                    style: {attribute: 'style'}
                },
                templateOptions: {label: '奖券描述', placeholder: '奖券描述', rows: 10,style: 'max-width:500px'}
            },
            {
                key: 'activeStatus',
                type: 'c_input',
                className: 'formly-min-checkbox',
                templateOptions: {label: '启用状态', placeholder: '启用状态',type: 'checkbox'}
            }
        ],
        api: {
            read: './clientPrize/pagingList',
            update: './saveClientPrize',
            delete: './deleteClientPrize',
            upload: './files/create'
        }
    };
    cTables.initNgMgrCtrl(mgrData, $scope);

    $scope.processSubmit = function () {
        var formly = $scope.formData;
        if (formly.form.$valid) {
            var xhr = $resource(mgrData.api.update);
            xhr.save({}, formly.model).$promise.then($scope.saveSuccess, $scope.saveFailed);
        }
    };

    //上传控件监听器
    $scope.$on('fileToUpload', function (event, arg) {
        //console.log(event)
        //上传文件到后台
        var uploadId = event.targetScope.id;
        $resource(mgrData.api.upload).save({
            path: "prizeLogo",
            type: "file"
        }, arg).$promise.then(function (res) {
                //console.log(res)
                if (0 != res.status) {
                    $scope.toasterManage($scope.toastError, res);
                    return;
                }
                $scope.toasterManage($scope.toastUploadSucc);
                if(uploadId == "smallImage"){
                    $scope.formData.model.smallPic = res.dataMap.tree.downloadPath;
                    //console.log($scope.formData.model.images)
                    $scope.formData.model.imagesSmall = res.dataMap.tree.downloadPath;
                }else if(uploadId == "bigImage"){
                    $scope.formData.model.bigPic = res.dataMap.tree.downloadPath;
                    //console.log($scope.formData.model.images)
                    $scope.formData.model.imagesBig = res.dataMap.tree.downloadPath;
                }
            })
    });


}