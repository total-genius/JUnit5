package com.angubaidullin.testing.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class UserDao {

    public boolean delete(Integer id) {
        try (Connection connection = DriverManager.getConnection("url", "username", "pass")) {
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
