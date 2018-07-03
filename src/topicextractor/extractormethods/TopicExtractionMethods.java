/*
 * Title: ReadFromDBMethods.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

package topicextractor.extractormethods;

import storage.db.DBStorage;
import topicextractor.structures.TopicExtractionDialog;
import topicextractor.structures.TopicExtractionMessage;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            List<List<String>> tokens = tokenize(msgs);
            System.out.println();
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

    /**
     * Tokenization method for strings
     * @param msgs original messages
     * @see StringTokenizer
     */
    private static List<List<String>> tokenize(List<TopicExtractionMessage> msgs){
        List<List<String>> tokensList = new LinkedList<>();
        for (TopicExtractionMessage msg: msgs){
            String[] tokensA = msg.getText().split("\\s");
            List<String> tokensL = new LinkedList<>();
            for (String token: tokensA){
                String t = tokenEdit(token);
                if (!t.isEmpty()){
                    tokensL.add(t);
                }
            }
            if (!tokensL.isEmpty()){
                tokensList.add(tokensL);
            }
        }
        return tokensList;
    }

    /**
     * various modifications: emptiness, number check, link check, etc.
     * @param token original token
     */
    private static String tokenEdit(String token){
        if (tokenCheck(token)){
            return "";
        } else {
            token = removePunctuation(token);
            if (tokenCheck(token)){
                return "";
            } else {
                return token;
            }
        }
    }

    /**
     * various checks: emptiness, number check, link check, etc.
     * @param token original token
     */
    private static boolean tokenCheck(String token){
        return token.isEmpty() || tokenIsLink(token) || tokenIsNumber(token) || tokenIsShort(token, 1);
    }

    /**
     * checks if token is web link
     * @param token original token
     */
    private static boolean tokenIsLink(String token){
        // http(s), www, ftp links
        String p1 = "^(http://|https://|ftp://|file://|mailto:|nfs://|irc://|ssh://|telnet://|www\\.).+";
        // short links of type: youtube.com & youtube.com/watch?v=oHg5SJYRHA0
        String p2 = "^[A-Za-z0-9_.-~@]+\\.[A-Za-z0-9_.-~@]+(/.*)?";
        Pattern pat = Pattern.compile("(" + p1 +")" + "|" + "(" + p2 +")");
        Matcher mat = pat.matcher(token);
        return mat.matches();
    }

    /**
     * removes punctuation from the token
     * @param token original token
     */
    private static String removePunctuation(String token){
        return token.replaceAll("\\p{Punct}", "");
    }

    /**
     * checks if token can be casted into double
     */
    private static boolean tokenIsNumber(String token){
        try {
            Double d = Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    /**
     * checks if token is shorter (or equal) than x
     * @param token original token
     * @param x minimal length of token (inclusive)
     */
    private static boolean tokenIsShort(String token, int x){
        return token.length() <= x;
    }

}
