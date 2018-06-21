/*
 * Title: TopicExtractorMain.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

package topicminer;

import storage.db.DBStorage;
import storage.db.mongo.MongoDBStorage;

public class TopicExtractorMain {

    public static void main(String[] args) {

        // DB "telegram" location - localhost:27017
        // User "telegramJ" - db.createUser({user: "telegramJ", pwd: "cart", roles: [{ role: "readWrite", db: "telegram" }]})
        DBStorage dbStorage = new MongoDBStorage("telegramJ", "telegram", "cart", "localhost", 27017, "fs");




        //TODO read from DB, calculate time intervals, create docs, perform topic extraction

    }

}
