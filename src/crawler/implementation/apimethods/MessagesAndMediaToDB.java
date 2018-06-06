/*
 * Title: messagesAndMediaToDB.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

package crawler.implementation.apimethods;

import crawler.db.DBStorage;
import crawler.db.MessageHistoryExclusions;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.dialog.TLDialog;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.user.TLAbsUser;
import org.telegram.tl.TLObject;
import org.telegram.tl.TLVector;
import sun.plugin2.message.Message;

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
     * @param   maxDate max date of diapason for saving
     * @param   minDate min date of diapason for saving
     */
    public static void saveOnlyMessagesToDB(TelegramApi api, DBStorage dbStorage, TLVector<TLDialog> dialogs,
                                            HashMap<Integer, TLAbsChat> chatsHashMap,
                                            HashMap<Integer, TLAbsUser> usersHashMap,
                                            HashMap<Integer, TLAbsMessage> messagesHashMap,
                                            int msgLimit, int parLimit, int filter, int maxDate, int minDate) {
        for (TLDialog dialog : dialogs) {

            MessageHistoryExclusions exclusions = new MessageHistoryExclusions(dbStorage, dialog);

            //reads full dialog info
            TLObject fullDialog = DialogsHistoryMethods.getFullDialog(api, dialog, chatsHashMap, usersHashMap);
            //writes full dialog info
            dbStorage.writeFullDialog(fullDialog, chatsHashMap, usersHashMap);

            //reads participants
            TLObject participants = DialogsHistoryMethods.getParticipants(api, fullDialog, chatsHashMap, usersHashMap, parLimit, filter);
            // writes participants of the dialog to "messages + [dialog_id]" table/collection/etc.
            dbStorage.writeParticipants(participants, dialog);

            //reads the messages
            TLAbsMessage topMessage = DialogsHistoryMethods.getTopMessage(dialog, messagesHashMap);
            TLVector<TLAbsMessage> absMessages;
            if (exclusions.exist()){
                absMessages = DialogsHistoryMethods.getWholeMessagesHistoryWithExclusions(api, dialog, chatsHashMap, usersHashMap, topMessage, exclusions, msgLimit, maxDate, minDate);
            } else {
                absMessages = DialogsHistoryMethods.getWholeMessagesHistory(api, dialog, chatsHashMap, usersHashMap, topMessage, msgLimit, maxDate, minDate);
            }
            // writes messages of the dialog to "messages + [dialog_id]" table/collection/etc.
            dbStorage.writeTLAbsMessages(absMessages, dialog);

            // sleep between transmissions to avoid flood wait
            try {Thread.sleep(1000);} catch (InterruptedException ignored) {}
        }
        // write hashmaps
        dbStorage.writeUsersHashMap(usersHashMap);
        dbStorage.writeChatsHashMap(chatsHashMap);
    }

    /**
     * Writes only messages to DB
     * @param	api  TelegramApi instance for RPC request
     * @param   dbStorage   database instance
     * @param   dialogs dialogs TLVector
     * @param   chatsHashMap    chats hashmap
     * @param   usersHashMap    users hashmap
     * @param   messagesHashMap top messages
     * @param   msgLimit   maximum number of retrieved messages from each dialog (0 if all )
     * @param   maxDate max date of diapason for saving
     * @param   minDate min date of diapason for saving
     */
    public static void saveOnlyMediaToDB(TelegramApi api, DBStorage dbStorage, TLVector<TLDialog> dialogs,
                                            HashMap<Integer, TLAbsChat> chatsHashMap,
                                            HashMap<Integer, TLAbsUser> usersHashMap,
                                            HashMap<Integer, TLAbsMessage> messagesHashMap,
                                            int msgLimit, int maxDate, int minDate) {
        for (TLDialog dialog : dialogs) {

            MessageHistoryExclusions exclusions = new MessageHistoryExclusions(dbStorage, dialog);

            //reads the messages
            TLAbsMessage topMessage = DialogsHistoryMethods.getTopMessage(dialog, messagesHashMap);
            TLVector<TLAbsMessage> absMessages;
            if (exclusions.exist()){
                absMessages = DialogsHistoryMethods.getWholeMessagesHistoryWithExclusions(api, dialog, chatsHashMap, usersHashMap, topMessage, exclusions, msgLimit, maxDate, minDate);
            } else {
                absMessages = DialogsHistoryMethods.getWholeMessagesHistory(api, dialog, chatsHashMap, usersHashMap, topMessage, msgLimit, maxDate, minDate);
            }


            for (TLAbsMessage absMessage: absMessages){
                MediaDownloadMethods.messageDownloadMedia(api, absMessage, 1048576);
            }

            // sleep between transmissions to avoid flood wait
            try {Thread.sleep(1000);} catch (InterruptedException ignored) {}
        }
    }

}
