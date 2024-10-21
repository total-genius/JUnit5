package com.angubaidullin.testing.dao;

import java.util.HashMap;
import java.util.Map;

public class UserDaoSpy  extends UserDao{
    private UserDao userDao;

    public UserDaoSpy(UserDao userDao) {
        this.userDao = userDao;
    }

    private Map<Integer, Boolean> answer = new HashMap<Integer, Boolean>();

    @Override
    public boolean delete(Integer id) {
        return answer.getOrDefault(id, userDao.delete(id));
    }
}
