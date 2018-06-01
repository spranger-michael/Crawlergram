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
import org.telegram.api.dialog.TLDialog;
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
     * writes participants
     * @param participants
     * @param dialog
     */
    void writeParticipants(TLObject participants, TLDialog dialog);

    /**
     * Writes messages from dialogs to DB (each dialog to a single collection)
     * @param absMessages
     * @param dialog
     */
    void writeTLAbsMessages(TLVector<TLAbsMessage> absMessages, TLDialog dialog);

    /**
     * max id of the message from a particular chat
     */
    Integer getMessageMaxId(TLDialog dialog);

    /**
     * min id of the message from a particular chat (for offset)
     */
    Integer getMessageMinId(TLDialog dialog);

    /**
     * date of min id message from a particular chat (for offset)
     */
    Integer getMessageMinIdDate(TLDialog dialog);
}
