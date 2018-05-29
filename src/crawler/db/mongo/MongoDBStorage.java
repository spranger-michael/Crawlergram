/*
 * Title: MongoInterface.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

package crawler.db.mongo;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import crawler.db.Const;
import crawler.db.DBStorage;
import crawler.implementation.structures.MessageDoc;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.mongodb.client.model.Filters;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.chat.TLAbsChatFull;
import org.telegram.api.chat.TLChatFull;
import org.telegram.api.chat.channel.TLChannelFull;
import org.telegram.api.document.TLAbsDocument;
import org.telegram.api.document.TLDocument;
import org.telegram.api.document.attribute.*;
import org.telegram.api.file.location.TLFileLocation;
import org.telegram.api.game.TLGame;
import org.telegram.api.geo.point.TLAbsGeoPoint;
import org.telegram.api.geo.point.TLGeoPoint;
import org.telegram.api.message.*;
import org.telegram.api.message.media.*;
import org.telegram.api.messages.TLMessagesChatFull;
import org.telegram.api.peer.TLAbsPeer;
import org.telegram.api.peer.TLPeerChannel;
import org.telegram.api.peer.TLPeerChat;
import org.telegram.api.peer.TLPeerUser;
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
import org.telegram.tl.TLObject;
import org.telegram.tl.TLVector;

/**
 * Class for writing and reading data to and from MongoDB.
 */

public class MongoDBStorage implements DBStorage {

    public static final String DOC_DIAL_PREF = "dialog"; // prefix for dialogs collections in DB
    static String user; // the user name
    static String db; // the name of the db in which the user is defined
    static String psw; // the psw
    static String host; // host
    static Integer port; // port
    static MongoCredential credential; // auth info
    static MongoClientOptions options; // client options
    static MongoClient mongoClient; // client instance
    static MongoDatabase database; // db instance
    static GridFSBucket gridFSBucket; //bucket for files
    static MongoCollection<Document> collection; //collection

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
    public void write(Object obj){
        collection.insertOne((Document) obj);
    }

