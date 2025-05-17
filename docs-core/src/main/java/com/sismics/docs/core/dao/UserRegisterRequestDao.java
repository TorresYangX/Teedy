package com.sismics.docs.core.dao;

import com.sismics.docs.core.model.jpa.UserRegisterRequest;
import com.sismics.util.context.ThreadLocalContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.Date;
import java.util.List;

public class UserRegisterRequestDao {
    /**
     * 新建注册请求
     */
    public void create(UserRegisterRequest request) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        request.setCreateTime(new Date());
        request.setStatus("pending");
        em.persist(request);
    }

    /**
     * 根据状态获取注册请求列表
     */
    public List<UserRegisterRequest> findByStatus(String status) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select r from UserRegisterRequest r where r.status = :status order by r.createTime desc");
        q.setParameter("status", status);
        return q.getResultList();
    }

    /**
     * 根据ID获取注册请求
     */
    public UserRegisterRequest findById(Long id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        return em.find(UserRegisterRequest.class, id);
    }

    /**
     * 更新注册请求状态
     */
    public void updateStatus(Long id, String status) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        UserRegisterRequest req = em.find(UserRegisterRequest.class, id);
        if (req != null) {
            req.setStatus(status);
            req.setUpdateTime(new Date());
        }
    }

    /**
     * 删除注册请求
     */
    public void delete(Long id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        UserRegisterRequest req = em.find(UserRegisterRequest.class, id);
        if (req != null) {
            em.remove(req);
        }
    }

    /**
     * 检查用户名或邮箱是否已存在注册请求
     */
    public boolean existsByUsernameOrEmail(String username, String email) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select count(r) from UserRegisterRequest r where r.username = :username or r.email = :email");
        q.setParameter("username", username);
        q.setParameter("email", email);
        Long count = (Long) q.getSingleResult();
        return count > 0;
    }
} 