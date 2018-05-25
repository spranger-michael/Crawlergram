/*
 * Title: messagesAndMediaToDB.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

package crawler.implementation.apimethods;

import crawler.db.Const;
import crawler.db.DBStorage;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.dialog.TLDialog;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.user.TLAbsUser;
import org.telegram.tl.TLObject;
import org.telegram.tl.TLVector;

import java.util.HashMap;

public class MessagesAndMediaToDB {

    /**
     * Writes only messages to DB
     * @param	api  TelegramApi instance for RPC request
     * @param   dbStorage   database instance
     * @param   dialogs dialogs TLVector
     * @param   chatsHashMap    chats hashmap
     * @param   usersHashMap    users hashmap
     * @param   limit   maximum number of retrieved messages from each dialog (0 if no limit)
     */
    public static void saveMessagesOnlyToDB(TelegramApi api,
                                            DBStorage dbStorage,
                                            TLVector<TLDialog> dialogs,
                                            HashMap<Integer, TLAbsChat> chatsHashMap,
                                            HashMap<Integer, TLAbsUser> usersHashMap,
                                            HashMap<Integer, TLAbsMessage> messagesHashMap,
                                            int limit) {
        for (TLDialog dialog : dialogs) {
            TLObject fullDialog = DialogsHistoryMethods.getFullDialog(api, dialog, chatsHashMap, usersHashMap);
            // writes messages of the dialog to "messages + [dialog_id]" table/collection/etc.

            dbStorage.setTarget(Const.MSG_DIAL_PREF + dialog.getPeer().getId());
            //reads the messages

            /*
            TLVector<TLAbsMessage> absMessages = DialogsHistoryMethods.getWholeMessagesHistory(api, dialog, chatsHashMap, usersHashMap, messagesHashMap, limit);
            // write messages
            dbStorage.writeTLAbsMessages(absMessages);
            System.err.println(dialog.getPeer().getId()+ " "+ absMessages.size());
            // sleep between transmissions to avoid flood wait
            try {Thread.sleep(1000);} catch (InterruptedException ignored) {}
            */

        }
    }


    public static void saveMessagesAndMediaToDB(TelegramApi api,
                                                DBStorage dbStorage,
                                                TLVector<TLDialog> dialogs,
                                                HashMap<Integer, TLAbsChat> chatsHashMap,
                                                HashMap<Integer, TLAbsUser> usersHashMap,
                                                HashMap<Integer, TLAbsMessage> messagesHashMap,
                                                int limit) {
        for (TLDialog dialog : dialogs) {
            // make actions upon each message in loop
            TLVector<TLAbsMessage> absMessages = DialogsHistoryMethods.getWholeMessagesHistory(api, dialog, chatsHashMap, usersHashMap, messagesHashMap, limit);
            for (TLAbsMessage absMessage : absMessages) {

                //TODO redo downloads

                // write the absMessage content in console
                //ConsoleOutputMethods.testMessageOutputConsole(absMessage);
                // save absMessage media to file
                //messageDownloadMedia(api, absMessage, path);
            }

            System.err.println(dialog.getPeer().getId()+ " "+ absMessages.size());

            // sleep between transmissions to avoid flood wait
            try {Thread.sleep(1000);} catch (InterruptedException ignored) {}
        }
    }


}
