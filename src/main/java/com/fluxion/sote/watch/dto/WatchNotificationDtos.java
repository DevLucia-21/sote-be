package com.fluxion.sote.watch.dto;

public class WatchNotificationDtos {

    public static class RegisterTokenRequest {
        private String deviceId;
        private String fcmToken;

        public String getDeviceId() {
            return deviceId;
        }

        public String getFcmToken() {
            return fcmToken;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public void setFcmToken(String fcmToken) {
            this.fcmToken = fcmToken;
        }
    }

    public static class SendTestNotificationRequest {
        private String title;
        private String body;

        public String getTitle() {
            return title;
        }

        public String getBody() {
            return body;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }
}
