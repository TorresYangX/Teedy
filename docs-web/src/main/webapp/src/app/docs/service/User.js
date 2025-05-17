'use strict';

/**
 * User service.
 */
angular.module('docs').factory('User', function(Restangular) {
  var userInfo = null;
  
  return {
    /**
     * Returns user info.
     * @param force If true, force reloading data
     */
    userInfo: function(force) {
      if (userInfo === null || force) {
        userInfo = Restangular.one('user').get();
      }
      return userInfo;
    },
    
    /**
     * Login an user.
     */
    login: function(user) {
      return Restangular.one('user').post('login', user);
    },
    
    /**
     * Logout the current user.
     */
    logout: function() {
      return Restangular.one('user').post('logout', {});
    },

    /**
     * 提交注册请求
     */
    registerRequest: function(registerData) {
      return Restangular.all('register/request').post(registerData);
    },

    /**
     * 获取所有待审批注册请求（管理员）
     */
    getPendingRegisterRequests: function() {
      return Restangular.one('register/request').all('pending').getList();
    },

    /**
     * 审批注册请求（通过）
     */
    approveRegisterRequest: function(id) {
      return Restangular.one('register/request/approve', id).post();
    },

    /**
     * 审批注册请求（拒绝）
     */
    rejectRegisterRequest: function(id) {
      return Restangular.one('register/request/reject', id).post();
    }
  }
});