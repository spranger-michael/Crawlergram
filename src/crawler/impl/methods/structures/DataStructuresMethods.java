/*
 * Title: CrawlerMain.java
 * Project: telegramJ
 * Creator: mikriuko
 */

package crawler.impl.methods.structures;

import crawler.impl.structures.Document;
import crawler.output.files.FilesMethods;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.user.TLAbsUser;
import org.telegram.tl.TLVector;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class DataStructuresMethods {

    /**
     * Creates and inits chats hashmap
     * @param   chats   TLVector with chats
     * @see HashMap<Integer, TLAbsChat>
     * @see TLVector<TLAbsChat>
     */
    public static HashMap<Integer, TLAbsChat> dataInitChatsHashMap(TLVector<TLAbsChat> chats){
        HashMap<Integer, TLAbsChat> chatsHashMap = new HashMap<Integer, TLAbsChat>();
        chats.forEach(chat -> chatsHashMap.put(chat.getId(), chat));
        return chatsHashMap;
    }

    /**
     * Creates and inits users hashmap
     * @param   users   TLVector with users
     * @see HashMap<Integer, TLAbsUser>
     * @see TLVector<TLAbsUser>
     */
    public static HashMap<Integer, TLAbsUser> dataInitUsersHashMap(TLVector<TLAbsUser> users){
        HashMap<Integer, TLAbsUser> usersHashMap = new HashMap<Integer, TLAbsUser>();
        users.forEach(user -> usersHashMap.put(user.getId(), user));
        return usersHashMap;
    }

    /**
     * Insert chats in existing hashmap (if key does not exist)
     * @param   chats   TLVector with chats
     * @see HashMap<Integer, TLAbsChat>
     * @see TLVector<TLAbsChat>
     */
    public static void dataInsertIntoChatsHashMap(HashMap<Integer, TLAbsChat> chatsHashMap, TLVector<TLAbsChat> chats){
        for (TLAbsChat chat: chats){
            if (!chatsHashMap.containsKey(chat.getId())){
                chatsHashMap.put(chat.getId(), chat);
            }
        }
    }

    /**
     * Insert users in existing hashmap (if key does not exist)
     * @param   users   TLVector with users
     * @see HashMap<Integer, TLAbsUser>
     * @see TLVector<TLAbsUser>
     */
    public static void dataInsertIntoUsersHashMap(HashMap<Integer, TLAbsUser> usersHashMap, TLVector<TLAbsUser> users){
        for (TLAbsUser user: users){
            if (!usersHashMap.containsKey(user.getId())){
                usersHashMap.put(user.getId(), user);
            }
        }
    }

    /**
     * Converts Set to double array
     * @param   set   input set (with doubles or integers)
     */
    public static double[] dataSetToDoubles(Set<Integer> set){
        double[] res = new double[set.size()];

        int i = 0;
        for (Integer elem: set){
            res[i] = elem;
            i++;
        }

        return res;
    }

    /**
     * Converts Set to double array
     * @param   set   input set (with doubles or integers)
     */
    public static double[][] dataSetToDoubles2D(Set<Integer> set){
        double[][] res = new double[set.size()][1];

        int i = 0;
        for (Integer elem: set){
            res[i][0] = elem;
            i++;
        }

        return res;
    }

    /**
     * Converts List to double array
     * @param   list   input list (with doubles or integers)
     */
    public static double[] dataListToDoubles(List<Integer> list){
        double[] res = new double[list.size()];

        int i = 0;
        for (Integer elem: list){
            res[i] = elem;
            i++;
        }

        return res;
    }

    /**
     * Converts List to double array
     * @param docsInDialogs HashMap with docs for output
     */
    public static void saveDocsToFiles(HashMap<Integer, List<Document>> docsInDialogs, String path) {
        Set<Integer> keysDialogs = docsInDialogs.keySet();
        //date (part of the filename uniqueness)
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS_");
        Date date = new Date();
        // create file for output
        for (Integer keyD : keysDialogs) {
            String filename = path + File.separator + "docs_" + dateFormat.format(date) + keyD + ".csv";
            FilesMethods.checkFilePath(filename);
            //write docs data to files
            try {
                Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
                for (Document doc : docsInDialogs.get(keyD)) {
                    out.append(doc.getId() + ";" + doc.getDate() + ";" + doc.getText() + "\n");
                }
                out.flush();
                out.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Replaces all the new lines in docs
     * @param docsInDialogs HashMap with docs for output
     */
    public static void removeDocsNewLine(HashMap<Integer, List<Document>> docsInDialogs){
        Set<Integer> keysDialogs = docsInDialogs.keySet();
        for (Integer keyD : keysDialogs) {
            for (int i = 0; i < docsInDialogs.get(keyD).size(); i++) {
                docsInDialogs.get(keyD).get(i).setText(replaceNewLine(docsInDialogs.get(keyD).get(i).getText(), " "));
            }
        }
    }

    /**
     * Replaces all \n, \r\n and \r with repl argument
     * @param str original string
     * @param repl replacement
     */
    private static String replaceNewLine(String str, String repl){
        String str1 = str.replaceAll("\n\r", repl);
        String str2 = str1.replaceAll("\r", repl);
        String str3 = str2.replaceAll("\n", repl);
        return str3;
    }

}
