'use strict';

/**
 * Settings user page controller.
 */
angular.module('docs').controller('SettingsUser', function($scope, $state, Restangular) {
  /**
   * Load users from server.
   */
  $scope.loadUsers = function() {
    Restangular.one('user/list').get({
      sort_column: 1,
      asc: true
    }).then(function(data) {
      $scope.users = data.users;
    });
  };
  
  $scope.loadUsers();
  
  /**
   * Edit a user.
   */
  $scope.editUser = function(user) {
    $state.go('settings.user.edit', { username: user.username });
  };

  // 注册请求管理
  $scope.registerRequests = [];
  $scope.loadRegisterRequests = function() {
    Restangular.one('register/request').all('pending').getList().then(function(data) {
      $scope.registerRequests = data;
    });
  };
  $scope.approveRegisterRequest = function(id) {
    Restangular.one('register/request/approve', id).post().then(function() {
      $scope.loadRegisterRequests();
    });
  };
  $scope.rejectRegisterRequest = function(id) {
    Restangular.one('register/request/reject', id).post().then(function() {
      $scope.loadRegisterRequests();
    });
  };

  $scope.loadRegisterRequests();
});