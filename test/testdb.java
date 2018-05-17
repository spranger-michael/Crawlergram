/*
 * Title: testdb.java
 * Project: telegramJ
 * Creator: mikriuko
 */


import crawler.db.mongo.MongoStorage;
import crawler.impl.structures.MessageDoc;

import java.util.*;

public class testdb {

    static String user = "telegramJ"; // the user name
    static String db = "telegram"; // the name of the db in which the user is defined
    static String psw = "cart"; // the psw

    public static void main(String[] args) {

        // DB "telegram" location - localhost:27017
        // User "telegramJ" - db.createUser({user: "telegramJ", pwd: "cart", roles: [{ role: "readWrite", db: "telegram" }]})
        MongoStorage mongo = new MongoStorage("telegramJ", "telegram", "cart", "localhost", 27017);

        HashMap<Integer, List<MessageDoc>> hm = mongo.dbReadMessageDocsHashMap();

        /*

        MongoCollection<Document> collection = database.getCollection("testColl");
        collection.createIndex(Indexes.ascending("name"));

        MongoIterable<String> collections = database.listCollectionNames();
        for (String collecion: collections){
            System.out.println(collecion);
        }



        Document doc = new Document("_id", "MongoDB")
                .append("type", "database")
                .append("count", 1)
                .append("versions", Arrays.asList("v3.2", "v3.0", "v2.6"))
                .append("info", new Document("x", 203).append("y", 102));

        collection.insertOne(doc);

        Block<Document> printBlock = new Block<Document>() {
            @Override
            public void apply(final Document document) {
                System.out.println(document.toJson());
            }
        };

        collection.find().forEach(printBlock);
        */


        System.out.println("done");

    }

}
