/**
 * Created by Martin on 2016/6/27.
 */
angular.module('myee', [])
    .controller('tableMgrCtrl', tableMgrCtrl);

/**
 * roleCtrl - controller
 */
tableMgrCtrl.$inject = ['$scope', '$resource', 'cTables', 'cfromly','toaster'];

function tableMgrCtrl($scope, $resource, cTables, cfromly,toaster) {
    var typeOpts = $resource('../admin/catering/type/options').query();
    var zoneOpts = $resource('../admin/catering/zone/options').query();

    var mgrData = {
        fields: [
            {
                key: 'name',
                type: 'c_input',
                className: 'c_formly_line',
                templateOptions: {label: '名称', required: true, placeholder: '名称'}
            },
            {
                key: 'scanCode',
                type: 'c_input',
                className: 'c_formly_line',
                templateOptions: {type:'number',label: '餐桌码', placeholder: '输入000-200之间的3位数字',min:0,max:200},
            },
            {
                key: 'textId',
                type: 'c_input',
                className: 'c_formly_line',
                templateOptions: {label: 'ERP ID', placeholder: '小超人点菜用'}
            },
            {
                key: 'description',
                type: 'c_input',
                className: 'c_formly_line',
                templateOptions: {label: '描述', placeholder: '描述'}
            },
            {
                key: 'tableType.id',
                type: 'c_select',
                className: 'c_formly_line c_select',
                templateOptions: {
                    label: '桌型',
                    valueProp: 'id',
                    options: typeOpts,
                    required: true,
                    placeholder: '桌型'
                }
            },
            {
                key: 'tableZone.id',
                type: 'c_select',
                className: 'c_formly_line c_select',
                templateOptions: {label: '区域', valueProp: 'id', options: zoneOpts, required: true, placeholder: '区域'}
            },
        ],
        api: {
            read: '../admin/catering/table/paging',
            update: '../admin/catering/table/save',
            delete: '../admin/catering/table/delete'
        }
    };

    cTables.initNgMgrCtrl(mgrData, $scope);
}