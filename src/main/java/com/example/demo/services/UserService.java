package com.example.demo.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.models.User;

@Service
public class UserService {

    @Autowired
    private SQL sql;

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try {
            ResultSet rs = sql.select("SELECT * FROM users", null);
            while (rs.next()) {
                users.add(mapUser(rs));
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User getUserById(int id) {
        try {
            ResultSet rs = sql.select("SELECT * FROM users WHERE id = ?", new Object[]{id});
            if (rs.next()) {
                User user = mapUser(rs);
                rs.getStatement().getConnection().close();
                return user;
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createUser(User user) {
        String query = "INSERT INTO users (name, email) VALUES (?, ?)";
        try {
            int rows = sql.executeUpdate(query, new Object[]{user.getName(), user.getEmail()});
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(int id, User user) {
        String query = "UPDATE users SET name = ?, email = ? WHERE id = ?";
        try {
            int rows = sql.executeUpdate(query, new Object[]{user.getName(), user.getEmail(), id});
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int id) {
        String query = "DELETE FROM users WHERE id = ?";
        try {
            int rows = sql.executeUpdate(query, new Object[]{id});
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        return user;
    }
}
