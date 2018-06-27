/*
 * Title: ReadFromDBMethods.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

package topicextractor.extractormethods;

import storage.db.DBStorage;
import topicextractor.structures.TopicExtractionDialog;
import topicextractor.structures.TopicExtractionMessage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TopicExtractionMethods {

    /**
     * do topic extraction for each dialog, if dates are wrong (from > to) or both dates equal zero -> read all messages
     * @param dbStorage db storage implementation
     * @param dateFrom date from
     * @param dateTo date to
     * @param docThreshold if chat has very low number of messages (< docThreshold) -> all chat is merged
     */
    public static void getTopicsForAllDialogs(DBStorage dbStorage, int dateFrom, int dateTo, int docThreshold){
        // get all dialogs
        List<TopicExtractionDialog> dialogs = dbStorage.getDialogs();
        // iteratively for each do:
        for (TopicExtractionDialog dialog: dialogs){
            // do for one
            getTopicsForOneDialog(dbStorage, dialog, dateFrom, dateTo, docThreshold);
        }
    }

    /**
     * do topic extraction for a specific dialog, if dates are wrong (from > to) or both dates equal zero -> read all
     * @param dbStorage db storage implementation
     * @param dialog dialog
     * @param dateFrom date from
     * @param dateTo date to
     * @param docThreshold if chat has very low number of messages (< docThreshold) -> all chat is merged
     */
    public static void getTopicsForOneDialog(DBStorage dbStorage, TopicExtractionDialog dialog,
                                             int dateFrom, int dateTo, int docThreshold){
        List<TopicExtractionMessage> msgs;
        // if dates valid - get only messages between these dates, otherwise - get all messages
        if (datesCheck(dateFrom, dateTo)){
            msgs = dbStorage.readMessages(dialog, dateFrom, dateTo);
        } else {
            msgs = dbStorage.readMessages(dialog);
        }
        // check if resulting list is not empty
        if (!msgs.isEmpty()){
            msgs = MessageMergingMethods.mergeMessages(dialog, msgs, docThreshold);
        }

        System.out.println();
        //TODO

    }

    /**
     * if dates are wrong (from > to) or both dates equal zero -> false
     * @param dateFrom
     * @param dateTo
     */
    private static boolean datesCheck(int dateFrom, int dateTo){
        if ((dateFrom == 0) && (dateTo == 0)){
            return false;
        } else {
            return dateFrom < dateTo;
        }
    }

}
