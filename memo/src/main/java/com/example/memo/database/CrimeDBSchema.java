package com.example.memo.database;

/**
 * @author smaxlyb
 * @date 2020/4/21 14:22
 * website: https://smaxlyb.cn
 */
public class CrimeDBSchema {
    public static final class CrimeTable{
        public static final String NAME = "crimes";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
            public static final String PHONE = "phone";
        }
    }
}
