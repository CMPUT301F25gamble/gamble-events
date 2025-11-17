package com.example.eventlotterysystemapplication;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.eventlotterysystemapplication.Controller.NotificationSender;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NotificationIntegrationTest {

    @Test
    public void testLotteryWinNotification(){
        String token = "fuiOw05WRhyG2yRh8QK_q2:APA91bE5c1qDIgo0uViGErU-7d-WC8gsUo4vJRCAbEP6jkmMiu2uKm8jrw_07LVyGM4VgP_PkUIrq3KmQWaoksQxW4CUazpDjizp_p4qZyCbhegkI6nEmMk";
        String title = "Hi Gaurang Test";
        String message = "Hi Gaurang";
        String eventID = "2jKXO77SjVanAOxAcdBd";
        String channelName = "lotteryWinNotification";

        NotificationSender.sendNotification(token, title, message, eventID, channelName);
    }
}
