package com.projects.taxiservice.model.queries;

import com.projects.taxiservice.persistent.DBManageable;
import com.projects.taxiservice.model.users.User;
import com.projects.taxiservice.model.taxi.CarClass;
import com.projects.taxiservice.model.taxi.Driver;

import java.time.LocalDateTime;

/**
 * Represents all information that <code>Driver</code> requires to work with <code>User</code>
 * Also shows the current state of request.
 */

public class UserQuery implements DBManageable {
    private int id;
    private User customer;
    private Driver driver;

    private LocalDateTime created, activated, closed;

    private String name;
    private String address;
    private String additionalInformation, feedback;
    private String phoneNumber;
    private CarClass carClass;
    private QueryStatus status;

    public static UserQuery EMPTY = new UserQuery().setId(-1);

    public UserQuery(){
        created = LocalDateTime.now();
    }

    public QueryStatus getStatus() {
        return status;
    }

    public UserQuery setStatus(QueryStatus status) {
        this.status = status;
        return this;
    }

    public CarClass getCarClass() {
        return carClass;
    }

    public UserQuery setCarClass(CarClass carClass) {
        this.carClass = carClass;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public UserQuery setAddress(String adress) {
        this.address = adress;
        return this;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public UserQuery setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public UserQuery setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserQuery setName(String name) {
        this.name = name;
        return this;
    }

    public int getId() {
        return id;
    }

    public UserQuery setId(int id) {
        this.id = id;
        return this;
    }

    public User getCustomer() {
        return customer;
    }

    public UserQuery setCustomer(User customer) {
        this.customer = customer;
        return this;
    }

    public Driver getDriver() {
        return driver;
    }

    public UserQuery setDriver(Driver driver) {
        this.driver = driver;
        return this;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getActivated() {
        return activated;
    }

    public void setActivated(LocalDateTime activated) {
        this.activated = activated;
    }

    public LocalDateTime getClosed() {
        return closed;
    }

    public void setClosed(LocalDateTime closed) {
        this.closed = closed;
    }

    public String getFeedback() {
        return feedback;
    }

    public UserQuery setFeedback(String feedback) {
        this.feedback = feedback;
        return this;
    }
}
