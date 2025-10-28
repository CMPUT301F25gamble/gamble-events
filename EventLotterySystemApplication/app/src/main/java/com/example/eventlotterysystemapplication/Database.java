package com.example.eventlotterysystemapplication;

public class Database {
    private static Database database;

    private Database(){

    }

    public static Database getDatabase(){
        if (database == null){
            database = new Database();
        }
        return database;
    }

    public void addEntrant(User entrant){

    }

    public void addEvent(Event event){

    }

    public void addNotificationLog(/*add in parameters later*/){

    }
}
