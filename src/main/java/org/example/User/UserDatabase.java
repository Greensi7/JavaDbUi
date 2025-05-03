package org.example.User;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;

public class UserDatabase implements Serializable {
    private static final String FILE_NAME = "userdb.ser";

    private final HashMap<String, UserRecord> users = new HashMap<>();

    public Boolean addUser(String username, String password, UserType userType) {
        if(!users.containsKey(username)) {
            users.put(username, new UserRecord(username, password, userType));
            save();
            return true;
        }
        return false;
    }

    public boolean isValidUser(String username, String password) {
        return users.containsKey(username) && users.get(username).getPassword().equals(password);
    }

    public Boolean exists(String username){
        return users.containsKey(username);
    }

    public Collection<UserRecord> getAllUsers() {
        return users.values();
    }


    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeUser(String name){
        users.remove(name);
        save();
    }

    public UserRecord getUserData(String name){
        return users.get(name);
    }

    public static UserDatabase load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new UserDatabase();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (UserDatabase) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new UserDatabase();
        }
    }
}