    /**
     * Writes full dialog to db
     * @param dial object with dialog (chat/channel/user)
     * @param chatsHashMap prevents unnecessary downloading by getting data from HashMap
     * @param usersHashMap prevents unnecessary downloading by getting data from HashMap
     */
    @Override
    public void writeFullDialog(TLObject dial, HashMap<Integer, TLAbsChat> chatsHashMap, HashMap<Integer, TLAbsUser> usersHashMap){
        // set target of writing
        this.setTarget(Const.DIALOGS);
        // write it
        if (dial instanceof TLMessagesChatFull) {
            TLAbsChatFull absChatFull = ((TLMessagesChatFull) dial).getFullChat();
            //check if chat full or channel full
            if (absChatFull instanceof TLChannelFull) {
                //TODO
            } else if (absChatFull instanceof TLChatFull) {
                //TODO
            }
        } else if (dial instanceof TLUserFull) {
            this.write(tlUserFullToDocument((TLUserFull) dial));
        }
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
     * converts abstract user to document
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
     * Write a single message to DB
     * @param absMessage
     */
    @Override
    public void writeTLAbsMessage(TLAbsMessage absMessage){

        //TODO

        if (absMessage instanceof TLMessage){

        } else if (absMessage instanceof TLMessageService){

        } else if (absMessage instanceof TLMessageEmpty){

        }

        //UpdateResult uRes = collection.updateOne(Filters.eq("_id",doc.get("_id")), new Document("$set",doc), new UpdateOptions().upsert(true));
    }

    /**
     * Write messages to DB
     * @param absMessages messages
     */
    @Override
    public void writeTLAbsMessages(TLVector<TLAbsMessage> absMessages) {
        try {
            for (TLAbsMessage absMessage : absMessages) {
                writeTLAbsMessage(absMessage);
            }
        } catch (MongoException e) {
            System.err.println(e.getCode() + " " + e.getMessage());
        }
    }

    /**
     * Converts TLMessage to Document
     */
    private static Document tlMessageToDocument(TLMessage m){
        return new Document("_id", m.getId())
                .append("classId", m.getClassId())
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
     * converts abstract peer to doc
     * @param ap abstract peer
     * @return doc
     */
    private static Document tlAbsPeerToDocument(TLAbsPeer ap){
        if (ap instanceof TLPeerUser){
            return new Document("classId", ap.getClassId()).append("id", ap.getId());
        } else if (ap instanceof TLPeerChannel){
            return new Document("classId", ap.getClassId()).append("id", ap.getId());
        } else if (ap instanceof TLPeerChat){
            return new Document("classId", ap.getClassId()).append("id", ap.getId());
        } else {
            return new Document("classId", ap.getClassId());
        }
    }

    /**
     * converts forward header to doc
     * @param fh forward header
     * @return doc
     */
    private static Document tlMsgFwdHeaderToDocument(TLMessageFwdHeader fh){
        return new Document("classId", fh.getClassId())
                .append("fromId", fh.getFromId())
                .append("date", fh.getDate())
                .append("channelId", fh.getChannelId())
                .append("channelPost", fh.getChannelPost());
    }

    /**
     * converts media to doc
     * @param amm
     * @return doc
     */
    private static Document tlAbsMessageMediaToDocument(TLAbsMessageMedia amm){
        if (amm instanceof TLMessageMediaContact) {
            return new Document("classId", amm.getClassId())
                    .append("id", ((TLMessageMediaContact) amm).getUserId())
                    .append("firstName",((TLMessageMediaContact) amm).getFirstName())
                    .append("lastName", ((TLMessageMediaContact) amm).getLastName())
                    .append("phone", ((TLMessageMediaContact) amm).getPhoneNumber());

        } else if (amm instanceof TLMessageMediaDocument) {
            return new Document("classId", amm.getClassId())
                    .append("caption", ((TLMessageMediaDocument) amm).getCaption())
                    .append("document", tlAbsDocumentToDocument(((TLMessageMediaDocument) amm).getDocument()));

        } else if (amm instanceof TLMessageMediaEmpty) {
            return new Document("classId", amm.getClassId());

        } else if (amm instanceof TLMessageMediaGame) {
            return new Document("classId", amm.getClassId())
                    .append("", tlGameToDocument(((TLMessageMediaGame) amm).getGame()));

        } else if (amm instanceof TLMessageMediaGeo) {
            return new Document("classId", amm.getClassId())
                    .append("geo", tlGeoPointToDocument(((TLMessageMediaGeo) amm).getGeo()));

        } else if (amm instanceof TLMessageMediaPhoto) {
            return new Document("classId", amm.getClassId())
                    .append("caption", ((TLMessageMediaPhoto) amm).getCaption())
                    .append("photo", tlAbsPhotoToDocument(((TLMessageMediaPhoto) amm).getPhoto()));

        } else if (amm instanceof TLMessageMediaUnsupported) {
            return new Document("classId", amm.getClassId());

        } else if (amm instanceof TLMessageMediaVenue) {
            return new Document("classId", amm.getClassId())
                    .append("id", ((TLMessageMediaVenue) amm).getVenue_id())
                    .append("address", ((TLMessageMediaVenue) amm).getAddress())
                    .append("provider", ((TLMessageMediaVenue) amm).getProvider())
                    .append("title", ((TLMessageMediaVenue) amm).getTitle())
                    .append("geo", tlGeoPointToDocument(((TLMessageMediaVenue) amm).getGeo()));

        } else if (amm instanceof TLMessageMediaWebPage) {
            return new Document("classId", amm.getClassId())
                    .append("", (tlWebPageToDocument(((TLMessageMediaWebPage) amm).getWebPage())));

        } else if (amm instanceof TLMessageMediaInvoice) {
            return new Document("classId", amm.getClassId())
                    .append("title", ((TLMessageMediaInvoice) amm).getTitle())
                    .append("amount", ((TLMessageMediaInvoice) amm).getTotalAmount())
                    .append("currency", ((TLMessageMediaInvoice) amm).getCurrency())
                    .append("description", ((TLMessageMediaInvoice) amm).getDescription())
                    .append("msgId", ((TLMessageMediaInvoice) amm).getReceiptMsgId())
                    .append("startParam", ((TLMessageMediaInvoice) amm).getStartParam());
        } else {
            return new Document("classId", amm.getClassId());
        }
    }

    /**
     * converts geo point to doc
     * @param agp geo point
     * @return doc
     */
    private static Document tlGeoPointToDocument(TLAbsGeoPoint agp){
        if (agp instanceof TLGeoPoint){
            return new Document("classId", agp.getClassId())
                    .append("lat", ((TLGeoPoint) agp).getLat())
                    .append("lon", ((TLGeoPoint) agp).getLon());
        } else {
            return new Document("classId", agp.getClassId());
        }
    }

    /**
     * converts abs doc to doc
     * @param ad abs doc
     * @return doc
     */
    private static Document tlAbsDocumentToDocument(TLAbsDocument ad){
        if (ad instanceof TLDocument){
            return new Document("classId", ad.getClassId())
                    .append("id", ad.getId())
                    .append("accessHash", ((TLDocument) ad).getAccessHash())
                    .append("dcId", ((TLDocument) ad).getDcId())
                    .append("date", ((TLDocument) ad).getDate())
                    .append("mimeType", ((TLDocument) ad).getMimeType())
                    .append("size", ((TLDocument) ad).getSize())
                    .append("version", ((TLDocument) ad).getVersion())
                    // only file name & no thumb
                    .append("filename", tlAbsDocumentAttributesToName(((TLDocument) ad).getAttributes(), (TLDocument) ad));
        } else {
            return new Document("classId", ad.getClassId())
                    .append("id", ad.getId());
        }
    }

    /**
     * converts doc attrs to name of the doc
     * @return
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
        return new Document("classId", g.getClassId())
                .append("id", g.getId())
                .append("name", g.getShortName())
                .append("title", g.getTitle())
                .append("accessHash", g.getAccessHash())
                .append("description", g.getDescription())
                .append("document", tlAbsDocumentToDocument(g.getDocument()));
    }

    /**
     * converts abs photo to doc
     * @param ap abs photo
     * @return doc
     */
    private static Document tlAbsPhotoToDocument(TLAbsPhoto ap){
        if (ap instanceof TLPhoto){
            return new Document("class", "Photo")
                    .append("id", ((TLPhoto) ap).getId())
                    .append("accessHash", ((TLPhoto) ap).getAccessHash())
                    .append("date", ((TLPhoto) ap).getDate())
                    .append("location", getLargestPhotoLocation(((TLPhoto) ap).getSizes()));
        } else if (ap instanceof TLPhotoEmpty){
            return new Document("class", "PhotoEmpty")
                    .append("id", ((TLPhotoEmpty) ap).getId());
        } else {
            return new Document("class", "Unknown");
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
        TLPhotoSize aps = null;
        TLFileLocation psl = null;
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
                .append("size", fl.getDcId())
                .append("localId", fl.getLocalId())
                .append("volumeId", fl.getVolumeId())
                .append("secret", fl.getSecret());
    }

    private static Document tlWebPageToDocument(TLAbsWebPage wp){
        if (wp instanceof TLWebPage){
            return new Document("classId", wp.getClassId())
                    .append("size", ((TLWebPage) wp).getTitle())
                    .append("localId", ((TLWebPage) wp).getUrl())
                    .append("volumeId", ((TLWebPage) wp).getSite_name());
        } else {
            return new Document("classId", wp.getClassId());
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

}
