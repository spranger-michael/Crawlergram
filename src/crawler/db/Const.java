/*
 * Title: Const.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

package crawler.db;

/**
 * DB constants
 */

public class Const {
    public static String MSG_DIAL_PREF = "MESSAGES"; // dialog messages table/collection/etc. prefix
    public static String USERS_COL = "USERS"; // users table/collection/etc.
    public static String CHATS_COL = "CHATS"; // chats table/collection/etc.

    public static String getMsgDialPref() {
        return MSG_DIAL_PREF;
    }

    public static String getUsersCol() {
        return USERS_COL;
    }

    public static void setUsersCol(String usersCol) {
        USERS_COL = usersCol;
    }

    public static String getChatsCol() {
        return CHATS_COL;
    }

    public static void setChatsCol(String chatsCol) {
        CHATS_COL = chatsCol;
    }

    public static void setMsgDialPref(String msgDialPref) {
        MSG_DIAL_PREF = msgDialPref;

    }
}
