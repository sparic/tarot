angular.module('myee', [])
    .controller('explorerCtrl', explorerCtrl);

/**
 * explorerCtrl - controller
 */
explorerCtrl.$inject = ['$scope', '$resource', '$filter', 'cfromly', 'Constants', 'cAlerts', 'toaster','$rootScope'];
function explorerCtrl($scope, $resource, $filter, cfromly, Constants, cAlerts, toaster,$rootScope) {
    var lang = $rootScope.lang_zh;
    var iDatatable = 0, iPush = 2, iEditor = 1;
    $scope.activeTab = iDatatable;
    var nodeTypes = [{name: '目录', value: 0}, {name: '文件', value: 1}];
    $scope.treeControl = {};
    $scope.treeData = [{path: '/', name: '/', type: 0, salt: '/'}];
    $scope.expandField = {field: 'name'};
    $scope.treeColumns = [
        {
            displayName: '修改时间',
            columnWidth: '15%',
            cellTemplate: '<span>{{cellTemplateScope.format(row.branch)}}</span>',
            cellTemplateScope: {
                format: function (data) {
                    if (!data.modified) return "-";
                    return $filter('date')(new Date(data.modified), 'yyyy-MM-dd HH:mm:ss');
                }
            }
        },
        {
            displayName: '类型',
            columnWidth: '10%',
            cellTemplate: '<span>{{cellTemplateScope.text(row.branch)}}</span>',
            cellTemplateScope: {
                text: function (data) {
                    return data.type == 0 ? '目录' : '文件';
                },
                style: function (data) {
                    return data.type == 0 ? 'label label-primary' : 'label label-danger';
                }
            }
        },
        {
            displayName: '大小',
            columnWidth: '10%',
            cellTemplate: '<span>{{cellTemplateScope.format(row.branch)}}</span>',
            cellTemplateScope: {
                format: function (data) {
                    if (!data.size) return '-';
                    var value = data.size;
                    if (value > 1024 * 1024 * 1024) {
                        return Math.ceil(value / (1024 * 1024 * 1024)) + 'G';
                    }
                    if (value > 1024 * 1024) {
                        return Math.ceil(value / (1024 * 1024)) + 'M';
                    }
                    if (value > 1024) {
                        return Math.ceil(value / 1024) + 'K';
                    }
                    return value + 'B';
                }
            }
        },
        {
            displayName: '操作',
            columnWidth: '150',
            cellTemplate: '<a><i ng-if="row.branch.type == 0" class="btn-icon fa fa-plus" tooltip-placement="top" uib-tooltip="'+ lang.addResource +'" ng-click="cellTemplateScope.add(row.branch)"></i></a>' +
            '<a><i ng-if="row.branch.type == 1" class="btn-icon fa fa-ban" tooltip-placement="top" uib-tooltip="'+ lang.noAddFile +'"></i></a>' +
            '<span class="divider"></span>' +
            '<a><i ng-if="row.branch.type == 1" class="btn-icon fa fa-pencil" tooltip-placement="top" uib-tooltip="'+ lang.edit +'" ng-click="cellTemplateScope.edit(row.branch)"></i></a>' +
            '<a><i ng-if="row.branch.type == 0" class="btn-icon fa fa-ban" tooltip-placement="top" uib-tooltip="'+ lang.noEditFolder +'"></i></a>' +
            '<span class="divider"></span>' +
            '<a><i ng-if="row.branch.salt == row.branch.storeId && row.branch.path != \'/\'" class="btn-icon fa fa-trash-o" tooltip-placement="top" uib-tooltip="'+ lang.delete +'" ng-click="cellTemplateScope.delete(row.branch)"></i></a>' +
            '<a><i ng-if="row.branch.salt != row.branch.storeId || row.branch.path == \'/\'" class="btn-icon fa fa-ban" tooltip-placement="top" uib-tooltip="'+ lang.noAuthDelete +'"></i></a>' +
            '<span class="divider"></span>' +
            '<a ng-if="row.branch.type == 1" ng-href="{{row.branch.url}}" tooltip-placement="top" uib-tooltip="'+ lang.download +'" download><i class="btn-icon fa fa-download" ></i></a>' +
            '<a ng-if="row.branch.type == 0"><i class="btn-icon fa fa-ban" tooltip-placement="top" uib-tooltip="'+ lang.noDownloadFolder +'"></i></a>',
            cellTemplateScope: {
                add: function (data) {
                    $scope.handleSelect(data);
                    //$scope.formData.options.resetModel();
                    $scope.activeTab = iEditor;
                    $scope.formData.model = {
                        salt: data.salt,
                        path: data.path
                    }
                    if (angular.element('#file')[0]) {
                        angular.element('#file')[0].value = '';//清空input[type=file]value[ 垃圾方式 建议不要使用]
                    }
                    $scope.current = data;
                },
                edit: function (data) {
                    $scope.activeTab = iEditor;
                    $scope.formData.model = {
                        salt: data.salt,
                        name: data.name,
                        path: data.path,
                        type: data.type,
                        editorModel: 1
                    }
                    if (angular.element('#file')[0]) {
                        angular.element('#file')[0].value = '';//清空input[type=file]value[ 垃圾方式 建议不要使用]
                    }
                    $scope.current = data;
                    $scope.flag1 = true;
                },
                delete: function (data) {
                    $scope.delete(data);
                },
            }
        }
    ];

    $scope.handleSelect = function (data) {
        if (data.type == 0) {
            $resource('../admin/file/search').save({node: data.path}, {}).$promise.then(
                function success(resp) {
                    angular.merge(data.children, resp.rows);
                }
            );
        }
    };



    //点击推送按钮时调用
    $scope.goSend = function () {
        var arraySelected = [];
        var data = $scope.treeData;
        recursionTree(data, arraySelected);
        if (arraySelected.length == 0) {
            toaster.warning({body: "请选择文件！"});
            return;
        }
        if (arraySelected.length > 100) {
            toaster.warning({body: "请选择不超过100个文件！"});
            return;
        }
        $scope.activeTab = iPush;
        $scope.formDataPusher.model.store = {name: Constants.thisMerchantStore.name};
        $scope.formDataPusher.model.content = arraySelected;
    };

    //递归出所有选中的文件
    function recursionTree(data, arraySelected) {
        angular.forEach(data, function (d) {
            if (d.checked == true && d.type == 1) {
                arraySelected.push({url: d.url, name: d.name});
            }
            if (d.children.length > 0) {
                recursionTree(d.children, arraySelected);
            }
        });
    }

    //查询推送设备下拉框内容
    function getDeviceUsedList() {
        var data = $resource("./device/used/list4Select").query();
        return data;
    }

    //查询推送应用下拉框内容
    function getAppList() {
        return [{name: 'gaea', value: 1}];
    }

    function parsePushContent(value) {
        return eval(value);
    }

    function formatPushContent(value) {
        return $filter('json')(value, 2);
    }

    var mgrDataPusher = {
        fields: [
            {
                id: 'store.name',
                key: 'store.name',
                type: 'c_input',
                templateOptions: {disabled: true, label: '推送门店', placeholder: '推送门店'}
            },
            {
                key: 'uniqueNo',
                type: 'c_select',
                className: 'c_select',
                templateOptions: {label: '选择推送设备', required: true, options: getDeviceUsedList()}
            },
            {
                key: 'appId',
                type: 'c_select',
                className: 'c_select',
                templateOptions: {label: '选择推送应用', required: true, options: getAppList()}
            },
            {
                key: 'timeout',
                type: 'datepicker',
                templateOptions: {
                    label: '过期时间',
                    placeholder: '过期时间',
                    type: 'text',
                    datepickerPopup: 'yyyy-MM-dd',
                    datepickerOptions: {format: 'yyyy-MM-dd'}
                }
            },
            {
                key: 'storagePath',
                type: 'c_input',
                ngModelAttrs: {
                    maxlen: {
                        attribute: 'maxlength'
                    }
                },
                templateOptions: {label: '终端存储路径', placeholder: '终端存储路径(长度小于50)', maxlen: 50}
            },
            {
                key: 'content',
                type: 'c_textarea',
                ngModelAttrs: {
                    style: {attribute: 'style'},
                    maxlen: {attribute: 'maxlength'}
                },
                parsers: [parsePushContent],
                formatters: [formatPushContent],
                templateOptions: {
                    label: '推送内容',
                    required: true,
                    placeholder: '推送内容(长度小于40000)',
                    rows: 20,
                    style: 'max-width:1000px',
                    maxlen: 40000
                }
            }
        ],
        api: {
            push: '../admin/file/push',
            delete: '../admin/file/delete',
        }
    };

    var mgrData = {
        fields: [
            {
                key: 'salt',
                type: 'c_input',
                templateOptions: {disabled: true, label: '隐藏ID', placeholder: '隐藏ID'},
                hideExpression: function ($viewValue, $modelValue, scope) {
                    return true;   //隐藏
                }
            },
            {
                key: 'editorModel',
                type: 'c_input',
                templateOptions: {disabled: true, label: '编辑模式', placeholder: '编辑模式'},
                hideExpression: function ($viewValue, $modelValue, scope) {
                    return true;   //隐藏
                }
            },
            {
                id: 'path',
                key: 'path',
                type: 'c_input',
                templateOptions: {disabled: true, label: '绝对路径', placeholder: '绝对路径'}
            },
            {
                id: 'name',
                key: 'name',
                type: 'c_input',
                templateOptions: {required: true, label: '文件名称', placeholder: '文件名称'},
            },
            {
                id: 'currPath',
                key: 'currPath',
                type: 'c_input',
                ngModelAttrs: {
                    maxlen: {
                        attribute: 'maxlength'
                    }
                },
                templateOptions: {label: '文件路径', placeholder: '文件路径(长度小于50)', maxlen: 50},
                hideExpression: function ($viewValue, $modelValue, scope) {
                    return scope.model.type == 0 ? true : false;//true新增文件夹时隐藏文件路径，默认路径和名称相同
                },
                expressionProperties: {
                    'templateOptions.disabled': 'model.editorModel==1?true:false', //编辑模式不能修改节点路径
                }
            },
            {
                id: 'type',
                key: 'type',
                type: 'c_select',
                className: 'c_select',
                templateOptions: {required: true, label: '文件类型', options: nodeTypes},
                expressionProperties: {
                    'templateOptions.disabled': 'model.editorModel==1?true:false' //编辑模式不能修改节点类型
                }
            },
            {
                id: "file",
                key: 'file',
                type: 'upload',
                name: '',
                templateOptions: {required: false, type: 'file', label: '上传文件'},
                hideExpression: function ($viewValue, $modelValue, scope) {
                    return scope.model.type == 0 ? true : false;//true新增文件夹时隐藏文件内容输入框 false新增时显示批量修改
                },
                //expressionProperties: {
                //    'templateOptions.disabled': 'model.ifEditor', // disabled when ifEditor is true
                //    //'templateOptions.disabled': 'model.editorModel==1?true:false'
                //}
            },
            {
                key: 'ifEditor',
                type: 'c_input',
                className: 'formly-min-checkbox',
                templateOptions: {label: '文本编辑', required: false, type: 'checkbox'},
                defaultValue: false,
                hideExpression: function ($viewValue, $modelValue, scope) {
                    var flag = scope.model.editorModel == 1 ? true : false;//是否是编辑模式
                    if (scope.model.ifEditor && flag && $scope.flag1) {
                        $scope.showContent($scope.current);
                        $scope.flag1 = false;
                    }
                    return scope.model.type == 0 ? true : false;//新增文件夹时隐藏
                }
            },
            {
                id: 'content',
                key: 'content',
                type: 'c_textarea',
                ngModelAttrs: {
                    style: {attribute: 'style'},
                    maxlen: {attribute: 'maxlength'}
                },
                templateOptions: {
                    disabled: true,
                    label: '文件内容',
                    placeholder: '文件内容(长度小于2000)',
                    rows: 15,
                    style: 'max-width:500px',
                    maxlen: 2000
                },
                hideExpression: function ($viewValue, $modelValue, scope) {
                    return scope.model.type == 0 ? true : false;//true新增文件夹时隐藏文件内容输入框 false新增时显示批量修改
                },
                expressionProperties: {
                    'templateOptions.disabled': '!model.ifEditor', // disabled when ifEditor is false
                    'templateOptions.required': 'model.ifEditor' // disabled when ifEditor is false
                }
            }
        ],
        api: {
            getContent: '../admin/content/get',
            create: '../admin/file/create',
            //delete: './device/used/delete',
        }
    };
    //formly配置项push
    $scope.formDataPusher = {
        fields: mgrDataPusher.fields
    };

    //formly配置项editor
    $scope.formData = {
        fields: mgrData.fields
    };

    //formly提交
    $scope.pushSubmit = function () {
        var formly = $scope.formDataPusher;
        if (formly.form.$valid) {
            formly.options.updateInitialValue();
            $resource(mgrDataPusher.api.push).save({}, formly.model).$promise.then(
                function success(resp) {
                    if (resp != null && resp.status == 0) {
                        toaster.success({body: resp.statusMessage});
                        $scope.goDataTable();
                    } else {
                        toaster.error({body: resp.statusMessage});
                    }
                }
            );
        }
    };

    //删除资源
    $scope.delete = function (data) {
        var parentNode = $scope.treeControl.get_parent_branch(data);
        cAlerts.confirm('确定删除?', function () {
            //点击确定回调
            $resource(mgrDataPusher.api.delete).save({salt: data.salt, path: data.path}, {}).$promise.then(function success(resp) {
                if (resp != null && resp.status == 0) {
                    $scope.deleteDom(parentNode.children, data.uid);
                    toaster.success({body: "删除成功"});
                    $scope.goDataTable();
                } else {
                    toaster.error({body: resp.statusMessage});
                }
            });
        });
    };

    //删除资源dom删除
    $scope.deleteDom = function (childrenList, uid) {
        var i = 0;
        while (i < childrenList.length) {
            if (childrenList[i].uid == uid) {
                childrenList.splice(i, 1)
                break;
            }
            i++;
        }
    }

    //formly提交
    $scope.editorSubmit = function () {
        var formly = $scope.formData;
        if (formly.form.$valid) {
            var addFile = $scope.formData_addFile;
            if (!addFile) {
                addFile = new FormData();
            }
            if ($scope.formData.model.type == 0) {
                var parentPath = $scope.current.path == '/' ? "" : $scope.current.path;
                $scope.current.children.push({
                    name: $scope.formData.model.name,
                    path: parentPath + "/" + $scope.formData.model.name,
                    salt: $scope.current.salt,
                    storeId: $scope.current.storeId,
                    url: $scope.current.url + "/" + $scope.formData.model.name,
                    size: 0,
                    modified: new Date(),
                    children: [],
                    type: 0
                });
                $scope.goDataTable();
            } else {
                var addFile = $scope.formData_addFile || {};
                $resource(mgrData.api.create).save({entityText: JSON.stringify($scope.formData.model)}, addFile).$promise.then(function (res) {
                    if ($scope.formData.model.editorModel == 1 ? true : false) {
                        var fileNewName = $scope.formData.model.name;
                        $scope.current.name = fileNewName;
                        var index = $scope.formData.model.path.lastIndexOf("/");
                        var path = $scope.formData.model.path.substring(0, index);
                        $scope.current.path = path + "/" + fileNewName;
                    } else {
                        angular.merge($scope.current.children, res.rows);
                    }
                    $scope.goDataTable();
                });
            }
        }
    };

    $scope.$on('fileToUpload', function (event, arg) {
        $scope.formData_addFile = arg;
    });

    //formly返回
    $scope.goDataTable = function () {
        $scope.disableSubmit = false;
        $scope.activeTab = iDatatable;
    };

    //成功后调用
    function saveSuccess(response) {

    }

    //失败调用
    function saveFailed(response) {
    }

    $scope.disableSubmit = false;
    $scope.showContent = function (data) {
        $resource(mgrData.api.getContent).get({data: data}, {}).$promise.then(function success(resp) {
            if (resp != null && resp.status == 0) {
                $scope.formData.model.content = resp.rows[0].message;
                $scope.disableSubmit = false;
            } else {
                $scope.disableSubmit = true;
                toaster.error({body: resp.statusMessage});
            }
        });
    }

    /**  读取Json文件内容 **/
    $scope.readJsonFile = function (input) {
        //支持chrome IE10
        if (window.FileReader) {
            var file = input.files[0];
            filename = file.name.split(".")[0];
            var reader = new FileReader();
            reader.onload = function() {
                return this.result;
            }
            reader.readAsText(file);
        }
        //支持IE 7 8 9 10
        else if (typeof window.ActiveXObject != 'undefined'){
            var xmlDoc;
            xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
            xmlDoc.async = false;
            xmlDoc.load(input.value);
            return xmlDoc.xml;
        }
        //支持FF
        else if (document.implementation && document.implementation.createDocument) {
            var xmlDoc;
            xmlDoc = document.implementation.createDocument("", "", null);
            xmlDoc.async = false;
            xmlDoc.load(input.value);
            return xmlDoc.xml;
        } else {
            return 'error';
        }
    }
}