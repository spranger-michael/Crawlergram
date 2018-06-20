/*
 * Title: testdbread.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

import com.mongodb.client.MongoIterable;
import crawler.db.mongo.MongoDBStorage;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class testdbread {

    static String user = "telegramJ"; // the user name
    static String db = "telegram"; // the name of the db in which the user is defined
    static String psw = "cart"; // the psw

    public static void main(String[] args) {
        // DB "telegram" location - localhost:27017
        // User "telegramJ" - db.createUser({user: "telegramJ", pwd: "cart", roles: [{ role: "readWrite", db: "telegram" }]})
        MongoDBStorage mongo = new MongoDBStorage("telegramJ", "telegram", "cart", "localhost", 27017, "fs");


        List<String> collections = mongo.getExistingCollections();
        for (String collecion: collections){
            mongo.readMessages(collecion, 1528134100, 1528634100);
        }



        System.out.println("done");
    }
}
