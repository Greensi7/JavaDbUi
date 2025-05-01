package org.example.Data;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import cz.fi.muni.pb162.sqlike.data.database.Database;

public class DatabaseList implements Serializable {

    private Map<String, Database> databases;

    private static final String FILE_PATH = "databaselist.ser";

    public DatabaseList() {
        this.databases = new HashMap<>(); // Initialize the map
    }

    public Boolean addDatabase(String name, Database database) {
        if(!containsDatabase(name)) {
            this.databases.put(name, database);
            save();
            return true;
        }
        return false;
    }

    public Database getDatabase(String name) {
        return this.databases.get(name);
    }

    public Database removeDatabase(String name) {
        Database result = this.databases.remove(name);
        save();
        return result;
    }

    public Boolean containsDatabase(String name) {
        return this.databases.containsKey(name);
    }

    public Map<String, Database> getDatabases() {
        return this.databases;
    }

    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(this);
            System.out.println("DatabaseList saved successfully to " + FILE_PATH);
        } catch (IOException e) {
            System.out.println("Error saving DatabaseList to disk: " + e.getMessage());
        }
    }

    public static DatabaseList load() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                return (DatabaseList) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error loading DatabaseList from disk: " + e.getMessage());
                return null;
            }
        } else {
            System.out.println("No existing DatabaseList found. Returning a new empty DatabaseList.");
            var result = new DatabaseList();
            result.save();
            return result;
        }
    }
}
