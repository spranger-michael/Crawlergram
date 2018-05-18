/*
 * Title: MongoStructuresMethods.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

package crawler.db.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoIterable;
import crawler.implementation.structures.MessageDoc;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoStructuresMethods {

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
