(function() {
    var folke = angular.module('folke', []);


    folke.directive('commandContainer', function () {
        return {
            restrict: 'E',
            transclude: 'true',
            scope: {
                submitText: "@",
                commandName: "@",
                command: "="
            },
            template: '<div class="command-container">' +
                '<div class="my-transclude"></div>' +
                '<input type="button" class="btn btn-default" value="{{submitText}}" ng-click="sendCommand(command, commandName)" />' +
                '</div>',
            controller: 'CommandContainerController',
            link: function (scope, element, attrs, ctrl, transclude) {
                transclude(scope.$parent, function (clone) {
                    element.find(".my-transclude").replaceWith(clone);
                });
            }
        };
    });

    folke.controller('CommandContainerController', function ($scope, $http) {
        var baseUrl = "http://localhost:52661/api/commands/";
        $scope.sendCommand = function (command, commandName) {
            var url = baseUrl + commandName;
            $http.post(url, command).success(function (data) {
                $scope.command = {};
            }).error(function() {
                alert("Failed to execute command: " + commandName);
                console.log(command);
            });
        }
    });

})();


// angular service - update log
// CreatePersonController
// AddAddressToPersonController

//$(function () {
//    $("form").submit(function (e) {
//        var form = $(this);
//        var data = 
//            {
//                Id: '1b181d25-f371-4f56-b0e0-22f636d30781',
//                SSN: {
//                    Value: '1111111111'
//                },
//                Name: {
//                    Value: 'Tomas'
//                }
//            };
//        var jsonData = JSON.stringify(data);
//        $.ajax({
//            url: "/api/commands/createperson",
//            data: jsonData, 
//            type: "POST",
//            dataType: "json",
//            contentType: "application/json; charset=utf-8",
//            succes: function(resultData) {
//                alert("Success: " + resultData);
//            }});
//        e.preventDefault();
//    });
