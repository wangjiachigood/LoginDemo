package com.pb.joindata.logindemo;

import android.provider.BaseColumns;

/**
 * Created by wangjiachi on 2017/8/15.
 */

public final class DbUser {
    public static final class User implements BaseColumns {
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String ISSAVED = "issaved";
    }
}
