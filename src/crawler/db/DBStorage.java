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
import org.telegram.tl.TLObject;
import org.telegram.tl.TLVector;

import java.util.HashMap;

public interface DBStorage {

    /**
     * Sets target of writing or reading (table, collection, etc.) in db
     * @param target
     */
    void setTarget(String target);

    /**
     * Drops target table, collection, etc. in db
     * @param target
     */
    void dropTarget(String target);

    /**
     * Drops current db
     */
    void dropDatabase();

    /**
     * write object to db
     * @param obj
     */
    void write(Object obj);

    /**
     * upsert object into db
     * @param obj
     */
    void upsert(Object obj);

    /**
     * writes full dialog to db
     * @param dial
     * @param chatsHashMap
     * @param usersHashMap
     */
    void writeFullDialog(TLObject dial, HashMap<Integer, TLAbsChat> chatsHashMap, HashMap<Integer, TLAbsUser> usersHashMap);

    /**
     * writes users hashmap to db
     * @param usersHashMap
     */
    void writeUsersHashMap(HashMap<Integer, TLAbsUser> usersHashMap);

    /**
     * writes chats hashmap to db
     * @param chatsHashMap
     */
    void writeChatsHashMap(HashMap<Integer, TLAbsChat> chatsHashMap);

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





}
