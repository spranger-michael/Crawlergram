/*
 * Title: CrawlerMain.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

/**
 * Connects to The telegram, gets dialogs, saves messages and documents to DB
 */

package crawler.mains;

import crawler.db.mongo.MongoStorage;
import crawler.implementation.apicallback.ApiCallbackImplemented;
import crawler.implementation.apimethods.AuthMethods;
import crawler.implementation.apimethods.DialogsHistoryMethods;
import crawler.implementation.apimethods.MessagesGetMediaMethods;
import crawler.implementation.logs.LogMethods;
import crawler.output.console.ConsoleOutputMethods;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.dialog.TLDialog;
import org.telegram.api.engine.ApiCallback;
import org.telegram.api.engine.AppInfo;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.engine.storage.AbsApiState;
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
        MongoStorage mongo = new MongoStorage("telegramJ", "telegram", "cart", "localhost", 27017);

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

        // get all dialogs of user (telegram returns 100 dialogs at maximum, getting by slices)
        DialogsHistoryMethods.getDialogsChatsUsers(api, dialogs, chatsHashMap, usersHashMap);
        // output to console
        ConsoleOutputMethods.testChatsHashMapOutputConsole(chatsHashMap);
        ConsoleOutputMethods.testUsersHashMapOutputConsole(usersHashMap);

        // all dialogs
        int messagesLimit = 1000; // maximum number of retrieved messages from each dialog

        //Saves messages and media and outputs messages in console:
        MessagesGetMediaMethods.saveMediaFromDialogsMessages(api, dialogs, chatsHashMap, usersHashMap, messagesLimit, "media");

        //mongo.dbWriteMessageDocsHashMap(docsInDialogs);

        // stops the execution
        System.exit(0);
    }

}
