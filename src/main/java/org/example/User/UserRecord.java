package org.example.User;

import java.io.Serial;
import java.io.Serializable;

public class UserRecord implements Serializable {

    private final String name;
    private String password;
    private final UserType userType;

    public UserRecord(String name, String password, UserType userType){
        this.name = name;
        this.password = password;
        this.userType = userType;
    }

    public String getName(){
        return name;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public UserType getUserType(){
        return userType;
    }
}
