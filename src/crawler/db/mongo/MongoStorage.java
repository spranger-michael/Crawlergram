/*
 * Title: MongoInterface.java
 * Project: telegramJ
 * Creator: mikriuko
 */

package crawler.db.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import crawler.impl.structures.MessageDoc;
import org.bson.BsonDocument;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.mongodb.client.model.Filters;

/**
 * Class for writing and reading data to and from MongoDB.
 */

public class MongoStorage {

    static final String DIALOGSPREFIX = "dialog"; // prefix for dialogs collections in DB
    static String user; // the user name
    static String db; // the name of the db in which the user is defined
    static String psw; // the psw
    static String host; // host
    static Integer port; // port
    static MongoCredential credential; // auth info
    static MongoClientOptions options; // client options
    static MongoClient mongoClient; // client instance
    static MongoDatabase database; // db instance


    public MongoStorage(String user, String db, String psw, String host, Integer port){
        this.user = user;
        this.db = db;
        this.psw = psw;
        this.host = host;
        this.port = port;
        this.credential = MongoCredential.createCredential(user, db, psw.toCharArray());
        this.options = MongoClientOptions.builder().build();
        this.mongoClient = new MongoClient(new ServerAddress(host, port), credential, options);
        this.database = mongoClient.getDatabase(db);
    }

    /**
     * Writes MessageDoc instances to collections. Collection names are "DIALOGSPREFIX+[dialog_id]" (for example: "dialog777000")
     * @param docsInDialogs HashMap of all MessageDocs in Dialogs
     */
    public void dbWriteMessageDocsHashMap(HashMap<Integer, List<MessageDoc>> docsInDialogs){
        Set<Integer> keysDialogs = docsInDialogs.keySet();
        for (Integer keyD : keysDialogs) {
            // get collection for writing
            if (docsInDialogs.get(keyD).size() > 0){
                MongoCollection<Document> collection = database.getCollection(DIALOGSPREFIX + keyD);
                List<Document> docs = MongoStructuresMethods.messageDocsToDocuments(docsInDialogs.get(keyD));
                //collection.insertMany(docs);
                for (Document doc: docs){
                    UpdateResult uRes = collection.updateOne(Filters.eq("_id",doc.get("_id")), new Document("$set",doc), new UpdateOptions().upsert(true));
                }
            }
        }
    }

    /**
     * Reads collections from DB and saves them as HashMap. Collection names are "DIALOGSPREFIX+[dialog_id]" (for example: "dialog777000")
     */
    public HashMap<Integer, List<MessageDoc>> dbReadMessageDocsHashMap(){
        HashMap<Integer, List<MessageDoc>> docsInDialogs = new HashMap<>();
        // get all the existing collections
        MongoIterable<String> colNames = database.listCollectionNames();
        // get only the collections starting from DIALOGSPREFIX
        List<String> newColNames = MongoStructuresMethods.getCollectionNamesWithPrefix(colNames, DIALOGSPREFIX);
        // read documents from collection and insert to hashmap
        for (String newColName: newColNames){
            // key
            Integer newCol = Integer.valueOf(newColName.replaceAll(DIALOGSPREFIX,""));
            MongoCollection<Document> collection = database.getCollection(newColName);
            // converts to MessageDocs list and writes to the hashtable
            docsInDialogs.put(newCol, MongoStructuresMethods.recoverMessageDocsHashMap(collection.find()));
        }
        return docsInDialogs;
    }


}
