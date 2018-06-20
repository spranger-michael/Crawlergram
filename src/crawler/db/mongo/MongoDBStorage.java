/*
 * Title: MongoInterface.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

package crawler.db.mongo;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import crawler.db.Constants;
import crawler.db.DBStorage;
import crawler.implementation.structures.MessageDoc;
import org.bson.Document;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

import com.mongodb.client.model.Filters;
import org.bson.types.ObjectId;
import org.telegram.api.channel.TLChannelParticipants;
import org.telegram.api.channel.participants.*;
import org.telegram.api.chat.*;
import org.telegram.api.chat.channel.TLChannel;
import org.telegram.api.chat.channel.TLChannelForbidden;
import org.telegram.api.chat.channel.TLChannelFull;
import org.telegram.api.chat.invite.*;
import org.telegram.api.chat.participant.chatparticipant.TLAbsChatParticipant;
import org.telegram.api.chat.participant.chatparticipant.TLChatParticipant;
import org.telegram.api.chat.participant.chatparticipant.TLChatParticipantAdmin;
import org.telegram.api.chat.participant.chatparticipant.TLChatParticipantCreator;
import org.telegram.api.chat.participant.chatparticipants.TLChatParticipants;
import org.telegram.api.chat.photo.TLAbsChatPhoto;
import org.telegram.api.chat.photo.TLChatPhoto;
import org.telegram.api.chat.photo.TLChatPhotoEmpty;
import org.telegram.api.dialog.TLDialog;
import org.telegram.api.document.TLAbsDocument;
import org.telegram.api.document.TLDocument;
import org.telegram.api.document.TLDocumentEmpty;
import org.telegram.api.document.attribute.*;
import org.telegram.api.file.location.TLAbsFileLocation;
import org.telegram.api.file.location.TLFileLocation;
import org.telegram.api.file.location.TLFileLocationUnavailable;
import org.telegram.api.game.TLGame;
import org.telegram.api.geo.point.TLAbsGeoPoint;
import org.telegram.api.geo.point.TLGeoPoint;
import org.telegram.api.geo.point.TLGeoPointEmpty;
import org.telegram.api.input.chat.TLAbsInputChannel;
import org.telegram.api.input.chat.TLInputChannel;
import org.telegram.api.input.chat.TLInputChannelEmpty;
import org.telegram.api.message.*;
import org.telegram.api.message.action.*;
import org.telegram.api.message.media.*;
import org.telegram.api.messages.TLMessagesChatFull;
import org.telegram.api.paymentapi.TLPaymentCharge;
import org.telegram.api.paymentapi.TLPaymentRequestedInfo;
import org.telegram.api.paymentapi.TLPostAddress;
import org.telegram.api.peer.TLAbsPeer;
import org.telegram.api.peer.TLPeerChannel;
import org.telegram.api.peer.TLPeerChat;
import org.telegram.api.peer.TLPeerUser;
import org.telegram.api.phone.call.discardreason.*;
import org.telegram.api.photo.TLAbsPhoto;
import org.telegram.api.photo.TLPhoto;
import org.telegram.api.photo.TLPhotoEmpty;
import org.telegram.api.photo.size.TLAbsPhotoSize;
import org.telegram.api.photo.size.TLPhotoSize;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.api.user.TLUserEmpty;
import org.telegram.api.user.TLUserFull;
import org.telegram.api.webpage.TLAbsWebPage;
import org.telegram.api.webpage.TLWebPage;
import org.telegram.api.webpage.TLWebPageEmpty;
import org.telegram.tl.TLIntVector;
import org.telegram.tl.TLObject;
import org.telegram.tl.TLVector;

/**
 * Class for writing and reading data to and from MongoDB.
 */

public class MongoDBStorage implements DBStorage {

    public static final String DOC_DIAL_PREF = "dialog"; // OLD prefix for dialogs collections in DB
    static String user; // the user name
    static String db; // the name of the db in which the user is defined
    static String psw; // the psw
    static String host; // host
    static Integer port; // port
    static MongoCredential credential; // auth info
    static MongoClientOptions options; // client options
    static MongoClient mongoClient; // client instance
    static MongoDatabase database; // db instance
    static GridFSBucket gridFSBucket; // bucket for files
    static MongoCollection<Document> collection; //collection
    static boolean upsert; // upsert into DB? if false - regular write

