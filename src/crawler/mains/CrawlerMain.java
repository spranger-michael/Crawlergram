/*
 * Title: CrawlerMain.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

/**
 * Connects to The telegram, gets dialogs, saves messages and documents to DB
 */

package crawler.mains;

import crawler.db.DBStorage;
import crawler.db.mongo.MongoDBStorage;
import crawler.implementation.apicallback.ApiCallbackImplemented;
import crawler.implementation.apimethods.AuthMethods;
import crawler.implementation.apimethods.DialogsHistoryMethods;
import crawler.implementation.apimethods.MessagesAndMediaToDB;
import crawler.implementation.logs.LogMethods;
import crawler.output.console.ConsoleOutputMethods;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.dialog.TLDialog;
import org.telegram.api.engine.ApiCallback;
import org.telegram.api.engine.AppInfo;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.engine.storage.AbsApiState;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.user.TLAbsUser;
import org.telegram.bot.kernel.engine.MemoryApiState;
import org.telegram.tl.TLVector;

import java.util.HashMap;
import java.util.Optional;

public class CrawlerMain {

    private static final int APIKEY = 0; // your api keys
    private static final String APIHASH = ""; // your api hash
    private static final String PHONENUMBER = ""; // your phone number


    public static void main(String[] args) {

        // DB "telegram" location - localhost:27017
        // User "telegramJ" - db.createUser({user: "telegramJ", pwd: "cart", roles: [{ role: "readWrite", db: "telegram" }]})
        DBStorage dbStorage = new MongoDBStorage("telegramJ", "telegram", "cart", "localhost", 27017, "fs");

        //register loggers
        LogMethods.registerLogs("logs");

        // api state
        AbsApiState apiState = new MemoryApiState("api.state");

        // app info set
        AppInfo appInfo = new AppInfo(APIKEY, "desktop", "Windows", "pre alpha 0.01", "en");

        // api callback methods
        ApiCallback apiCallback = new ApiCallbackImplemented();

        // init api
        TelegramApi api = new TelegramApi(apiState, appInfo, apiCallback);

        // set api state
        AuthMethods.setApiState(api, apiState);

        // do auth
        AuthMethods.auth(api, apiState, APIKEY, APIHASH, PHONENUMBER, Optional.<String>empty(), Optional.<String>empty());

        // dialogs, chats, users structures
        HashMap<Integer, TLAbsChat> chatsHashMap = new HashMap<>();
        HashMap<Integer, TLAbsUser> usersHashMap = new HashMap<>();
        TLVector<TLDialog> dialogs = new TLVector<>();
        //hashmap with top messages (needed for offsets)
        HashMap<Integer, TLAbsMessage> messagesHashMap = new HashMap<>();

        // get all dialogs of user (telegram returns 100 dialogs at maximum, getting by slices)
        DialogsHistoryMethods.getDialogsChatsUsers(api, dialogs, chatsHashMap, usersHashMap, messagesHashMap);

        // output to console
        ConsoleOutputMethods.testChatsHashMapOutputConsole(chatsHashMap);
        ConsoleOutputMethods.testUsersHashMapOutputConsole(usersHashMap);

        // parameters
        int messagesLimit = 300; // maximum number of retrieved messages from each dialog (0 if all)
        int participantsLimit = 0; // maximum number of retrieved participants from each dialog (0 if all)
        int filter = 0; // participants filter: 0 - recent, 1 - admins, 2 - kicked, 3 - bots, default - recent

        //Saves messages and media and outputs messages in console:
        MessagesAndMediaToDB.saveMessagesOnlyToDB(api, dbStorage, dialogs, chatsHashMap, usersHashMap, messagesHashMap, messagesLimit, participantsLimit, filter);

        //mongo.dbWriteMessageDocsHashMap(docsInDialogs);

        // stops the execution
        System.exit(0);
    }

}
