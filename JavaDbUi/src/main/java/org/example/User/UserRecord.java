package org.example.User;

import java.io.Serial;
import java.io.Serializable;

public class UserRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String name;
    private String password;
    private UserType userType;

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
    public UserType getUserType(){
        return userType;
    }
}
