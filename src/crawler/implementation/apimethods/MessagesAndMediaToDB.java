/*
 * Title: messagesAndMediaToDB.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

package crawler.implementation.apimethods;

import crawler.output.console.ConsoleOutputMethods;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.dialog.TLDialog;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.user.TLAbsUser;
import org.telegram.tl.TLVector;

import java.util.HashMap;

public class MessagesAndMediaToDB {

    public static void saveMessagesMediaToDB(TelegramApi api, TLVector<TLDialog> dialogs, HashMap<Integer, TLAbsChat> chatsHashMap, HashMap<Integer, TLAbsUser> usersHashMap, HashMap<Integer, TLAbsMessage> messagesHashMap, int limit) {
        for (TLDialog dialog : dialogs) {
            // make actions upon each message in loop
            TLVector<TLAbsMessage> messages = DialogsHistoryMethods.getWholeMessagesHistory(api, dialog, chatsHashMap, usersHashMap, messagesHashMap, limit);
            for (TLAbsMessage message : messages) {

                //TODO redo downloads

                // write the message content in console
                //ConsoleOutputMethods.testMessageOutputConsole(message);
                // save message media to file
                //messageDownloadMedia(api, message, path);
            }

            System.err.println(dialog.getPeer().getId()+ " "+ messages.size());

            // sleep between transmissions to avoid flood wait
            try {Thread.sleep(1000);} catch (InterruptedException ignored) {}
        }
    }

}
