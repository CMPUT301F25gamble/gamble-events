package com.example.eventlotterysystemapplication;

import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.EventNotificationManager;
import com.example.eventlotterysystemapplication.Model.User;

import org.junit.Test;

public class NotificationsUnitTest {
    @Test
    public void TestAdminNotification() {
        Database.getDatabase().getUser("HNxIEk1QQ0QC73YHnk1K5ozDBEJ3", task -> {
            if (task.isSuccessful()){
                User user = task.getResult();
                EventNotificationManager.notifyAdmin(user, user, "Test Admin", "Test Admin");
            }
        });
    }
}
