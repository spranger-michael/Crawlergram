/*
 * Title: CrawlerMain.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

package crawler.mains;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import crawler.db.mongo.MongoDBStorage;
import crawler.implementation.apicallback.ApiCallbackImplemented;
import crawler.implementation.apimethods.*;
import crawler.implementation.structures.DataStructuresMethods;
import crawler.output.console.ConsoleOutputMethods;
import crawler.implementation.structures.MessageDoc;
import crawler.output.files.FilesMethods;
import crawler.implementation.logs.MTProtoLoggerInterfaceImplemented;
import crawler.implementation.logs.ApiLoggerInterfaceImplemented;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.dialog.TLDialog;
import org.telegram.api.engine.*;
import org.telegram.api.engine.storage.AbsApiState;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.user.TLAbsUser;
import org.telegram.bot.kernel.engine.MemoryApiState;
import org.telegram.tl.TLVector;


public class CrawlerMainOld {

    private static final int APIKEY = 0; // your api keys
    private static final String APIHASH = ""; // your api hash
    private static final String PHONENUMBER = ""; // your phone number


    public static void main(String[] args) {

        // DB "telegram" location - localhost:27017
        // User "telegramJ" - db.createUser({user: "telegramJ", pwd: "cart", roles: [{ role: "readWrite", db: "telegram" }]})
        MongoDBStorage mongo = new MongoDBStorage("telegramJ", "telegram", "cart", "localhost", 27017, "fs");

        // create & check files for logs
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");
        Date date = new Date();
        String logfilePathApi = "logs" + File.separator + "apiLog_" + dateFormat.format(date) + ".log";
        String logfilePathMTProto = "logs" + File.separator + "MTProtoLog_" + dateFormat.format(date) + ".log";
        FilesMethods.checkFilePath(logfilePathApi);
        FilesMethods.checkFilePath(logfilePathMTProto);
        // init logs
        org.telegram.mtproto.log.Logger.registerInterface(new MTProtoLoggerInterfaceImplemented(logfilePathMTProto));
        org.telegram.api.engine.Logger.registerInterface(new ApiLoggerInterfaceImplemented(logfilePathApi));

        // apimethods state
        AbsApiState apiState = new MemoryApiState("api.state");
        // app info
        AppInfo appInfo = new AppInfo(APIKEY, "desktop", "Windows", "pre alpha 0.01", "en");
        // callback
        ApiCallback apiCallback = new ApiCallbackImplemented();

        // init apimethods
        TelegramApi api = new TelegramApi(apiState, appInfo, apiCallback);
        // set apimethods state
        AuthMethods.setApiState(api, apiState);

        // do auth
        AuthMethods.auth(api, apiState, APIKEY, APIHASH, PHONENUMBER, Optional.<String>empty(), Optional.<String>empty());

        // dialogs, chats, users structures
        HashMap<Integer, TLAbsChat> chatsHashMap = new HashMap<>();
        HashMap<Integer, TLAbsUser> usersHashMap = new HashMap<>();
        HashMap<Integer, TLAbsMessage> messagesHashMap = new HashMap<>();
        TLVector<TLDialog> dialogs = new TLVector<>();

        // get all dialogs of user (telegram returns 100 dialogs at maximum, getting by slices)
        DialogsHistoryMethods.getDialogsChatsUsers(api, dialogs, chatsHashMap, usersHashMap, messagesHashMap);

        ConsoleOutputMethods.testChatsHashMapOutputConsole(chatsHashMap);
        ConsoleOutputMethods.testUsersHashMapOutputConsole(usersHashMap);

        // all dialogs
        int messagesLimit = 1000; // maximum number of retrieved messages from each dialog
        int docThreshold = 50; // threshold between long and short chat

        /**
         * This line saves media and outputs messages in console:
         *     MessagesGetMediaMethods.saveMediaFromDialogsMessages(apimethods, dialogs, chatsHashMap, usersHashMap, messagesLimit, "downloaded docs");
         */
        MessagesGetMediaMethods.saveMediaFromDialogsMessages(api, dialogs, chatsHashMap, usersHashMap, messagesHashMap, messagesLimit, "media", 0, 0);

        /**
         * This line gets all the messages and saves them to the docs hashtable (empty docs are not saved, but used for calculations):
         *      HashMap<Integer, List<MessageDoc>> docsInDialogs = MessagesToDocsMethods.apiMessagesToDocuments(apimethods, dialogs, chatsHashMap, usersHashMap, messagesLimit, docThreshold);
         */
        HashMap<Integer, List<MessageDoc>> docsInDialogs = MessagesToDocsMethods.apiMessagesToDocuments(api, dialogs, chatsHashMap, usersHashMap, messagesLimit, docThreshold);
        // save HashMap to file (with additional preparation)
        ConsoleOutputMethods.testDocsInDialogsHashMapOutputConsole(docsInDialogs);
        DataStructuresMethods.removeDocsNewLine(docsInDialogs);
        DataStructuresMethods.saveDocsToFiles(docsInDialogs, "docs");

        mongo.dbWriteMessageDocsHashMap(docsInDialogs);

        // stops the execution
        System.exit(0);
    }
}