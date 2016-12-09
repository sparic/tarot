/**
 * INSPINIA - Responsive Admin Theme
 *
 * Inspinia theme use AngularUI Router to manage routing and views
 * Each view are defined as state.
 * Initial there are written state for all view in theme.
 *
 */
//动态加载controller
function ctrlManagerLoader(oclazyload, dir, ctrl) {
    return oclazyload.load([
        {
            serie: true,
            files: ['assets/plugins/formly/api-check.min.js']
        },
        {
            serie: true,
            name: 'formly',
            files: ['assets/plugins/formly/formly.min.js']
        },
        {
            serie: true,
            name: 'formlyBootstrap',
            files: ['assets/plugins/formly/angular-formly-templates-bootstrap.js']
        },
        {
            name: 'ngMessages',
            files: ['assets/plugins/angular/angular-messages.min.js']
        },
        {
            serie: true,
            name: 'ngTable',
            files: ['assets/plugins/ng-table/ng-table.js', 'assets/plugins/ng-table/ng-table.css', 'assets/plugins/bootstrap/bootstrap-table.css']
        },
        {
            //serie: true,
            name: 'ngImgCrop',
            files: ['assets/plugins/ngImgCrop/ng-img-crop.js', 'assets/plugins/ngImgCrop/ng-img-crop.css']
        },
        {
            serie: true,
            name: 'treeGrid',
            files: ['assets/plugins/ui-tree/angular-tree-grid.css', 'assets/plugins/ui-tree/angular-tree-grid.js']
        },
        {
            serie: true,
            name: 'ngQiniu',
            files: ['assets/plugins/qiniu/ngQiniu.js']
        },
        {
            name: 'myee',//属于哪个模块
            files: ['assets/mvc/' + dir + '/controller/' + ctrl]
        }
    ])
}

