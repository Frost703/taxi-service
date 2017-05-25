package com.projects.taxiservice.dblogic.classcontrollers;

import com.projects.taxiservice.dblogic.DBController;
import com.projects.taxiservice.users.customer.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by O'Neill on 5/16/2017.
 */
public class UserDBController {
    private final Connection con;
    private static final Logger logger = Logger.getLogger(UserDBController.class.getName());

    public UserDBController(Connection con) throws IOException{
        if(con == null) throw new IllegalArgumentException("Connection object cannot be null!");
        this.con = con;
        logger.addHandler(DBController.getLogHandler());
    }

    public List<String> getListOfAvailableOperations(){
        return Arrays.asList("register->inserts into DB", "get->selects by id or login");
    }

    public Object execute(String operation, User user) throws SQLException{
        Object output;
        switch(operation.toLowerCase()){
            case "register": output = insertUser(user); break;
            case "get": output = selectUser(user); break;
            default: output = null; logger.log(Level.INFO, "Operation not recognized. Returning a null object. Operation: {0}", operation);
        }

        return output;
    }

    private int insertUser(User user) throws SQLException{
        String insertOperation = "INSERT INTO \"users\" " +
                "(login, password, phone, name, address) VALUES " +
                "(?, ?, ?, ?, ?);";

        try {
            if (user == null) throw new IllegalArgumentException("Can't insert a null user to DB");

            if (user.getLogin() == null || user.getLogin().length() < 1) throw new IllegalArgumentException("Login cannot be empty");
            if (user.getPassword() == null || user.getPassword().length() < 1)
                throw new IllegalArgumentException("Password cannot be empty");
            if (user.getName() == null || user.getName().length() < 1) throw new IllegalArgumentException("Name cannot be empty");


            PreparedStatement st = con.prepareStatement(insertOperation);
            st.setString(1, user.getLogin().toLowerCase());
            st.setString(2, user.getPassword());
            st.setString(4, user.getName());

            if (user.getPhone() == null || user.getPhone().length() < 3) st.setString(3, null);
            else st.setString(3, user.getPhone());

            if (user.getAddress() == null || user.getAddress().length() < 3) st.setString(5, null);
            else st.setString(5, user.getAddress());

            st.execute();

            user = selectUser(user.setId(-1));
            if(user.getId() < 0) throw new SQLException("Failed to insert a user to DB");

        } catch (SQLException sqe) {
            logger.log(Level.WARNING, sqe.getMessage(), sqe);
            throw sqe;
        } catch(IllegalArgumentException il) {
            logger.log(Level.WARNING, il.getMessage(), il);
            throw il;
        }

        logger.log(Level.INFO, "Inserted a new user to DB with login={0}", user.getLogin());
        return 1;
    }

    private User selectUser(User user) throws SQLException{
        try {
            if (user == null)
                throw new IllegalArgumentException("Can't perform select statement. Passed User object is null");
            if (user.getId() < 0 && (user.getLogin() == null || user.getLogin().length() < 1))
                throw new IllegalArgumentException("Can't perform select statement. Id or login must be provided");

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
            rs = st.executeQuery();

            while(rs.next()){
                user.setId(rs.getInt("id")).setLogin(rs.getString("login")).setName(rs.getString("name"))
                        .setPhone(rs.getString("phone")).setAddress(rs.getString("address"));
            }
            rs.close();
            st.close();



        } catch (SQLException sqe) {
            logger.log(Level.WARNING, sqe.getMessage(), sqe);
            throw sqe;
        } catch(IllegalArgumentException il) {
            logger.log(Level.WARNING, il.getMessage(), il);
            throw il;
        }

        logger.log(Level.FINEST, "Returned an object from DB with id={0} and login={1}", new Object[] {user.getId(), user.getLogin()});
        return user;
    }


}
