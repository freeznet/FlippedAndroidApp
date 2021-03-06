

package com.hkust.android.hack.flipped.core;

/**
 * Bootstrap constants
 */
public final class Constants {
    private Constants() {}

    public static final class Auth {
        private Auth() {}

        /**
         * Account type id
         */
        public static final String BOOTSTRAP_ACCOUNT_TYPE = "com.hkust.android.hack.flipped";

        /**
         * Account name
         */
        public static final String BOOTSTRAP_ACCOUNT_NAME = "Flipped";

        /**
         * Provider id
         */
        public static final String BOOTSTRAP_PROVIDER_AUTHORITY = "com.hkust.android.hack.flipped.sync";

        /**
         * Auth token type
         */
        public static final String AUTHTOKEN_TYPE = BOOTSTRAP_ACCOUNT_TYPE;
    }

    /**
     * All HTTP is done through a REST style API built for demonstration purposes on Parse.com
     * Thanks to the nice people at Parse for creating such a nice system for us to use for bootstrap!
     */
    public static final class Http {
        private Http() {}


        /**
         * Base URL for all requests
         */
        public static final String URL_BASE = "http://meetwithyou-georelation.rhcloud.com/api";

        /**
         * Authentication URL
         */
        public static final String URL_AUTH = URL_BASE + "/Login";

        /**
         * List Users URL
         */
        public static final String URL_USERS = URL_BASE + "/1/users";

        /**
         * List News URL
         */
        public static final String URL_NEWS = URL_BASE + "/1/classes/News";

        /**
         * List Checkin's URL
         */
        public static final String URL_CHECKINS = URL_BASE + "/1/classes/Locations";

        public static final String PARSE_APP_ID = "NIj9zwuOkQAwOX5fnNZ3HfTrWzdGNSiIRY4Pr5vO";
        public static final String PARSE_REST_API_KEY = "06GuUyDDuthWmKLE2D6a6TB3HwvBnTqNeJedy3Cd";
        public static final String HEADER_PARSE_REST_API_KEY = "X-Parse-REST-API-Key";
        public static final String HEADER_PARSE_APP_ID = "X-Parse-Application-Id";
        public static final String CONTENT_TYPE_JSON = "application/json";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String SESSION_TOKEN = "sessionid";


        public static final String URL_REG = URL_BASE + "/Register";

    }


    public static final class Extra {
        private Extra() {}

        public static final String NEWS_ITEM = "news_item";

        public static final String USER = "user";

    }

    public static final class Intent {
        private Intent() {}

        /**
         * Action prefix for all intents created
         */
        public static final String INTENT_PREFIX = "com.hkust.android.hack.flipped.";

    }

    public static class Notification {
        private Notification() {
        }

        public static final int TIMER_NOTIFICATION_ID = 1000; // Why 1000? Why not? :)
    }

}


