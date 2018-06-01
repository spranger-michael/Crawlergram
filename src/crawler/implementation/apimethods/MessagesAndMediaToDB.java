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
     * @param   messagesHashMap top messages
     * @param   msgLimit   maximum number of retrieved messages from each dialog (0 if all )
     * @param   parLimit   maximum number of retrieved participants from each dialog (0 if all)
     * @param   filter  participants filter: 0 - recent, 1 - admins, 2 - kicked, 3 - bots, default - recent
     */
    public static void saveOnlyMessagesToDB(TelegramApi api, DBStorage dbStorage, TLVector<TLDialog> dialogs,
                                            HashMap<Integer, TLAbsChat> chatsHashMap,
                                            HashMap<Integer, TLAbsUser> usersHashMap,
                                            HashMap<Integer, TLAbsMessage> messagesHashMap,
                                            int msgLimit, int parLimit, int filter) {
        for (TLDialog dialog : dialogs) {
            //reads full dialog info
            TLObject fullDialog = DialogsHistoryMethods.getFullDialog(api, dialog, chatsHashMap, usersHashMap);
            //writes full dialog info
            dbStorage.writeFullDialog(fullDialog, chatsHashMap, usersHashMap);

            //reads participants
            TLObject participants = DialogsHistoryMethods.getParticipants(api, fullDialog, chatsHashMap, usersHashMap, parLimit, filter);
            // writes participants of the dialog to "messages + [dialog_id]" table/collection/etc.
            dbStorage.writeParticipants(participants, dialog);

            //reads the messages
            TLVector<TLAbsMessage> absMessages = DialogsHistoryMethods.getWholeMessagesHistory(api, dialog, chatsHashMap, usersHashMap, messagesHashMap, msgLimit);
            // writes messages of the dialog to "messages + [dialog_id]" table/collection/etc.
            dbStorage.writeTLAbsMessages(absMessages, dialog);
            System.err.println(dialog.getPeer().getId()+ " "+ absMessages.size());

            // sleep between transmissions to avoid flood wait
            try {Thread.sleep(1000);} catch (InterruptedException ignored) {}
        }
        // write hashmaps
        dbStorage.writeUsersHashMap(usersHashMap);
        dbStorage.writeChatsHashMap(chatsHashMap);
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