    public MongoDBStorage(String user, String db, String psw, String host, Integer port, String gridFSBucketName){
        this.user = user;
        this.db = db;
        this.psw = psw;
        this.host = host;
        this.port = port;
        this.credential = MongoCredential.createCredential(user, db, psw.toCharArray());
        this.options = MongoClientOptions.builder().build();
        this.mongoClient = new MongoClient(new ServerAddress(host, port), credential, options);
        this.database = mongoClient.getDatabase(db);
        this.gridFSBucket = GridFSBuckets.create(this.database, gridFSBucketName);
        this.upsert = false;
    }

    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        MongoDBStorage.user = user;
    }

    public static String getDb() {
        return db;
    }

    public static void setDb(String db) {
        MongoDBStorage.db = db;
    }

    public static String getPsw() {
        return psw;
    }

    public static void setPsw(String psw) {
        MongoDBStorage.psw = psw;
    }

    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        MongoDBStorage.host = host;
    }

    public static Integer getPort() {
        return port;
    }

    public static void setPort(Integer port) {
        MongoDBStorage.port = port;
    }

    public static MongoCredential getCredential() {
        return credential;
    }

    public static void setCredential(MongoCredential credential) {
        MongoDBStorage.credential = credential;
    }

    public static MongoClientOptions getOptions() {
        return options;
    }

    public static void setOptions(MongoClientOptions options) {
        MongoDBStorage.options = options;
    }

    public static MongoClient getMongoClient() {
        return mongoClient;
    }

    public static void setMongoClient(MongoClient mongoClient) {
        MongoDBStorage.mongoClient = mongoClient;
    }

    public static MongoDatabase getDatabase() {
        return database;
    }

    public static void setDatabase(MongoDatabase database) {
        MongoDBStorage.database = database;
    }

    public static GridFSBucket getGridFSBucket() {
        return gridFSBucket;
    }

    public static void setGridFSBucket(GridFSBucket gridFSBucket) {
        MongoDBStorage.gridFSBucket = gridFSBucket;
    }

    public static MongoCollection<Document> getCollection() {
        return collection;
    }

    public static void setCollection(MongoCollection<Document> collection) {
        MongoDBStorage.collection = collection;
    }

    public static boolean isUpsert() {
        return upsert;
    }

    public static void setUpsert(boolean upsert) {
        MongoDBStorage.upsert = upsert;
    }

    public static void setDatabase(String db) {
        MongoDBStorage.database = mongoClient.getDatabase(db);
    }

    public static void setGridFSBucket(String gridFSBucketName) {
        MongoDBStorage.gridFSBucket = GridFSBuckets.create(database, gridFSBucketName);
    }

    public static void setCollection(String collName) {
        MongoDBStorage.collection = database.getCollection(collName);
    }

    /**
     * place to read/write
     * @param target target collection
     */
    @Override
    public void setTarget(String target) {
        try {
            collection = database.getCollection(target);
        } catch (MongoException e) {
            System.err.println(e.getCode() + " " + e.getMessage());
        }
    }

    /**
     * Drops target collection
     * @param target
     */
    @Override
    public void dropTarget(String target) {
        try{
            database.getCollection(target).drop();
        } catch (MongoException e){
            System.err.println(e.getCode() + " " + e.getMessage());
        }
    }

    /**
     * Drops current db
     */
    @Override
    public void dropDatabase() {
        try{
            database.drop();
        } catch (MongoException e){
            System.err.println(e.getCode() + " " + e.getMessage());
        }
    }

    /**
     * writes object to db
     * @param obj
     */
    @Override
    public void write(Object obj) {
        if (obj != null) {
            if (!isUpsert()) {
                try {
                    collection.insertOne((Document) obj);
                } catch (MongoException e) {
                    System.err.println(e.getCode() + " " + e.getMessage());
                }
            } else {
                try {
                    Document doc = (Document) obj;
                    UpdateResult uRes = collection.updateOne(Filters.eq("_id", doc.get("_id")), new Document("$set", doc), new UpdateOptions().upsert(true));
                } catch (MongoException e) {
                    System.err.println(e.getCode() + " " + e.getMessage());
                }

            }
        }
    }

    /**
     * Writes full dialog to db
     * @param dial object with dialog (chat/channel/user)
     * @param chatsHashMap prevents unnecessary downloading by getting data from HashMap
     * @param usersHashMap prevents unnecessary downloading by getting data from HashMap
     */
    @Override
    public void writeFullDialog(TLObject dial, HashMap<Integer, TLAbsChat> chatsHashMap, HashMap<Integer, TLAbsUser> usersHashMap){
        // set target
        this.setTarget(Constants.DIALOGS);
        // write it
        if (dial instanceof TLMessagesChatFull) {
            TLAbsChatFull absChatFull = ((TLMessagesChatFull) dial).getFullChat();
            //check if chat full or channel full
            if (absChatFull instanceof TLChannelFull) {
                this.write(tlChannelFullToDocument((TLChannelFull) absChatFull));
            } else if (absChatFull instanceof TLChatFull) {
                this.write(tlChatFullToDocument((TLChatFull) absChatFull));
            }
        } else if (dial instanceof TLUserFull) {
            this.write(tlUserFullToDocument((TLUserFull) dial));
        }
    }

    /**
     * writes users hashmap to db
     * @param usersHashMap hashmap
     */
    @Override
    public void writeUsersHashMap(HashMap<Integer, TLAbsUser> usersHashMap) {
        // set target
        this.setTarget(Constants.USERS_COL);
        // write
        Set<Integer> keys = usersHashMap.keySet();
        for (Integer key : keys) {
            TLAbsUser absUser = usersHashMap.get(key);
            this.write(tlAbsUserToDocument(absUser));
        }
    }

    /**
     * writes chats hashmap to db
     * @param chatsHashMap hashmap
     */
    @Override
    public void writeChatsHashMap(HashMap<Integer, TLAbsChat> chatsHashMap) {
        // set target
        this.setTarget(Constants.CHATS_COL);
        // write
        Set<Integer> keys = chatsHashMap.keySet();
        for (Integer key : keys) {
            TLAbsChat absChat = chatsHashMap.get(key);
            this.write(tlAbsChatToDocument(absChat));
        }
    }

    /**
     * writes participants to db
     * @param participants participants
     */
    @Override
    public void writeParticipants(TLObject participants, TLDialog dialog) {
        this.setTarget(Constants.PAR_DIAL_PREF + dialog.getPeer().getId());
        if (participants != null){
            if (participants instanceof TLChatParticipants){
                writeChatsParticipants(((TLChatParticipants) participants).getParticipants());
            } else if (participants instanceof TLUserFull){
                this.write(tlUserFullToDocument((TLUserFull) participants));
            } else if ((participants instanceof TLChannelParticipants)){
                if (((TLChannelParticipants) participants).getCount() > 0){
                    writeChannelParticipants(((TLChannelParticipants) participants).getParticipants());
                }
            }
        }
    }

    /**
     * Write messages to DB
     * @param absMessages messages
     * @param dialog dialog
     */
    @Override
    public void writeTLAbsMessages(TLVector<TLAbsMessage> absMessages, TLDialog dialog) {
        this.setTarget(Constants.MSG_DIAL_PREF + dialog.getPeer().getId());
        if ((absMessages != null) && (!absMessages.isEmpty())){
            try {
                for (TLAbsMessage absMessage : absMessages) {
                    writeTLAbsMessage(absMessage);
                }
            } catch (MongoException e) {
                System.err.println(e.getCode() + " " + e.getMessage());
            }
        }
    }

    /**
     * Write a single TLAbsMessage to DB
     * @param absMessage
     */
    @Override
    public void writeTLAbsMessage(TLAbsMessage absMessage){
        if (absMessage instanceof TLMessage){
            this.write(tlMessageToDocument((TLMessage) absMessage));
        } else if (absMessage instanceof TLMessageService){
            this.write(tlMessageServiceToDocument((TLMessageService) absMessage));
        } else if (absMessage instanceof TLMessageEmpty){
            this.write(new Document("class","MessageEmpty")
                    .append("_id",((TLMessageEmpty) absMessage).getId())
                    .append("chatId", absMessage.getChatId()));
        }
    }

    /**
     * Write messages to DB
     * @param absMessage messages
     * @param filePath path to the downloaded (reference)
     */
    @Override
    public void writeTLAbsMessageWithReference(TLAbsMessage absMessage, String filePath) {
        int id = -1;
        if (absMessage instanceof TLMessage){
            this.write(tlMessageToDocumentWithReference((TLMessage) absMessage, filePath));
            id = ((TLMessage) absMessage).getId();
        } else if (absMessage instanceof TLMessageService){
            this.write(tlMessageServiceToDocument((TLMessageService) absMessage));
            id = ((TLMessageService) absMessage).getId();
        } else if (absMessage instanceof TLMessageEmpty){
            this.write(new Document("class","MessageEmpty")
                    .append("_id",((TLMessageEmpty) absMessage).getId())
                    .append("chatId", absMessage.getChatId()));
            id = ((TLMessageEmpty) absMessage).getId();
        }
        System.out.println(id);
    }

    /**
     * max id of the message from a particular chat
     */
    @Override
    public Integer getMessageMaxId(TLDialog dialog){
        try {
            this.setTarget(Constants.MSG_DIAL_PREF + dialog.getPeer().getId());
            FindIterable<Document> findMax = collection.find().sort(descending("_id")).limit(1);
            Document docMax = findMax.first();
            return docMax != null ? (Integer) docMax.get("_id") : null;
        } catch (MongoException e) {
            System.err.println(e.getCode() + " " + e.getMessage());
            return null;
        }
    }

    /**
     * min id of the message from a particular chat (for offset)
     */
    @Override
    public Integer getMessageMinId(TLDialog dialog) {
        try {
            this.setTarget(Constants.MSG_DIAL_PREF + dialog.getPeer().getId());
            FindIterable<Document> findMin = collection.find().sort(ascending("_id")).limit(1);
            Document docMin = findMin.first();
            return docMin != null ? (Integer) docMin.get("_id") : null;
        } catch (MongoException e) {
            System.err.println(e.getCode() + " " + e.getMessage());
            return null;
        }
    }

    /**
     * date of min id message from a particular chat (for offset)
     */
    @Override
    public Integer getMessageMinIdDate(TLDialog dialog) {
        try {
            this.setTarget(Constants.MSG_DIAL_PREF + dialog.getPeer().getId());
            FindIterable<Document> findMin = collection.find().sort(ascending("_id")).limit(1);
            Document docMin = findMin.first();
            return docMin != null ? (Integer) docMin.get("date") : null;
        } catch (MongoException e) {
            System.err.println(e.getCode() + " " + e.getMessage());
            return null;
        }
    }

    /**
     * date of max id message from a particular chat (for offset)
     */
    @Override
    public Integer getMessageMaxIdDate(TLDialog dialog) {
        try {
            this.setTarget(Constants.MSG_DIAL_PREF + dialog.getPeer().getId());
            FindIterable<Document> findMax = collection.find().sort(descending("_id")).limit(1);
            Document docMax = findMax.first();
            return docMax != null ? (Integer) docMax.get("date") : null;
        } catch (MongoException e) {
            System.err.println(e.getCode() + " " + e.getMessage());
            return null;
        }
    }

    /**
     * writes bytes to GridFS
     * @param name filename
     * @param bytes contents
     */
    @Override
    public void writeFile(String name, byte[] bytes) {
        InputStream inputStream = new ByteArrayInputStream(bytes);
        // file type (last split)
        String[] split = name.split("\\.");
        String type = split[split.length-1];
        // 100kb chunks
        GridFSUploadOptions options = new GridFSUploadOptions().chunkSizeBytes(100*1024).metadata(new Document("type", type));
        ObjectId fileId = gridFSBucket.uploadFromStream(name, inputStream, options);
    }

    public List<String> getExistingCollections(){
        List<String> colNames = new ArrayList<>();
        MongoIterable<String> collections = database.listCollectionNames();
        for (String collection: collections){
            colNames.add(collection);
        }
        return colNames;
    }

    /**
     * reads all messages from DB for target collection
     * @param targetCollectionName target collection
     */
    public List<Document> readMessages(String targetCollectionName) {
        List<Document> msgs = new LinkedList<>();
        this.setTarget(targetCollectionName);
        FindIterable<Document> docs = collection.find().sort(descending("_id"));
        for (Document doc: docs){
            msgs.add(doc);
        }
        return msgs;
    }

    /**
     * reads messages between two dates from DB for target collection
     * @param targetCollectionName targetCollectionName collection
     * @param dateFrom start date date
     * @param dateTo end date
     */
    public List<Document> readMessages(String targetCollectionName, int dateFrom, int dateTo) {
        List<Document> msgs = new LinkedList<>();
        this.setTarget(targetCollectionName);
        FindIterable<Document> docs = collection.find(and(gte("date", dateFrom), lte("date", dateTo))).sort(descending("_id"));
        for (Document doc: docs){
            msgs.add(doc);
        }
        return msgs;
    }

    /**
     * Converts channel full to document
     * @param cf channel full
     */
    private static Document tlChannelFullToDocument(TLChannelFull cf){
        return new Document("_id", cf.getId())
                .append("class", "ChannelFull")
                .append("about", cf.getAbout())
                .append("adminCount", cf.getAdminCount())
                .append("migratedFromId", cf.getMigratedFromChatId())
                .append("pinnedMessageId", cf.getPinnedMessageId())
                .append("exportedInvite", tlAbsChatInviteToDocument(cf.getExportedInvite()));
    }

    /**
     * Converts chat full to document
     * @param cf chat full
     */
    private static Document tlChatFullToDocument(TLChatFull cf){
        return new Document("_id", cf.getId())
                .append("class", "ChatFull")
                .append("exportedInvite", tlAbsChatInviteToDocument(cf.getExportedInvite()));
    }

    /**
     * Converts abstract chat invite to document
     * @param aci abstract chat invite
     */
    private static Document tlAbsChatInviteToDocument(TLAbsChatInvite aci){
        if (aci instanceof TLChatInvite){
            return new Document("class", "ChatInvite")
                    .append("chatInvite", tlChatInviteToDocument((TLChatInvite) aci));
        } else if (aci instanceof TLChatInviteAlready){
            return new Document("class", "ChatInviteAlready")
                    .append("chat", tlAbsChatToDocument(((TLChatInviteAlready) aci).getChat()));
        } else if (aci instanceof TLChatInviteEmpty){
            return new Document("class", "ChatInviteEmpty");
        } else if (aci instanceof TLChatInviteExported){
            return new Document("class", "ChatInviteExported")
                    .append("link", ((TLChatInviteExported) aci).getLink());
        } else {
            return null;
        }
    }

    /**
     * Converts chat invite to document
     * @param ci chat invite
     */
    private static Document tlChatInviteToDocument(TLChatInvite ci){
        return new Document("title", ci.getTitle())
                .append("participantsCount", ci.getParticipantsCount())
                .append("photo", tlAbsPhotoToDocument(ci.getPhoto()))
                .append("participants", tlVectorTlAbsUserToDocument(ci.getParticipants()));
    }

    /**
     * Converts user full to document
     * @param uf user full
     */
    private static Document tlUserFullToDocument(TLUserFull uf){
        TLAbsUser au = uf.getUser();
        return new Document("_id",au.getId())
                .append("class", "UserFull")
                .append("about", uf.getAbout())
                .append("commonChatsCount", uf.getCommonChatsCount())
                .append("flags",uf.getFlags())
                .append("photo", tlAbsPhotoToDocument(uf.getProfilePhoto()))
                .append("user", tlAbsUserToDocument(au));
    }

    /**
     * Converts abstract user to document
     * @param au abstract user
     */
    private static Document tlAbsUserToDocument(TLAbsUser au){
        if (au instanceof TLUser){
            TLUser u = (TLUser) au;
            return new Document("class", "User")
                    .append("_id", u.getId())
                    .append("accessHash", u.getAccessHash())
                    .append("firstName",u.getFirstName())
                    .append("lastName",u.getLastName())
                    .append("userName",u.getUserName())
                    .append("flags",u.getFlags())
                    .append("langCode",u.getLangCode())
                    .append("phone",u.getPhone())
                    .append("botInfoVersion",u.getBotInfoVersion())
                    .append("botInlinePlaceholder",u.getBotInlinePlaceholder());
        } else if (au instanceof TLUserEmpty){
            TLUserEmpty u = (TLUserEmpty) au;
            return new Document("class", "UserEmpty").append("_id", u.getId());
        } else {
            return null;
        }
    }

    /**
     * Converts vector of abstract users to document
     * @param vau abstract user vector
     */
    private static List<Document> tlVectorTlAbsUserToDocument(TLVector<TLAbsUser> vau){
        List<Document> doc = new ArrayList<>();
        if ((!vau.isEmpty()) && (vau != null)){
            for (TLAbsUser au: vau){
                doc.add(tlAbsUserToDocument(au));
            }
        }
        return doc;
    }

    /**
     * Converts an abstract chat to Document
     * @param ac abstract chat
     */
    private static Document tlAbsChatToDocument(TLAbsChat ac){
        if (ac instanceof TLChannel){
            return new Document("class", "Channel")
                    .append("_id", ac.getId())
                    .append("accessHash", ((TLChannel) ac).getAccessHash())
                    .append("date", ((TLChannel) ac).getDate())
                    .append("flags", ((TLChannel) ac).getFlags())
                    .append("title", ((TLChannel) ac).getTitle())
                    .append("username", ((TLChannel) ac).getUsername())
                    .append("version", ((TLChannel) ac).getVersion())
                    .append("photo", tlAbsChatPhotoToDocument(((TLChannel) ac).getPhoto()));
        } else if (ac instanceof TLChannelForbidden){
            return new Document("class", "ChannelForbidden")
                    .append("accessHash", ((TLChannelForbidden) ac).getAccessHash())
                    .append("title", ((TLChannelForbidden) ac).getTitle())
                    .append("_id", ac.getId());
        } else if (ac instanceof TLChat){
            return new Document("class", "Chat")
                    .append("_id", ac.getId())
                    .append("date", ((TLChat) ac).getDate())
                    .append("flags", ((TLChat) ac).getFlags())
                    .append("participantsCount", ((TLChat) ac).getParticipantsCount())
                    .append("title", ((TLChat) ac).getTitle())
                    .append("version", ((TLChat) ac).getVersion())
                    .append("photo", tlAbsChatPhotoToDocument(((TLChat) ac).getPhoto()))
                    .append("migratedTo", tlAbsInputChannelToDocument(((TLChat) ac).getMigratedTo()));
        } else if (ac instanceof TLChatForbidden){
            return new Document("class", "ChatForbidden")
                    .append("title", ((TLChatForbidden) ac).getTitle())
                    .append("_id", ac.getId());
        } else if (ac instanceof TLChatEmpty){
            return new Document("class", "ChatEmpty")
                    .append("_id", ac.getId());
        } else {
            return null;
        }
    }

    /**
     * Converts abstract chat photo to document
     * @param acp abstract chat photo
     */
    private static Document tlAbsChatPhotoToDocument(TLAbsChatPhoto acp){
        if (acp instanceof TLChatPhoto){
            return new Document("class", "ChatPhoto")
                    .append("bigPhoto", tlAbsFileLocationToDocument(((TLChatPhoto) acp).getPhoto_big()))
                    .append("smallPhoto", tlAbsFileLocationToDocument(((TLChatPhoto) acp).getPhoto_small()));
        } else if (acp instanceof TLChatPhotoEmpty){
            return new Document("class", "ChatPhotoEmpty");
        } else {
            return null;
        }
    }

    /**
     * Converts file location to Document
     * @param afl abstract file location
     */
    private static Document tlAbsFileLocationToDocument(TLAbsFileLocation afl){
        if (afl instanceof TLFileLocation){
            return new Document("class", "FileLocation")
                    .append("secret", ((TLFileLocation) afl).getSecret())
                    .append("volumeId",((TLFileLocation) afl).getVolumeId())
                    .append("localId",((TLFileLocation) afl).getLocalId())
                    .append("dcId",((TLFileLocation) afl).getDcId());
        } else if (afl instanceof TLFileLocationUnavailable){
            return new Document("class", "FileLocationUnavailable");
        } else {
            return null;
        }
    }

    /**
     * Converts abstract input channel to document
     * @param aic abstract input channel
     */
    private static Document tlAbsInputChannelToDocument (TLAbsInputChannel aic){
        if (aic instanceof TLInputChannel){
            return new Document("class", "InputChannel")
                    .append("accessHash", ((TLInputChannel) aic).getAccessHash())
                    .append("_id", aic.getChannelId());
        } else if (aic instanceof TLInputChannelEmpty){
            return new Document("class", "InputChannelEmpty")
                    .append("_id",aic.getChannelId());
        } else {
            return null;
        }
    }

    /**
     * converts abs photo to doc
     * @param ap abs photo
     * @return doc
     */
    private static Document tlAbsPhotoToDocument(TLAbsPhoto ap){
        if (ap instanceof TLPhoto){
            return new Document("class", "Photo")
                    .append("_id", ((TLPhoto) ap).getId())
                    .append("accessHash", ((TLPhoto) ap).getAccessHash())
                    .append("date", ((TLPhoto) ap).getDate())
                    .append("location", getLargestPhotoLocation(((TLPhoto) ap).getSizes()));
        } else if (ap instanceof TLPhotoEmpty){
            return new Document("class", "PhotoEmpty")
                    .append("_id", ((TLPhotoEmpty) ap).getId());
        } else {
            return null;
        }
    }

    /**
     * gets the location of the largest (and last one in the list) accessible photo
     * @param apss abs photo size
     * @return doc
     */
    private static Document getLargestPhotoLocation(TLVector<TLAbsPhotoSize> apss){
        // getting the last and largest TLPhotoSize
        Document doc = null;
        TLPhotoSize aps;
        TLFileLocation psl;
        for (int i = apss.size()-1; i >= 0; i--){
            if (sizeAvailable(apss.get(i))){
                aps = (TLPhotoSize) apss.get(i);
                psl = (TLFileLocation) aps.getLocation();
                doc = new Document("class", "PhotoSize")
                        .append("size", aps.getSize())
                        .append("type", aps.getType())
                        .append("location", tlFileLocationToDocument(psl));
                break;
            }
        }
        return doc;
    }

    /**
     * checks if file location available
     * @param aps
     * @return
     */
    private static boolean sizeAvailable(TLAbsPhotoSize aps){
        boolean f = false;
        if (aps instanceof TLPhotoSize){
            if (((TLPhotoSize) aps).getLocation() instanceof TLFileLocation){
                f = true;
            }
        }
        return f;
    }


    /**
     * converts file location to doc
     * @param fl location
     * @return doc
     */
    private static Document tlFileLocationToDocument(TLFileLocation fl){
        return new Document("class", "FileLocation")
                .append("dcId", fl.getDcId())
                .append("localId", fl.getLocalId())
                .append("volumeId", fl.getVolumeId())
                .append("secret", fl.getSecret());
    }

    /**
     * writes channel participants
     * @param vacp participants vector
     */
    private void writeChannelParticipants(TLVector<TLAbsChannelParticipant> vacp){
        for (TLAbsChannelParticipant acp: vacp){
            this.write(tlAbsChannelParticipantToDocument(acp));
        }
    }

    /**
     * converts participant to document
     * @param acp participant
     */
    private static Document tlAbsChannelParticipantToDocument(TLAbsChannelParticipant acp){
        if (acp instanceof TLChannelParticipant){
            return new Document("class", "ChannelParticipant")
                    .append("_id",((TLChannelParticipant) acp).getUserId())
                    .append("date", ((TLChannelParticipant) acp).getDate());
        } else if (acp instanceof TLChannelParticipantSelf){
            return new Document("class", "ChannelParticipantSelf")
                    .append("_id", ((TLChannelParticipantSelf) acp).getUserId())
                    .append("date", ((TLChannelParticipantSelf) acp).getDate())
                    .append("inviterId", ((TLChannelParticipantSelf) acp).getInviterId());
        } else if (acp instanceof TLChannelParticipantModerator){
            return new Document("class", "ChannelParticipantModerator")
                    .append("_id", ((TLChannelParticipantModerator) acp).getUserId())
                    .append("date", ((TLChannelParticipantModerator) acp).getDate())
                    .append("inviterId", ((TLChannelParticipantModerator) acp).getInviterId());
        } else if (acp instanceof TLChannelParticipantKicked){
            return new Document("class", "ChannelParticipantKicked")
                    .append("_id", ((TLChannelParticipantKicked) acp).getUserId())
                    .append("date", ((TLChannelParticipantKicked) acp).getDate())
                    .append("kickedBy", ((TLChannelParticipantKicked) acp).getKickedBy());
        } else if (acp instanceof TLChannelParticipantEditor){
            return new Document("class", "ChannelParticipantEditor")
                    .append("_id", ((TLChannelParticipantEditor) acp).getUserId())
                    .append("date", ((TLChannelParticipantEditor) acp).getDate())
                    .append("inviterId", ((TLChannelParticipantEditor) acp).getInviterId());
        } else if (acp instanceof TLChannelParticipantCreator){
            return new Document("class", "ChannelParticipantCreator")
                    .append("_id", ((TLChannelParticipantCreator) acp).getUserId());
        } else {
            return null;
        }
    }

    /**
     * writes chat participants
     * @param vacp participants vector
     */
    private void writeChatsParticipants(TLVector<TLAbsChatParticipant> vacp){
        for (TLAbsChatParticipant acp: vacp){
            this.write(tlAbsChatParticipantToDocument(acp));
        }
    }

    /**
     * converts participant to document
     * @param acp participant
     */
    private static Document tlAbsChatParticipantToDocument(TLAbsChatParticipant acp){
        if (acp instanceof TLChatParticipant){
            return new Document("class", "ChatParticipant")
                    .append("_id", acp.getUserId())
                    .append("date", ((TLChatParticipant) acp).getDate())
                    .append("inviterId", ((TLChatParticipant) acp).getInviterId());
        } else if (acp instanceof TLChatParticipantAdmin){
            return new Document("class", "ChatParticipantAdmin")
                    .append("_id", acp.getUserId())
                    .append("date", ((TLChatParticipantAdmin) acp).getDate())
                    .append("inviterId", ((TLChatParticipantAdmin) acp).getInviterId());
        } else if (acp instanceof TLChatParticipantCreator){
            return new Document("class", "ChatParticipantCreator")
                    .append("_id", acp.getUserId());
        } else {
            return null;
        }
    }

    /**
     * converts message to document
     * @param m message
     */
    private static Document tlMessageToDocument(TLMessage m){
        return new Document("_id", m.getId())
                .append("class", "Message")
                .append("flags", m.getFlags())
                .append("fromId", m.getFromId())
                .append("toId", tlAbsPeerToDocument(m.getToId()))
                .append("fwdFrom", tlMsgFwdHeaderToDocument(m.getFwdFrom()))
                .append("viaBotId", m.getViaBotId())
                .append("replyToMsgId", m.getReplyToMsgId())
                .append("date", m.getDate())
                .append("message", m.getMessage())
                .append("media", tlAbsMessageMediaToDocument(m.getMedia()))
                .append("views", m.getViews())
                .append("editDate", m.getEditDate());
    }

    /**
     * converts message to document with reference
     * @param m message
     */
    private static Document tlMessageToDocumentWithReference(TLMessage m, String filePath){
        return new Document("_id", m.getId())
                .append("class", "Message")
                .append("flags", m.getFlags())
                .append("fromId", m.getFromId())
                .append("toId", tlAbsPeerToDocument(m.getToId()))
                .append("fwdFrom", tlMsgFwdHeaderToDocument(m.getFwdFrom()))
                .append("viaBotId", m.getViaBotId())
                .append("replyToMsgId", m.getReplyToMsgId())
                .append("date", m.getDate())
                .append("message", m.getMessage())
                .append("media", tlAbsMessageMediaToDocument(m.getMedia()))
                .append("views", m.getViews())
                .append("editDate", m.getEditDate())
                .append("mediaReference", filePath);
    }

    /**
     * converts abstract peer to doc
     * @param ap abstract peer
     * @return doc
     */
    private static Document tlAbsPeerToDocument(TLAbsPeer ap){
        if (ap instanceof TLPeerUser){
            return new Document("class", "PeerUser")
                    .append("_id", ap.getId());
        } else if (ap instanceof TLPeerChannel){
            return new Document("class", "PeerChannel")
                    .append("_id", ap.getId());
        } else if (ap instanceof TLPeerChat){
            return new Document("class", "PeerChat")
                    .append("_id", ap.getId());
        } else {
            return null;
        }
    }

    /**
     * converts forward header to doc
     * @param fh forward header
     * @return doc
     */
    private static Document tlMsgFwdHeaderToDocument(TLMessageFwdHeader fh){
        if (fh != null) {
            return new Document("class", "MessageFwdHeader")
                    .append("fromId", fh.getFromId())
                    .append("date", fh.getDate())
                    .append("channelId", fh.getChannelId())
                    .append("channelPost", fh.getChannelPost());
        } else{
            return null;
        }
    }

    /**
     * converts media to doc
     * @param amm
     * @return doc
     */
    private static Document tlAbsMessageMediaToDocument(TLAbsMessageMedia amm){
        if (amm instanceof TLMessageMediaContact) {
            return new Document("class", "MessageMediaContact")
                    .append("_id", ((TLMessageMediaContact) amm).getUserId())
                    .append("firstName",((TLMessageMediaContact) amm).getFirstName())
                    .append("lastName", ((TLMessageMediaContact) amm).getLastName())
                    .append("phone", ((TLMessageMediaContact) amm).getPhoneNumber());

        } else if (amm instanceof TLMessageMediaDocument) {
            return new Document("class", "MessageMediaDocument")
                    .append("caption", ((TLMessageMediaDocument) amm).getCaption())
                    .append("document", tlAbsDocumentToDocument(((TLMessageMediaDocument) amm).getDocument()));

        } else if (amm instanceof TLMessageMediaEmpty) {
            return new Document("class", "MessageMediaEmpty");

        } else if (amm instanceof TLMessageMediaGame) {
            return new Document("class", "MessageMediaGame")
                    .append("game", tlGameToDocument(((TLMessageMediaGame) amm).getGame()));

        } else if (amm instanceof TLMessageMediaGeo) {
            return new Document("class", "MessageMediaGeo")
                    .append("geo", tlGeoPointToDocument(((TLMessageMediaGeo) amm).getGeo()));

        } else if (amm instanceof TLMessageMediaPhoto) {
            return new Document("class", "MessageMediaPhoto")
                    .append("caption", ((TLMessageMediaPhoto) amm).getCaption())
                    .append("photo", tlAbsPhotoToDocument(((TLMessageMediaPhoto) amm).getPhoto()));

        } else if (amm instanceof TLMessageMediaUnsupported) {
            return new Document("class", "MessageMediaUnsupported");

        } else if (amm instanceof TLMessageMediaVenue) {
            return new Document("class", "MessageMediaVenue")
                    .append("id", ((TLMessageMediaVenue) amm).getVenue_id())
                    .append("address", ((TLMessageMediaVenue) amm).getAddress())
                    .append("provider", ((TLMessageMediaVenue) amm).getProvider())
                    .append("title", ((TLMessageMediaVenue) amm).getTitle())
                    .append("geo", tlGeoPointToDocument(((TLMessageMediaVenue) amm).getGeo()));

        } else if (amm instanceof TLMessageMediaWebPage) {
            return new Document("class", "MessageMediaWebPage")
                    .append("web", (tlWebPageToDocument(((TLMessageMediaWebPage) amm).getWebPage())));

        } else if (amm instanceof TLMessageMediaInvoice) {
            return new Document("class", "MessageMediaInvoice")
                    .append("title", ((TLMessageMediaInvoice) amm).getTitle())
                    .append("amount", ((TLMessageMediaInvoice) amm).getTotalAmount())
                    .append("currency", ((TLMessageMediaInvoice) amm).getCurrency())
                    .append("description", ((TLMessageMediaInvoice) amm).getDescription())
                    .append("msgId", ((TLMessageMediaInvoice) amm).getReceiptMsgId())
                    .append("startParam", ((TLMessageMediaInvoice) amm).getStartParam());
        } else {
            return null;
        }
    }

    /**
     * converts geo point to doc
     * @param agp geo point
     * @return doc
     */
    private static Document tlGeoPointToDocument(TLAbsGeoPoint agp){
        if (agp instanceof TLGeoPoint){
            return new Document("class", "GeoPoint")
                    .append("lat", ((TLGeoPoint) agp).getLat())
                    .append("lon", ((TLGeoPoint) agp).getLon());
        } else if (agp instanceof TLGeoPointEmpty) {
            return new Document("class", "GeoPointEmpty");
        } else {
            return null;
        }
    }

    /**
     * converts abs doc to doc
     * @param ad abs doc
     * @return doc
     */
    private static Document tlAbsDocumentToDocument(TLAbsDocument ad){
        if (ad instanceof TLDocument){
            return new Document("class", "Document")
                    .append("_id", ad.getId())
                    .append("accessHash", ((TLDocument) ad).getAccessHash())
                    .append("dcId", ((TLDocument) ad).getDcId())
                    .append("date", ((TLDocument) ad).getDate())
                    .append("mimeType", ((TLDocument) ad).getMimeType())
                    .append("size", ((TLDocument) ad).getSize())
                    .append("version", ((TLDocument) ad).getVersion())
                    // only file name & no thumb
                    .append("filename", tlAbsDocumentAttributesToName(((TLDocument) ad).getAttributes(), (TLDocument) ad));
        } else if (ad instanceof TLDocumentEmpty) {
            return new Document("class", "DocumentEmpty")
                    .append("_id", ad.getId());
        } else {
            return null;
        }
    }

    /**
     * converts doc attrs to name of the doc
     */
    private static String tlAbsDocumentAttributesToName(TLVector<TLAbsDocumentAttribute> adas, TLDocument doc) {
        String name = "";
        for (TLAbsDocumentAttribute ada: adas) {
            if (ada instanceof TLDocumentAttributeFilename) {
                name = ((TLDocumentAttributeFilename) ada).getFileName();
            }
        }
        if (name.isEmpty()) {
            name = doc.getId()+"_document";
            for (TLAbsDocumentAttribute attr : adas) {
                if (attr instanceof TLDocumentAttributeAudio) {
                    name = doc.getDate()+"_"+doc.getId()+".ogg"; // audio message
                    return name;
                } else if (attr instanceof TLDocumentAttributeVideo) {
                    name = doc.getDate()+"_"+doc.getId()+".mp4"; // video message
                    return name;
                } else if (attr instanceof TLDocumentAttributeAnimated) {
                    name = doc.getDate()+"_"+doc.getId()+".gif"; // //gif
                    return name;
                } else if (attr instanceof TLDocumentAttributeSticker) {
                    name = doc.getDate()+"_"+doc.getId()+".webp";
                    return name;
                }
            }
        }
        return name;
    }

    /**
     * converts game to doc
     * @param g game
     * @return doc
     */
    private static Document tlGameToDocument(TLGame g){
        return new Document("class", "Game")
                .append("_id", g.getId())
                .append("name", g.getShortName())
                .append("title", g.getTitle())
                .append("accessHash", g.getAccessHash())
                .append("description", g.getDescription())
                .append("document", tlAbsDocumentToDocument(g.getDocument()));
    }

    /**
     * converts web page to document
     * @param wp web page
     */
    private static Document tlWebPageToDocument(TLAbsWebPage wp){
        if (wp instanceof TLWebPage){
            return new Document("class", "WebPage")
                    .append("title", ((TLWebPage) wp).getTitle())
                    .append("url", ((TLWebPage) wp).getUrl())
                    .append("site", ((TLWebPage) wp).getSite_name())
                    .append("_id", ((TLWebPage) wp).getId())
                    .append("description", ((TLWebPage) wp).getDescription())
                    .append("author", ((TLWebPage) wp).getAuthor())
                    .append("duration", ((TLWebPage) wp).getDuration())
                    .append("type", ((TLWebPage) wp).getType())
                    .append("photo", tlAbsPhotoToDocument(((TLWebPage) wp).getPhoto()))
                    .append("document", tlAbsDocumentToDocument(((TLWebPage) wp).getDocument()))
                    .append("hash", ((TLWebPage) wp).getHash());
        } else if (wp instanceof TLWebPageEmpty) {
            return new Document("class", "WebPageEmpty");
        } else {
            return null;
        }
    }

    /**
     * converts service message to document
     * @param ms
     */
    private static Document tlMessageServiceToDocument(TLMessageService ms){
        return new Document("class", "MessageService")
                .append("_id", ms.getId())
                .append("date", ms.getDate())
                .append("chatId", ms.getChatId())
                .append("flags", ms.getFlags())
                .append("fromId", ms.getFromId())
                .append("toId", tlAbsPeerToDocument(ms.getToId()))
                .append("replyToMsgId", ms.getReplyToMessageId())
                .append("action", tlAbsMessageActionToDocument(ms.getAction()));
    }

    private static Document tlAbsMessageActionToDocument(TLAbsMessageAction ama){
        if (ama instanceof TLMessageActionEmpty){
            return new Document("class", "MessageActionEmpty");
        } else if (ama instanceof TLMessageActionChannelCreate){
            return new Document("class", "MessageActionChannelCreate")
                    .append("title", ((TLMessageActionChannelCreate) ama).getTitle());
        } else if (ama instanceof TLMessageActionChannelMigratedFrom){
            return new Document("class", "MessageActionChannelMigratedFrom")
                    .append("title",((TLMessageActionChannelMigratedFrom) ama).getTitle())
                    .append("chatId",((TLMessageActionChannelMigratedFrom) ama).getChatId());
        } else if (ama instanceof TLMessageActionChatAddUser){
            return new Document("class", "MessageActionChatAddUser")
                    .append("users", tlIntVectorToList(((TLMessageActionChatAddUser) ama).getUsers()));
        } else if (ama instanceof TLMessageActionChatCreate){
            return new Document("class", "MessageAction")
                    .append("title",((TLMessageActionChatCreate) ama).getTitle())
                    .append("users", tlIntVectorToList(((TLMessageActionChatCreate) ama).getUsers()));
        } else if (ama instanceof TLMessageActionChatDeletePhoto){
            return new Document("class", "MessageActionChatDeletePhoto");
        } else if (ama instanceof TLMessageActionChatDeleteUser){
            return new Document("class", "MessageActionChatDeleteUser")
                    .append("user",((TLMessageActionChatDeleteUser) ama).getUserId());
        } else if (ama instanceof TLMessageActionChatEditPhoto){
            return new Document("class", "MessageActionChatEditPhoto")
                    .append("photo",tlAbsPhotoToDocument(((TLMessageActionChatEditPhoto) ama).getPhoto()));
        } else if (ama instanceof TLMessageActionChatEditTitle){
            return new Document("class", "MessageActionChatEditTitle")
                    .append("title",((TLMessageActionChatEditTitle) ama).getTitle());
        } else if (ama instanceof TLMessageActionChatJoinedByLink){
            return new Document("class", "MessageActionChatJoinedByLink")
                    .append("inviterId",((TLMessageActionChatJoinedByLink) ama).getInviterId());
        } else if (ama instanceof TLMessageActionGameScore){
            return new Document("class", "MessageActionGameScore")
                    .append("game",((TLMessageActionGameScore) ama).getGameId())
                    .append("score",((TLMessageActionGameScore) ama).getScore());
        } else if (ama instanceof TLMessageActionHistoryClear){
            return new Document("class", "MessageActionHistoryClear");
        } else if (ama instanceof TLMessageActionMigrateTo){
            return new Document("class", "MessageActionMigrateTo")
                    .append("channelId",((TLMessageActionMigrateTo) ama).getChannelId());
        } else if (ama instanceof TLMessageActionPaymentSent){
            return new Document("class", "MessageActionPaymentSent")
                    .append("totalAmount",((TLMessageActionPaymentSent) ama).getTotalAmount())
                    .append("currency", ((TLMessageActionPaymentSent) ama).getCurrency());
        } else if (ama instanceof TLMessageActionPaymentSentMe){
            return new Document("class", "MessageActionPaymentSentMe")
                    .append("totalAmount",((TLMessageActionPaymentSentMe) ama).getTotalAmount())
                    .append("currency", ((TLMessageActionPaymentSentMe) ama).getCurrency())
                    .append("ShippingOptionId",((TLMessageActionPaymentSentMe) ama).getShippingOptionId())
                    .append("payload",((TLMessageActionPaymentSentMe) ama).getPayload().getData())
                    .append("charge", tlPaymentChargeToDocument(((TLMessageActionPaymentSentMe) ama).getCharge()))
                    .append("info",tlPaymentRequestedInfoToDocument(((TLMessageActionPaymentSentMe) ama).getInfo()));
        } else if (ama instanceof TLMessageActionPhoneCall){
            return new Document("class", "MessageAction")
                    .append("callId",((TLMessageActionPhoneCall) ama).getCallId())
                    .append("duration",((TLMessageActionPhoneCall) ama).getDuration())
                    .append("flags",((TLMessageActionPhoneCall) ama).getFlags())
                    .append("reason", tlAbsPhoneCallDiscardReasonToDocument(((TLMessageActionPhoneCall) ama).getReason()));
        } else if (ama instanceof TLMessageActionPinMessage){
            return new Document("class", "MessageAction");
        } else{
            return null;
        }
    }

    /**
     * converts int vector to list
     * @param iv int vector
     */
    private static List<Integer> tlIntVectorToList(TLIntVector iv){
        List<Integer> list = new ArrayList<>();
        if ((!iv.isEmpty()) && (iv != null)) {
            for (Integer i : iv) {
                list.add(i);
            }
        }
        return list;
    }

    /**
     * converts payment charge to document
     * @param pc payment charge
     */
    private static Document tlPaymentChargeToDocument(TLPaymentCharge pc){
        return new Document("class", "PaymentCharge")
                .append("_id",pc.getId())
                .append("providerChargeId", pc.getProviderChargeId());
    }

    /**
     * converts pri to document
     * @param pri payment info
     */
    private static Document tlPaymentRequestedInfoToDocument(TLPaymentRequestedInfo pri){
        return new Document("class", "PaymentRequestedInfo")
                .append("email", pri.getEmail())
                .append("name", pri.getName())
                .append("phone", pri.getPhone())
                .append("shippingAddress", tlPostAddressToDocument(pri.getShippingAddress()));
    }

    /**
     * converts post address to document
     * @param pa post address
     */
    private static Document tlPostAddressToDocument(TLPostAddress pa){
        return new Document("class", "PostAddress")
                .append("city", pa.getCity())
                .append("countryIso2", pa.getCountryIso2())
                .append("postCode", pa.getPostCode())
                .append("state", pa.getState())
                .append("streetLine1", pa.getStreetLine1())
                .append("streetLine2", pa.getStreetLine2());
    }

    /**
     * convetrs apcdr to document
     * @param apcdr phone call discard reason
     */
    private static Document tlAbsPhoneCallDiscardReasonToDocument(TLAbsPhoneCallDiscardReason apcdr){
        if (apcdr instanceof TLPhoneCallDiscardReasonBusy){
            return new Document("class", "PhoneCallDiscardReasonBusy");
        } else if (apcdr instanceof TLPhoneCallDiscardReasonDisconnect){
            return new Document("class", "PhoneCallDiscardReasonDisconnect");
        } else if (apcdr instanceof TLPhoneCallDiscardReasonHangup){
            return new Document("class", "PhoneCallDiscardReasonHangup");
        } else if (apcdr instanceof TLPhoneCallDiscardReasonMissed){
            return new Document("class", "PhoneCallDiscardReasonMissed");
        } else {
            return null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////old/////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Writes MessageDoc instances to collections. Collection names are "DOC_DIAL_PREF+[dialog_id]" (for example: "dialog777000")
     * @param docsInDialogs HashMap of all MessageDocs in Dialogs
     */
    public void dbWriteMessageDocsHashMap(HashMap<Integer, List<MessageDoc>> docsInDialogs){
        Set<Integer> keysDialogs = docsInDialogs.keySet();
        for (Integer keyD : keysDialogs) {
            // get collection for writing
            if (docsInDialogs.get(keyD).size() > 0){
                MongoCollection<Document> collection = database.getCollection(DOC_DIAL_PREF + keyD);
                List<Document> docs = messageDocsToDocuments(docsInDialogs.get(keyD));
                //collection.insertMany(docs);
                for (Document doc: docs){
                    UpdateResult uRes = collection.updateOne(Filters.eq("_id",doc.get("_id")), new Document("$set",doc), new UpdateOptions().upsert(true));
                }
            }
        }
    }

    /**
     * Reads collections from DB and saves them as HashMap. Collection names are "DOC_DIAL_PREF+[dialog_id]" (for example: "dialog777000")
     */
    public HashMap<Integer, List<MessageDoc>> dbReadMessageDocsHashMap(){
        HashMap<Integer, List<MessageDoc>> docsInDialogs = new HashMap<>();
        // get all the existing collections
        MongoIterable<String> colNames = database.listCollectionNames();
        // get only the collections starting from DOC_DIAL_PREF
        List<String> newColNames = getCollectionNamesWithPrefix(colNames, DOC_DIAL_PREF);
        // read documents from collection and insert to hashmap
        for (String newColName: newColNames){
            // key
            Integer newCol = Integer.valueOf(newColName.replaceAll(DOC_DIAL_PREF,""));
            MongoCollection<Document> collection = database.getCollection(newColName);
            // converts to MessageDocs list and writes to the hashtable
            docsInDialogs.put(newCol, recoverMessageDocsHashMap(collection.find()));
        }
        return docsInDialogs;
    }

    /**
     * Converts a single MessageDoc to Document
     * @param md initial MessageDoc
     * @return Document
     * @see Document
     * @see MessageDoc
     */
    public static Document messageDocToDocument(MessageDoc md){
        return new Document("_id", md.getId())
                .append("date", md.getDate())
                .append("text", md.getText());
    }

    /**
     * Converts a list MessageDocs to a list of Documents
     * @param mds list of MessageDoc
     * @return list of Documents
     * @see Document
     * @see MessageDoc
     */
    public static List<Document> messageDocsToDocuments(List<MessageDoc> mds){
        List<Document> docs = new ArrayList<>();
        for (MessageDoc md: mds){
            docs.add(messageDocToDocument(md));
        }
        return docs;
    }

    /**
     * Gets only the list of suitable dialog names. The name is suitable if it starts from DIALOGPREFIX.
     * @param colNames list of colNames
     * @param prefix prefix
     * @return list of collection names with this prefix
     */
    public static List<String> getCollectionNamesWithPrefix(MongoIterable<String> colNames, String prefix){
        List<String> newColNames = new ArrayList<>();
        for (String colName: colNames){
            if (colName.startsWith(prefix)){
                newColNames.add(colName);
            }
        }
        return newColNames;
    }

    /**
     * Converts FindIterable of Documents to List of MessageDocs
     * @return List with MessageDocs from dialog
     */
    public static  List<MessageDoc> recoverMessageDocsHashMap(FindIterable<Document> docs){
        List<MessageDoc> mds = new ArrayList<>();
        for (Document doc: docs){
            mds.add(new MessageDoc((Integer) doc.get("_id"), (Integer) doc.get("date"), (String) doc.get("text")));
        }
        return mds;
    }

    /**
     * finds min & max ids of target
     */
    public void findMinMaxIds(){
        FindIterable<Document> findMin = collection.find().sort(ascending("_id")).limit(1);
        FindIterable<Document> findMax = collection.find().sort(descending("_id")).limit(1);
        Document docMin = findMin.first();
        Document docMax = findMax.first();
        int minId = docMin != null ? (Integer) docMin.get("_id") : null;
        int maxId = docMax != null ? (Integer) docMax.get("_id") : null;
        System.out.println();
    }

}
