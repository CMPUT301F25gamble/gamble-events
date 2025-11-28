import { onRequest } from "firebase-functions/v2/https";
import { initializeApp } from "firebase-admin/app";
import { getMessaging } from "firebase-admin/messaging";
import * as logger from "firebase-functions/logger";

initializeApp();

export const sendPushNotification = onRequest(async (req, res) => {
  const { token, title, body } = req.body;

  logger.warn(`Title: ${title}, Body: ${body}, Token: ${token}, eventID: ${eventID}, channelName: ${channelName}`);

  if (!token || !title || !body) {
    logger.error("Missing required fields");
    res.status(400).send("Missing required fields");
    return;
  }

  const message = {
    token,
    // data: {eventID, channelName},
    notification: { title, body },
  };

  try {
    const response = await getMessaging().send(message);
    logger.info(`Notification sent successfully: ${response}`);
    res.status(200).send(`Notification sent: ${response}`);
  } catch (error) {
    logger.error("Error sending message:", error);
    res.status(500).send("Failed to send notification");
  }
});
