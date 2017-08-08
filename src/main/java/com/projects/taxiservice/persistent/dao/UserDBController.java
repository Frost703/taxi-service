package com.projects.taxiservice.persistent.dao;

import com.projects.taxiservice.persistent.DBController;
import com.projects.taxiservice.model.users.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by O'Neill on 5/16/2017.
 */
public final class UserDBController {
    private static Connection con;
    private static final Logger logger = Logger.getLogger(DBController.class.getName());

    static{
        setConnection(DBController.getConnection());
    }

    public static synchronized void setConnection(Connection connection){
        if(connection == null) {
            logger.log(Level.SEVERE, "Passed a null connection to setConnection() method");
            throw new IllegalArgumentException("Connection object cannot be null!");
        }
        con = connection;
    }

    private UserDBController() {}

    public static synchronized User insertUser(User user) throws SQLException{
        String insertOperation = "INSERT INTO \"users\" " +
                "(login, password, phone, name, address) VALUES " +
                "(?, ?, ?, ?, ?);";

        if (user == null) {
            logger.log(Level.WARNING, "Passed null User object");
            throw new IllegalArgumentException("Can't insert a null user to DB");
        }

        if (user.getLogin() == null || user.getLogin().length() < 1) {
            logger.log(Level.WARNING, "Passed a User object with empty login");
            throw new IllegalArgumentException("Login cannot be empty");
        }
        if (user.getPassword() == null || user.getPassword().length() < 1) {
            logger.log(Level.WARNING, "Passed a User object with empty password");
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (user.getName() == null || user.getName().length() < 1) {
            logger.log(Level.WARNING, "Passed a User object with empty name");
            throw new IllegalArgumentException("User name cannot be empty");
        }

        try(PreparedStatement st = con.prepareStatement(insertOperation)) {
            st.setString(1, user.getLogin().toLowerCase());
            st.setString(2, user.getPassword());
            st.setString(4, user.getName());

            if (user.getPhone() == null || user.getPhone().length() < 3) st.setString(3, null);
            else st.setString(3, user.getPhone());

            if (user.getAddress() == null || user.getAddress().length() < 3) st.setString(5, null);
            else st.setString(5, user.getAddress());

            st.executeUpdate();

            user = selectUser(user.setId(-1));
            if (user.getId() < 0) {
                throw new SQLException("Failed to insert a user to DB");
            }
        }

        logger.log(Level.FINEST, "Inserted a new User to DB with id={0}", user.getId());
        return user;
    }

    public static synchronized User selectUser(User user) throws SQLException{
        if (user == null) {
            logger.log(Level.WARNING, "Passed null User object");
            throw new IllegalArgumentException("Can't perform select user statement. Passed User object is null");
        }
        if (user.getId() < 0 && (user.getLogin() == null || user.getLogin().length() < 3)) {
            logger.log(Level.WARNING, "Passed a User object with (id<1) and empty login");
            throw new IllegalArgumentException("Can't perform select user statement. Id or login must be provided");
        }

        String selectUser = "SELECT * FROM \"users\" WHERE ";
        ResultSet rs = null;
        PreparedStatement st = null;

        if (user.getId() > 0) {
            selectUser += "id=?;";
            st = con.prepareStatement(selectUser);
            st.setInt(1, user.getId());
        } else {
            selectUser += "login=?;";
            st = con.prepareStatement(selectUser);
            st.setString(1, user.getLogin().toLowerCase());
        }

        User userStored = new User();
        rs = st.executeQuery();
        while(rs.next()){
            userStored.setId(rs.getInt("id")).setLogin(rs.getString("login")).setName(rs.getString("name"))
                    .setPhone(rs.getString("phone")).setAddress(rs.getString("address"));
        }

        rs.close();
        st.close();

        logger.log(Level.FINEST, "Returned an object from DB with id={0}", userStored.getId());
        return userStored;
    }
}