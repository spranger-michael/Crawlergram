/*
 * Title: DBStorage.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

/*
 * Title: DBStorage.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

package crawler.db;

import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.media.TLAbsMessageMedia;
import org.telegram.api.user.TLAbsUser;
import org.telegram.tl.TLVector;

import java.util.HashMap;

public interface DBStorage {

    /**
     * Sets target of writing or reading (table, collection, etc.) in db
     * @param target
     */
    void setTarget(String target);

    /**
     * Writes message from dialogs to DB (each dialog to a single collection)
     * @param absMessage
     */
    void writeTLAbsMessage(TLAbsMessage absMessage);

    /**
     * Writes messages from dialogs to DB (each dialog to a single collection)
     * @param absMessages
     */
    void writeTLAbsMessages(TLVector<TLAbsMessage> absMessages);

    /**
     * Writes media to DB
     * @param absMedia
     */
    void writeTLAbsMessageMedia(TLAbsMessageMedia absMedia);

    /**
     * Writes users to DB
     * @param usersHashMap
     */
    void writeTLAbsUsers(HashMap<Integer, TLAbsUser> usersHashMap);

    /**
     * Writes chats to DB
     * @param chatsHashMap
     */
    void writeTLAbsChats(HashMap<Integer, TLAbsChat> chatsHashMap);
}
