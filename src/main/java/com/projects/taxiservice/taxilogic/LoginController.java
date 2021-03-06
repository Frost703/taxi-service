package com.projects.taxiservice.taxilogic;

import com.projects.taxiservice.TaxiService;
import com.projects.taxiservice.persistent.dao.DriverDBController;
import com.projects.taxiservice.persistent.dao.UserDBController;
import com.projects.taxiservice.taxilogic.interfaces.LoginControllerOperations;
import com.projects.taxiservice.taxilogic.utilities.RandomTokenGen;
import com.projects.taxiservice.taxilogic.utilities.TokenFilter;
import com.projects.taxiservice.model.users.User;
import com.projects.taxiservice.model.taxi.Driver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Controls all web requests that come through /login endpoint
 */
@RestController
@CrossOrigin
@RequestMapping("/login")
public class LoginController implements LoginControllerOperations {
    private static final Logger logger = Logger.getLogger(TaxiService.class.getName());

    /**
     * Signs in a user or driver to the systems and gives a random token for his session
     *
     * @param req http request with login information of current user or driver
     * @return 400 status when login is invalid. 422 when incorrect credentials are passed.
     *         200 status and token when successful
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> signIn(HttpServletRequest req){
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        String type = req.getParameter("type");

        if(login.length() < 1 || password.length() < 1 || type.length() < 1) {
            logger.log(Level.INFO, "One of fields required for login was empty");
            return new ResponseEntity<Object>("Not enough data. Empty field", HttpStatus.BAD_REQUEST);
        }

        //add a new user session and valid response
        if(type.equals("user")) {
            User user = loginUser(login, password);
            if(user.getId() > 0) {
                String secureToken = new RandomTokenGen().getSecureToken();
                if(TokenFilter.isUserSession(secureToken)) TokenFilter.removeUserSession(secureToken);
                TokenFilter.addUserSession(secureToken, user);

                logger.log(Level.FINEST, "New user login with id={0} and token={1}", new Object[] {user.getId(), secureToken});
                return new ResponseEntity<Object>(secureToken, HttpStatus.OK);
            }
            else return new ResponseEntity<Object>("Incorrect credentials", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        //add a new driver session and valid response
        else {
            Driver driver = loginDriver(login, password);
            if(driver.getId() > 0) {
                String secureToken = new RandomTokenGen().getSecureToken();
                if(TokenFilter.isDriverSession(secureToken)) TokenFilter.removeDriverSession(secureToken);
                TokenFilter.addDriverSession(secureToken, driver);

                logger.log(Level.FINEST, "New user driver with id={0} and token={1}", new Object[] {driver.getId(), secureToken});
                return new ResponseEntity<Object>(secureToken, HttpStatus.OK);
            }
            else return new ResponseEntity<Object>("Incorrect credentials", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    /**
     * Goes to database and selects user according to login
     *
     * @param login of user
     * @param password of user
     * @return a new User object received from database
     */
    private User loginUser(String login, String password){
        User customer = new User();
        customer.setId(-1)
                .setLogin(login)
                .setPassword(password);

        try {
            User user = UserDBController.selectUser(customer);

            if(user.getId() < 1) return User.EMPTY;
            else{
                return user;
            }
        }catch (SQLException sqe) {
            logger.log(Level.WARNING, sqe.getMessage(), sqe);
            return User.EMPTY;
        }
    }

    /**
     * Goes to database and selects driver according to login
     *
     * @param login of driver
     * @param password of driver
     * @return a new Driver object received from database
     */
    private Driver loginDriver(String login, String password){
        Driver driver = new Driver();
        driver.setId(-1)
                .setLogin(login)
                .setPassword(password);

        try{
            Driver driverStored = DriverDBController.selectDriver(driver);

            if(driverStored.getId() < 1) return Driver.EMPTY;
            else return driverStored;
        } catch(SQLException sqe) {
            logger.log(Level.WARNING, sqe.getMessage(), sqe);
            return Driver.EMPTY;
        }
    }
}
