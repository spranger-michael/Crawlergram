/*
 * Title: ReadFromDBMethods.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

package topicextractor.extractormethods;

import storage.db.DBStorage;
import topicextractor.structures.TopicExtractionDialog;
import topicextractor.structures.TopicExtractionMessage;

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
        if ((dialogs != null) && (!dialogs.isEmpty())){
            for (TopicExtractionDialog dialog: dialogs){
                // do for one
                getTopicsForOneDialog(dbStorage, dialog, dateFrom, dateTo, docThreshold);
            }
        } else {
            System.out.println("NO DIALOGS FOUND");
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
        if ((msgs != null) && !msgs.isEmpty()){
            msgs = MessageMergingMethods.mergeMessages(dialog, msgs, docThreshold);
            msgs = removePunctuation(msgs);
            //TODO
        } else {
            System.out.println("EMPTY MESSAGES: " + dialog.getId() + " " + dialog.getUsername());
        }
    }

    /**
     * if dates are wrong (from > to) or both dates equal zero -> false
     * @param dateFrom
     * @param dateTo
     */
    private static boolean datesCheck(int dateFrom, int dateTo) {
        return ((dateFrom != 0) || (dateTo != 0)) && dateFrom < dateTo;
    }

    private static List<String[]> tokenize(List<TopicExtractionMessage> msgs){
        return null;
        //TODO
    }

    private static List<TopicExtractionMessage> removePunctuation(List<TopicExtractionMessage> msgs){
        for (int i = 0; i < msgs.size(); i++){
            msgs.get(i).setText(msgs.get(i).getText().replaceAll("\\p{Punct}", ""));
        }
        return msgs;
    }



}
