angular.module('myee', [])
    .controller('agentLogCtrl', agentLogCtrl);

agentLogCtrl.$inject = ['$scope', 'cResource'];
function agentLogCtrl($scope, cResource) {

    var mgrData = $scope.mgrData = {
        fields: [
        ],
        api: {
            getAllDeviceUsed: './device/used/listAll',
            pullLog: './agentLog/pullLog',
        }
    };

    $scope.allDeviceUsed = cResource.query('./device/used/listAll');

    $scope.pullLog = function () {
        console.log("11");
    }

}