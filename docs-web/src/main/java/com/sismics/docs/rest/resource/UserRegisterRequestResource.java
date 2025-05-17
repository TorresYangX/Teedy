package com.sismics.docs.rest.resource;

import com.sismics.docs.core.dao.UserRegisterRequestDao;
import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.model.jpa.UserRegisterRequest;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.rest.exception.ClientException;
import com.sismics.security.UserPrincipal;
import com.sismics.docs.rest.constant.BaseFunction;
import com.sismics.docs.rest.resource.BaseResource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/register/request")
@Produces(MediaType.APPLICATION_JSON)
public class UserRegisterRequestResource extends BaseResource {
    private UserRegisterRequestDao requestDao = new UserRegisterRequestDao();
    private UserDao userDao = new UserDao();

    /**
     * 访客提交注册请求（JSON）
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response submitRequest(UserRegisterRequest req) {
        if (userDao.getActiveByUsername(req.getUsername()) != null) {
            throw new ClientException("AlreadyExists", "用户名已存在");
        }
        requestDao.create(req);
        return Response.ok().entity("注册请求已提交，等待管理员审批").build();
    }

    /**
     * 访客提交注册请求（表单）
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response submitRequestForm(@FormParam("username") String username,
                                      @FormParam("email") String email,
                                      @FormParam("password") String password) {
        if (userDao.getActiveByUsername(username) != null) {
            throw new ClientException("AlreadyExists", "用户名已存在");
        }
        UserRegisterRequest req = new UserRegisterRequest();
        req.setUsername(username);
        req.setEmail(email);
        req.setPassword(password);
        requestDao.create(req);
        return Response.ok().entity("注册请求已提交，等待管理员审批").build();
    }

    /**
     * 管理员获取所有待审批注册请求
     */
    @GET
    @Path("/pending")
    public List<UserRegisterRequest> getPendingRequests(@Context SecurityContext sc) {
        authenticate();
        checkBaseFunction(BaseFunction.ADMIN);
        return requestDao.findByStatus("pending");
    }

    /**
     * 管理员审批（通过）
     */
    @POST
    @Path("/approve/{id}")
    public Response approve(@PathParam("id") Long id, @Context SecurityContext sc) throws Exception {
        authenticate();
        checkBaseFunction(BaseFunction.ADMIN);
        UserRegisterRequest req = requestDao.findById(id);
        if (req == null || !"pending".equals(req.getStatus())) {
            throw new ClientException("NotFound", "请求不存在或已处理");
        }
        // 创建新用户，所有非空字段都赋值
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(req.getPassword());
        user.setEmail(req.getEmail() != null ? req.getEmail() : "");
        user.setRoleId("user"); // 普通用户角色ID，如有常量可用请替换
        user.setPrivateKey(com.sismics.docs.core.util.EncryptionUtil.generatePrivateKey());
        user.setStorageQuota(0L); // 可根据实际业务设定默认配额
        user.setStorageCurrent(0L);
        user.setCreateDate(new java.util.Date());
        user.setOnboarding(true); // 或false，视业务而定
        userDao.create(user, principal.getId());
        // 更新请求状态
        requestDao.updateStatus(id, "approved");
        return Response.ok().entity("已通过").build();
    }

    /**
     * 管理员审批（拒绝）
     */
    @POST
    @Path("/reject/{id}")
    public Response reject(@PathParam("id") Long id, @Context SecurityContext sc) {
        authenticate();
        checkBaseFunction(BaseFunction.ADMIN);
        UserRegisterRequest req = requestDao.findById(id);
        if (req == null || !"pending".equals(req.getStatus())) {
            throw new ClientException("NotFound", "请求不存在或已处理");
        }
        requestDao.updateStatus(id, "rejected");
        return Response.ok().entity("已拒绝").build();
    }
} 