function config($stateProvider, $urlRouterProvider, $ocLazyLoadProvider,$httpProvider) {
    $urlRouterProvider.otherwise("/merchant/shop");

    $ocLazyLoadProvider.config({
        debug: false
    });

    $httpProvider.defaults.headers.post['Content-Type'] = undefined;

    $stateProvider
        .state('merchant', {
            abstract: true,
            url: "/merchant",
            template: "<div ui-view></div>"
        })
        .state('merchant.shop', {
            url: "/shop",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'merchantShopCtrl',
            data: {
                pageTitle: '门店管理',
                subTitle: '门店列表',
                datatable: 'assets/mvc/merchant/view/shop_datatable.html',
                editor: 'assets/mvc/merchant/view/shop_editor.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'merchant', 'shopCtrl.js');
                }
            }
        })
        .state('merchant.merchant', {
            url: "/merchant",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'merchantCtrl',
            data: {
                pageTitle: '门店管理',
                subTitle: '商户管理',
                datatable: 'assets/mvc/merchant/view/merchant_datatable.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'merchant', 'merchantCtrl.js')
                },
                /*qiniu:function($ocLazyLoad){
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            name:'ngQiniu',
                            files: ['assets/plugins/qiniu/ngQiniu.js']
                        }
                    ])
                }*/
            }
        })
        .state('campaign', {
            abstract: true,
            url: "/campaign",
            template: "<div ui-view></div>"
        })
        .state('campaign.priceCheck', {
            url: "/priceCheck",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'campaignCtrl',
            data: {
                pageTitle: '活动管理',
                subTitle: '大学士兑奖',
                datatable: 'assets/mvc/campaign/view/priceCheck_datatable.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'campaign', 'campaignCtrl.js')
                }
            }
        })
        .state('campaign.clientPrize', {
            url: "/clientPrize",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'clientPrizeCtrl',
            data: {
                pageTitle: '活动管理',
                subTitle: '小超人抽奖配置',
                datatable: 'assets/mvc/campaign/view/clientPrize_datatable.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'campaign', 'clientPrizeCtrl.js')
                }
            }
        })
        .state('campaign.clientPrizeCheck', {
            url: "/clientPrizeCheck",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'clientPrizeCheckCtrl',
            data: {
                pageTitle: '活动管理',
                subTitle: '小超人兑奖',
                datatable: 'assets/mvc/campaign/view/clientPrizeCheck_datatable.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'campaign', 'clientPrizeCheckCtrl.js')
                }
            }
        })
        .state('device', {
            abstract: true,
            url: "/device",
            template: "<div ui-view></div>"
        })
        .state('device.type', {
            url: "/type",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'deviceTypeCtrl',
            data: {
                pageTitle: '设备管理',
                subTitle: '设备类型',
                datatable: 'assets/mvc/device/view/device_datatable.html',
                editor: 'assets/mvc/device/view/device_editor.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'device', 'deviceTypeCtrl.js');
                }
            }
        })
        .state('device.list', {
            url: "/list",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: "deviceUsedCtrl",
            data: {
                pageTitle: '设备管理',
                subTitle: '设备列表',
                datatable: 'assets/mvc/device/view/device_used_datatable.html',
                editor: 'assets/mvc/device/view/device_used_editor.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'device', 'deviceUsedCtrl.js');
                }
            }
        })
        .state('device.product', {
            url: "/product",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'productUsedCtrl',
            data: {
                pageTitle: '设备管理',
                subTitle: '设备组列表',
                datatable: 'assets/mvc/device/view/product_used_datatable.html',
                editor: 'assets/mvc/device/view/product_used_editor.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'device', 'productUsedCtrl.js');
                }
            }
        })
        .state('explorer', {
            abstract: true,
            url: "/explorer",
            template: "<div ui-view></div>",
            data: {pageTitle: '资源管理'}
        })
        .state('explorer.explorer', {
            url: "/explorer",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'explorerCtrl',
            data: {
                subTitle: '资源列表',
                datatable: 'assets/mvc/explorer/view/explorer_datatable.html',
                editor: 'assets/mvc/explorer/view/explorer_editor.html',
                other1: 'assets/mvc/explorer/view/explorer_push.html',
                other2: 'assets/mvc/explorer/view/explorer_updateConfig.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'explorer', 'explorerCtrl.js');
                }
            }
        })
        .state('explorer.logging', {
            url: "/logging",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'loggingCtrl',
            data: {
                subTitle: '资源日志',
                datatable: 'assets/mvc/explorer/view/logging_datatable.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'explorer', 'loggingCtrl.js')
                }
            }
        })
        .state('cater', {
            abstract: true,
            url: "/cater",
            template: "<div ui-view></div>",
            data: {pageTitle: '餐厅设置'}
        })
        .state('cater.type', {
            url: "/type",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'tableTypeMgrCtrl',
            data: {
                subTitle: '餐桌类型',
                datatable: 'assets/mvc/cater/view/type_datatable.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'cater', 'tableTypeCtrl.js')
                }
            }
        })
        .state('cater.zone', {
            url: "/zone",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'tableZoneMgrCtrl',
            data: {
                subTitle: '餐桌区域',
                datatable: 'assets/mvc/cater/view/zone_datatable.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'cater', 'tableZoneCtrl.js')
                }
            }
        })
        .state('cater.table', {
            url: "/table",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'tableMgrCtrl',
            data: {
                subTitle: '餐桌管理',
                datatable: 'assets/mvc/cater/view/table_datatable.html',
                editor: 'assets/mvc/cater/view/table_editor.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'cater', 'tableCtrl.js')
                }
            }
        })
        .state('superman', {
            abstract: true,
            url: "/super",
            template: "<div ui-view></div>",
            data: {pageTitle: '小超人'}
        })
        .state('superman.menu', {
            url: "/menu",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'superMenuMgrCtrl',
            data: {
                subTitle: '菜品管理',
                datatable: 'assets/mvc/cater/view/super_menu_datatable.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'cater', 'superMenuCtrl.js')
                }
            }
        })
        .state('superman.statistic', {
            abstract: true,
            url: "/statistic",
            template: "<div ui-view></div>",
            data: {pageTitle: '统计'}
        })
        .state('superman.statistic.serviceEvaluation', {
            url: "/serviceEvaluation",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'evaluationCtrl',
            data: {
                subTitle: '服务评价',
                datatable: 'assets/mvc/superman/view/evaluation_report.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'superman', 'evaluationCtrl.js')
                }
            }
        })
        .state('superman.storeResource', {
            abstract: true,
            url: "/storeResource",
            template: "<div ui-view></div>",
            data: {pageTitle: '本店资源查看'}
        })
        .state('superman.storeResource.activity', {
            url: "/activity",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'superMenuMgrCtrl',
            data: {
                subTitle: '本店活动',
                datatable: 'assets/mvc/cater/view/super_menu_datatable.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'cater', 'superMenuCtrl.js')
                }
            }
        })
        .state('superman.storeResource.video', {
            url: "/video",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'superMenuMgrCtrl',
            data: {
                subTitle: '本店视频',
                datatable: 'assets/mvc/cater/view/super_menu_datatable.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'cater', 'superMenuCtrl.js')
                }
            }
        })
        .state('superman.MYResource', {
            abstract: true,
            url: "/MYResource",
            template: "<div ui-view></div>",
            data: {pageTitle: '木爷资源管理'}
        })
        .state('superman.MYResource.activity', {
            url: "/activity",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'superMenuMgrCtrl',
            data: {
                subTitle: '木爷活动资源',
                datatable: 'assets/mvc/cater/view/super_menu_datatable.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'cater', 'superMenuCtrl.js')
                }
            }
        })
        .state('superman.MYResource.video', {
            url: "/video",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'superMenuMgrCtrl',
            data: {
                subTitle: '木爷视频资源',
                datatable: 'assets/mvc/cater/view/super_menu_datatable.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'cater', 'superMenuCtrl.js')
                }
            }
        })
        .state('superman.MYResource.ad', {
            url: "/ad",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'superMenuMgrCtrl',
            data: {
                subTitle: '内嵌广告资源',
                datatable: 'assets/mvc/cater/view/super_menu_datatable.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'cater', 'superMenuCtrl.js')
                }
            }
        })
        .state('superman.MYResource.material', {
            url: "/material",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'superMenuMgrCtrl',
            data: {
                subTitle: '木爷素材资源',
                datatable: 'assets/mvc/cater/view/super_menu_datatable.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'cater', 'superMenuCtrl.js')
                }
            }
        })
        .state('superman.push', {
            abstract: true,
            url: "/push",
            template: "<div ui-view></div>",
            data: {pageTitle: '木爷资源推送'}
        })
        .state('superman.push.activity', {
            url: "/activity",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'superMenuMgrCtrl',
            data: {
                subTitle: '木爷活动推送',
                datatable: 'assets/mvc/cater/view/super_menu_datatable.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'cater', 'superMenuCtrl.js')
                }
            }
        })
        .state('superman.push.video', {
            url: "/video",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'superMenuMgrCtrl',
            data: {
                subTitle: '木爷视频推送',
                datatable: 'assets/mvc/cater/view/super_menu_datatable.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'cater', 'superMenuCtrl.js')
                }
            }
        })
        .state('superman.push.ad', {
            url: "/ad",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'superMenuMgrCtrl',
            data: {
                subTitle: '内嵌广告推送',
                datatable: 'assets/mvc/cater/view/super_menu_datatable.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'cater', 'superMenuCtrl.js')
                }
            }
        })
        .state('superman.push.material', {
            url: "/material",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'superMenuMgrCtrl',
            data: {
                subTitle: '木爷素材推送',
                datatable: 'assets/mvc/cater/view/super_menu_datatable.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'cater', 'superMenuCtrl.js')
                }
            }
        })
        .state('dataCenter', {
            abstract: true,
            url: "/dataCenter",
            template: "<div ui-view></div>"
        })
        .state('dataCenter.selfCheckLog', {
            url: "/selfCheckLog",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'logListCtrl',
            data: {
                pageTitle: '数据中心',
                subTitle: '自检日志',
                datatable: 'assets/mvc/datacenter/view/selfCheckLog_datatable.html',
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'datacenter', 'logCtrl.js');
                }
            }
        })
        .state('dataCenter.waittoken', {
            url: "/waittoken",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'waitTokenCtrl',
            data: {
                pageTitle: '数据中心',
                subTitle: '排队数据',
                datatable: 'assets/mvc/datacenter/view/waitToken_datatable.html',
                //editor: ''
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'datacenter', 'waitTokenCtrl.js')
                }
            }
        })
        .state('dataCenter.voiceLog', {
            url: "/voiceLog",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'voiceLogCtrl',
            data: {
                pageTitle: '数据中心',
                subTitle: '语音日志',
                datatable: 'assets/mvc/datacenter/view/voicelog_datatable.html',
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'datacenter', 'voiceLogCtrl.js');
                }
            }
        })
        .state('user', {
            abstract: true,
            url: "/user",
            template: "<div ui-view></div>"
        })
        .state('user.user', {
            url: "/user",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'userMgrCtrl',
            data: {
                pageTitle: '账号管理',
                subTitle: '管理员用户',
                datatable: 'assets/mvc/user/view/user_datatable.html',
                editor: 'assets/mvc/user/view/user_editor.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'user', 'userCtrl.js')
                }
            }
        })
        .state('user.adminRole', {
            url: "/adminRole",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'adminRoleMgrCtrl',
            data: {
                pageTitle: '账号管理',
                subTitle: '管理员用户角色',
                datatable: 'assets/mvc/user/view/admin_role_datatable.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'user', 'adminRoleCtrl.js')
                }
            }
        })

        .state('user.customer', {
            url: "/customer",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'customerMgrCtrl',
            data: {
                pageTitle: '账号管理',
                subTitle: '普通用户',
                datatable: 'assets/mvc/user/view/customer_datatable.html',
                editor: 'assets/mvc/user/view/customer_editor.html',
                other1: 'assets/mvc/user/view/customer_bind_store.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'user', 'customerCtrl.js')
                }
            }
        })
        .state('user.role', {
            url: "/role",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'roleMgrCtrl',
            data: {
                pageTitle: '账号管理',
                subTitle: '普通用户角色',
                datatable: 'assets/mvc/user/view/role_datatable.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'user', 'roleCtrl.js')
                }
            }
        })

        .state('remote', {
            abstract: true,
            url: "/remote",
            template: "<div ui-view></div>"
        })
        .state('remote.dUMonitor', {
            url: "/dUMonitor",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'dUMonitorMgrCtrl',
            data: {
                pageTitle: '远程监控',
                subTitle: '设备远程监控',
                datatable: 'assets/mvc/remote/view/dUMonitor.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'remote', 'dUMonitorCtrl.js')
                }
            }
        })
        .state('remote.camera', {
            url: "/camera",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'camera',
            data: {
                pageTitle: '远程监控',
                subTitle: '摄像头监控',
                datatable: 'assets/mvc/remote/view/camera.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'remote', 'camera.js')
                }
            }
        })
        .state('remote.onlineDevice', {
            url: "/onlineDevice",
            templateUrl: "assets/mvc/desktop/view/manager.html",
            controller: 'onlineDevice',
            data: {
                pageTitle: '远程监控',
                subTitle: '在线设备',
                datatable: 'assets/mvc/remote/view/onlineDevice.html'
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return ctrlManagerLoader($ocLazyLoad, 'remote', 'onlineDevice.js')
                }
            }
        });

}
angular
    .module('myee')
    .config(config)
    .run(function ($rootScope, $state) {
        $rootScope.storeInfo = {};
        $rootScope.$state = $state;
        $rootScope.baseUrl={pushUrl:baseUrl.pushUrl,qiniuCdn:baseUrl.qiniuCdn,userName:baseUrl.userName};
        $rootScope.lang_zh = {
            edit:"编辑",
            update:"保存",
            delete:"删除",
            cancel:"取消",
            add:"新增",
            copy:"复制",
            refresh:"刷新",
            download:"下载",
            bindProduct:"关联设备组",
            bindDevice:"关联设备",
            bindDeviceDetail:"关联设备详细",
            bindStorePermit:"设置账号可操作门店",
            attrDetail:"详细属性",
            contentDetail:"内容详细",
            flowDetail:"引流详细",
            flowSet:"设置引流",
            noDeleteThisShop:"当前切换的门店，不能删除",
            noDeleteThisMerchant:"当前切换的门店所属的商户，不能删除",
            noDeleteThisUser:"当前登录的用户，不能删除",
            noEditFolder:"文件夹不能编辑",
            noAuthDelete:"没有权限删除",
            noAddFile:"文件不能新增",
            noDownloadFolder:"文件夹不能下载",
            addResource:"添加"
        };
    })
    .constant('baseConstant',{
        //myeeDefaultUrlImg:"http://cdn.myee7.com/FuMJj5jpAK8_wd2c0KvdwEmCaATt", //长方形logo
        myeeDefaultUrlImg:"http://cdn.myee7.com/FtHyBUTUJ6slWaftvc-NtSJhu6Kg?imageView2/1/w/150/h/95",//正方形logo
    });

