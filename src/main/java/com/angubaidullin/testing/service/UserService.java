package com.angubaidullin.testing.service;

import com.angubaidullin.testing.dao.UserDao;
import com.angubaidullin.testing.dto.User;

import java.util.*;
import java.util.stream.Collectors;

public class UserService {

    private UserDao userDao;
    private List<User> userList = new ArrayList<User>();

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }
    public UserService() {

    }


    public List<User> getAll() {

        return userList;
    }

    public boolean add(User user) {
        return userList.add(user);
    }

    public void addAll(User... users) {
        userList.addAll(Arrays.asList(users));
    }

    public Map<Integer, User> getAllConvertedById() {
       return userList.stream()
                .collect(Collectors.toMap(User::getId, user -> user));
    }

    public Optional<User> login (String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password are required");
        }
        return userList.stream()
                .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                .findFirst();
    }

    public boolean deleteUserById(Integer id) {
        return userDao.delete(id);
    }
}
