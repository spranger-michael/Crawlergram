/*
 * Title: CrawlerMain.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

package crawler.implementation.apimethods;

import crawler.implementation.structures.DataStructuresMethods;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.dialog.TLDialog;
import org.telegram.api.engine.RpcException;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.engine.storage.AbsApiState;
import org.telegram.api.functions.messages.TLRequestMessagesGetDialogs;
import org.telegram.api.functions.messages.TLRequestMessagesGetHistory;
import org.telegram.api.input.peer.TLAbsInputPeer;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.TLMessage;
import org.telegram.api.message.TLMessageEmpty;
import org.telegram.api.message.TLMessageService;
import org.telegram.api.messages.TLAbsMessages;
import org.telegram.api.messages.dialogs.TLAbsDialogs;
import org.telegram.api.messages.dialogs.TLDialogsSlice;
import org.telegram.api.user.TLAbsUser;
import org.telegram.tl.TLVector;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public class DialogsHistoryMethods {

    /**
     * Gets all dialogs dialogs of current client by chunks
     * @param api TelegramApi instance for RPC request
     * @param dialogs dialogs vector
     * @param chatsHashMap chats map
     * @param usersHashMap users map
     * @see TelegramApi
     * @see TLVector
     * @see HashMap
     */
    public static void getDialogsChatsUsers(TelegramApi api, TLVector<TLDialog> dialogs, HashMap<Integer, TLAbsChat> chatsHashMap, HashMap<Integer, TLAbsUser> usersHashMap,  HashMap<Integer, TLAbsMessage> messagesHashMap){
        TLAbsDialogs absDialogs = null;
        Set<Integer> dialogIdSet = new HashSet<>();
        // read dialogs
        try {
            TLRequestMessagesGetDialogs getDialogs = SetTLObjectsMethods.getDialogsSet();
            absDialogs = api.doRpcCall(getDialogs);
        } catch (RpcException e) {
            System.err.println(e.getErrorTag() + " " + e.getErrorCode());
        } catch (TimeoutException | IOException e) {
            System.err.println(e.getMessage());
        }
        setDialogsChatsUsersStructures(absDialogs, dialogs, chatsHashMap, usersHashMap, messagesHashMap, dialogIdSet);
        // if slice of dialogs, get rest of the dialogs in loop by chunks
        if (absDialogs instanceof TLDialogsSlice){
            int count = ((TLDialogsSlice) absDialogs).getCount();
            int curCount = dialogs.size();
            while (curCount < count){
                // offset settings
                TLVector<TLAbsMessage> absMessages = absDialogs.getMessages();
                TLVector<TLDialog> dialogsVec = absDialogs.getDialogs();
                TLAbsInputPeer absInputPeerOffset = SetTLObjectsMethods.getAbsInputPeerSet(dialogsVec.get(dialogsVec.size()-1).getPeer(), chatsHashMap, usersHashMap);
                TLAbsMessage lastAbsMes = absMessages.get(absMessages.size()-1);
                int offId = getLastNonEmptyMessageId(lastAbsMes);
                int offDate = getLastNonEmptyMessageDate(lastAbsMes);
                // chunk request
                try {
                    TLRequestMessagesGetDialogs getDialogs = SetTLObjectsMethods.getDialogsSet(100, offId, absInputPeerOffset, offDate);
                    absDialogs = api.doRpcCall(getDialogs);
                } catch (RpcException e) {
                    System.err.println(e.getErrorTag() + " " + e.getErrorCode());
                } catch (TimeoutException | IOException e) {
                    System.err.println(e.getMessage());
                }
                setDialogsChatsUsersStructures(absDialogs, dialogs, chatsHashMap, usersHashMap, messagesHashMap, dialogIdSet);
                curCount = dialogs.size();
            }
        }
    }

    /**
     * gets the id of last non-empty message (and dialog)
     * @param dialogs abs dialogs vector
     */
    private static void dialogsIdToSet(TLAbsDialogs absDialogs, TLVector<TLDialog> dialogs, Set<Integer> dialogSet){
        for (TLDialog dialog: absDialogs.getDialogs()){
            if (!dialogSet.contains(dialog.getPeer().getId())){
                dialogSet.add(dialog.getPeer().getId());
                dialogs.add(dialog);
            }
        }
    }

    /**
     * Puts data from TLAbsDialogs instance to vectors
     * @param absDialogs abs dialogs
     * @param dialogs dialogs
     * @param chatsHashMap chats map
     * @param usersHashMap users map
     */
    private static void setDialogsChatsUsersStructures(TLAbsDialogs absDialogs, TLVector<TLDialog> dialogs, HashMap<Integer, TLAbsChat> chatsHashMap, HashMap<Integer, TLAbsUser> usersHashMap, HashMap<Integer, TLAbsMessage> messagesHashMap, Set<Integer> dialogSet){
        DataStructuresMethods.insertIntoChatsHashMap(chatsHashMap, absDialogs.getChats());
        DataStructuresMethods.insertIntoUsersHashMap(usersHashMap, absDialogs.getUsers());
        DataStructuresMethods.insertIntoMessagesHashMap(messagesHashMap, absDialogs.getMessages());
        dialogsIdToSet(absDialogs,dialogs,dialogSet);
    }

    /**
     * gets the id of last non-empty message (and dialog)
     * @param absMessages abs messages vector
     */
    private static int apiGetLastNonEmptyMessage(TLVector<TLAbsMessage> absMessages){
        int lastMessageId = 0;
        for (int i = absMessages.size()-1; i >= 0; i-- ){
            if (!(absMessages.get(i) instanceof TLMessageEmpty)){
                lastMessageId = i;
                break;
            }
        }
        return lastMessageId;
    }

    /**
     * gets the id of the last non-empty message
     * @param absMessage last non-empty message
     */
    private static int getLastNonEmptyMessageId(TLAbsMessage absMessage){
        int id = 0;
        if (absMessage instanceof TLMessage){
            id = ((TLMessage) absMessage).getId();
        } else if (absMessage instanceof TLMessageService){
            id = ((TLMessageService) absMessage).getId();
        }
        return id;
    }

    /**
     * gets the date of the last non-empty message
     * @param absMessage last non-empty message
     */
    private static int getLastNonEmptyMessageDate(TLAbsMessage absMessage){
        int date = 0;
        if (absMessage instanceof TLMessage){
            date = ((TLMessage) absMessage).getDate();
        } else if (absMessage instanceof TLMessageService){
            date = ((TLMessageService) absMessage).getDate();
        }
        return date;
    }

    /**
     * Gets message history (not empty or service messages). Telegram returns only 100 messages at maximum by default -> returns messages in chunks with offsets.
     * @param	api  TelegramApi instance for RPC request
     * @param   dialog  dialog
     * @param   chatsHashMap    chats hashtable
     * @param   usersHashMap    users hashtable
     * @param   limit   maximum number of retrieved messages from each dialog (0 if need to get all the messages from dialog)
     * @see TelegramApi
     * @see AbsApiState
     */
    public static TLVector<TLAbsMessage> getOnlyUsersMessagesHistory(TelegramApi api, TLDialog dialog, HashMap<Integer, TLAbsChat> chatsHashMap, HashMap<Integer, TLAbsUser> usersHashMap, int limit) {
        // sleep time (Telegram can send FLOOD WAIT ERROR if the requests are done too often)
        int sleepTime = 100;
        if ((limit > 1000) || (limit == 0)){sleepTime = 1000;}
        TLVector<TLAbsMessage> messages = new TLVector<>();
        Set<Integer> messageIdSet = new HashSet<>();
        int offId = 0; // offset id
        int offDate = 0; // offset date
        int receivedMsgs = 0; // received messages
        try {
            while (receivedMessagesCheck(receivedMsgs, limit)) {
                TLRequestMessagesGetHistory getHistory = SetTLObjectsMethods.getHistoryRequestSet(dialog, chatsHashMap, usersHashMap, 100, offDate, offId);
                TLAbsMessages absMessages = api.doRpcCall(getHistory);
                // if returns no messages -> break the loop
                if (absMessages.getMessages().isEmpty() || absMessages == null || absMessages.getMessages() == null){ break; }
                // update known users and chats hashmaps
                DataStructuresMethods.insertIntoChatsHashMap(chatsHashMap, absMessages.getChats());
                DataStructuresMethods.insertIntoUsersHashMap(usersHashMap, absMessages.getUsers());
                // abstract messages
                TLVector<TLAbsMessage> absMessagesVector = absMessages.getMessages();
                // collect only messages written by users
                getOnlyUsersMessagesFromHistory(messages, absMessagesVector, messageIdSet);
                receivedMsgs = messages.size();
                // if returns number of messages lesser than chunk size (100) - end of the chat -> break the loop
                if (absMessagesVector.size() < 100){ break; }
                // offsets: last offset + chunk size (100)
                offId = ((TLMessage) messages.get(messages.size()-1)).getId();
                offDate = ((TLMessage) messages.get(messages.size()-1)).getDate();
                try {Thread.sleep(sleepTime);} catch (InterruptedException e) {}
            }
        } catch (RpcException e) {
            System.err.println(e.getErrorTag() + " " + e.getErrorCode());
        } catch (TimeoutException | IOException e) {
            System.err.println(e.getMessage());
        }
        if ((messages.size() > limit) && (limit != 0)){
            int delta = messages.size() - limit;
            messages.subList(messages.size()-1-delta,messages.size()-1).clear();
        }
        return messages;
    }

    /**
     * Checks if enough messages received. Has additional check for infinite number of messages.
     * @param receivedMsgs number of received messages
     * @param limit messages limit (if 0 - infinity)
     */
    private static boolean receivedMessagesCheck(int receivedMsgs, int limit){
        boolean boo = false;
        if (limit == 0){
            return true;
        } else if (receivedMsgs <= limit){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Writes only users' messages to 1st array from 2nd one
     * @param messages output array (only users' messages)
     * @param absMessagesVector input array (all messages)
     * @param messageIdSet set of unique message IDs
     */
    private static void getOnlyUsersMessagesFromHistory(TLVector<TLAbsMessage> messages, TLVector<TLAbsMessage> absMessagesVector, Set<Integer> messageIdSet) {
        for (TLAbsMessage absMessage: absMessagesVector){
            // message should be TLMessage, not TLMessageEmpty or TLMessageService
            if (absMessage instanceof TLMessage){
                if (!(messageIdSet.contains(((TLMessage) absMessage).getId()))){
                    messages.add(absMessage);
                    messageIdSet.add(((TLMessage) absMessage).getId());
                }
            }
        }
    }

    /**
     * Writes users' and service messages to 1st array from 2nd one. Returns the number of retrieved messages
     * @param	messages  output array (only users' messages)
     * @param   absMessagesVector  input array (all messages)
     */
    private static int getAllMessagesFromHistory(TLVector<TLAbsMessage> messages, TLVector<TLAbsMessage> absMessagesVector) {
        int count = 0;
        for (TLAbsMessage absMessage: absMessagesVector){
            // message should be TLMessage or TLMessageService, not TLMessageEmpty
            if (!(absMessage instanceof TLMessageEmpty)){
                messages.add(absMessage);
                count++;
            }
        }
        return count;
    }

    /**
     * Gets message history (except empty messages). Telegram returns only 100 messages at maximum by default -> returns messages in chunks with offsets.
     * @param	api  TelegramApi instance for RPC request
     * @param   dialog  dialog
     * @param   chatsHashMap    chats hashtable
     * @param   usersHashMap    users hashtable
     * @param   limit   maximum number of retrieved messages from each dialog (0 if need to get all the messages from dialog)
     * @see TelegramApi
     * @see AbsApiState
     */
    public static TLVector<TLAbsMessage> getWholeMessagesHistory(TelegramApi api, TLDialog dialog, HashMap<Integer, TLAbsChat> chatsHashMap, HashMap<Integer, TLAbsUser> usersHashMap, HashMap<Integer, TLAbsMessage> messagesHashMap, int limit) {
        // sleep time (Telegram can send FLOOD WAIT ERROR if the requests are done too often)
        int sleepTime = 1;
        if ((limit > 1000) || (limit == 0)){sleepTime = 100;}
        TLVector<TLAbsMessage> messages = new TLVector<>();
        Set<Integer> messageIdSet = new HashSet<>();
        Integer offId = initOffsetsId(dialog, messagesHashMap); // offset id
        Integer offDate = initOffsetsDate(dialog, messagesHashMap); // offset date
        int receivedMsgs = 0; // received messages
        int iter = 0;
        while (receivedMessagesCheck(receivedMsgs, limit)) {
            TLRequestMessagesGetHistory getHistory = SetTLObjectsMethods.getHistoryRequestSet(dialog, chatsHashMap, usersHashMap, 100, offDate, offId);
            // try to get messages (in recusrsion), use 0 as initial depth
            TLAbsMessages absMessages = sleepAndRequestMessages(api, getHistory, sleepTime, 0);
            // if returns no messages -> break the loop
            if (absMessages.getMessages().isEmpty() || absMessages == null || absMessages.getMessages() == null) { break; }
            // update known users and chats hashmaps
            DataStructuresMethods.insertIntoChatsHashMap(chatsHashMap, absMessages.getChats());
            DataStructuresMethods.insertIntoUsersHashMap(usersHashMap, absMessages.getUsers());
            // abstract messages
            TLVector<TLAbsMessage> absMessagesVector = absMessages.getMessages();
            // collect non-empty ones
            getNonEmptyMessagesFromHistory(messages, absMessagesVector, messageIdSet);
            receivedMsgs = messages.size();
            // if returns number of messages less than the chunk size (100) - end of the chat -> break the loop
            if (absMessagesVector.size() < 100) {break;}
            // offsets: id and date of last message
            offId = resetOffsetsId(messages.get(messages.size() - 1));
            offDate = resetOffsetsDate(messages.get(messages.size() - 1));

            // sleep once per 1000 messages for 1 sec
            if (iter >=10){
                try {
                    Thread.sleep(1000);
                    iter = 0;
                } catch (InterruptedException e) {
                }
            } else {iter++;}

        }
        if ((messages.size() > limit) && (limit != 0)){
            int delta = messages.size() - limit;
            messages.subList(messages.size()-1-delta,messages.size()-1).clear();
        }
        return messages;
    }

    /**
     * Try 3 times to get messages (until depth <4), if fails - return null. Each time sleep time increases x3.
     * @param api TelegramApi instance for RPC request
     * @param getHistory request object
     * @param sleepTime sleep time
     * @param depth recursion depth (set default depth as 1)
     * @see TelegramApi
     * @see TLRequestMessagesGetHistory
     */
    private static TLAbsMessages sleepAndRequestMessages(TelegramApi api, TLRequestMessagesGetHistory getHistory, int sleepTime, int depth){
        Integer time;
        if ((depth != 0) && (sleepTime < 1000)){
            time = 1000;
        } else {
            time = Integer.valueOf(sleepTime);
        }
        TLAbsMessages absMessages = null;
        // sleep
        try { Thread.sleep(time); } catch (InterruptedException e) { System.err.println("Depth: "+ depth + ", Sleep time: " + time + " can't sleep " + e.getMessage()); }
        // try to get messages, in case of fail fail - try again, but later
        if (depth < 2){
            try {
                absMessages = api.doRpcCall(getHistory);
            } catch (RpcException e) {
                int timeSleep = time;
                if (e.getErrorTag().startsWith("FLOOD_WAIT_")){
                    try {
                        String timeSleepString = e.getErrorTag().replaceAll("FLOOD_WAIT_", "");
                        timeSleep = Integer.valueOf(timeSleepString)*1000;
                    } catch (Error er){}
                }
                System.err.println("RPC: "+e.getErrorTag()+ " " + e.getErrorCode());
                System.err.println("Depth: "+ depth + ", Sleep time: " + timeSleep + " " + e.getErrorTag() + " " + e.getErrorCode());
                absMessages = sleepAndRequestMessages(api, getHistory, timeSleep, ++depth);

            } catch (TimeoutException | IOException e) {
                System.err.println("TIMEOUT/IEO : "+ e.getMessage());
                System.err.println("Depth: "+ depth + ", Sleep time: " + time*10 + " " + e.getMessage());
                absMessages = sleepAndRequestMessages(api, getHistory, time*10, ++depth);
            }
        }
        return absMessages;
    }

    /**
     * Writes only non-empty messages to 1st array from 2nd one
     * @param messages output array (only users' messages)
     * @param absMessagesVector input array (all messages)
     * @param messageIdSet set of unique message IDs (to exclude duplications when overlapping arrays are being merged)
     */
    private static void getNonEmptyMessagesFromHistory(TLVector<TLAbsMessage> messages, TLVector<TLAbsMessage> absMessagesVector, Set<Integer> messageIdSet) {
        for (TLAbsMessage absMessage: absMessagesVector){
            // message should not be TLMessageEmpty (-> TLMessage or TLMessageService)
            if (!(absMessage instanceof TLMessageEmpty)){
                if (absMessage instanceof TLMessage){
                    if (!(messageIdSet.contains(((TLMessage) absMessage).getId()))){
                        messages.add(absMessage);
                        messageIdSet.add(((TLMessage) absMessage).getId());
                    }
                } else if (absMessage instanceof TLMessageService){
                    if (!(messageIdSet.contains(((TLMessageService) absMessage).getId()))){
                        messages.add(absMessage);
                        messageIdSet.add(((TLMessageService) absMessage).getId());
                    }
                }
            }
        }
    }

    /**
     * Initiates offset id
     * @param dialog dialog
     * @param messagesHashMap top messages
     */
    private static int initOffsetsId(TLDialog dialog, HashMap<Integer, TLAbsMessage> messagesHashMap){
        TLAbsMessage msg = messagesHashMap.get(dialog.getPeer().getId());
        int offId;
        if (msg instanceof TLMessage){
            offId = ((TLMessage) msg).getId();
        } else if (msg instanceof TLMessageService){
            offId = ((TLMessageService) msg).getId();
        } else {
            offId = 0;
        }
        return offId;
    }

    /**
     * Initiates offset date
     * @param dialog dialog
     * @param messagesHashMap top messages
     */
    private static int initOffsetsDate(TLDialog dialog, HashMap<Integer, TLAbsMessage> messagesHashMap){
        TLAbsMessage msg = messagesHashMap.get(dialog.getPeer().getId());
        int offDate;
        if (msg instanceof TLMessage){
            offDate = ((TLMessage) msg).getDate();
        } else if (msg instanceof TLMessageService){
            offDate = ((TLMessageService) msg).getDate();
        } else {
            offDate = 0;
        }
        return offDate;
    }

    /**
     * Reset offset id
     * @param msg message
     */
    private static int resetOffsetsId(TLAbsMessage msg){
        int offId;
        if (msg instanceof TLMessage){
            offId = ((TLMessage) msg).getId();
        } else if (msg instanceof TLMessageService){
            offId = ((TLMessageService) msg).getId();
        } else {
            offId = 0;
        }
        return offId;
    }

    /**
     * Reset offset date
     * @param msg message
     */
    private static int resetOffsetsDate(TLAbsMessage msg){
        int offDate;
        if (msg instanceof TLMessage){
            offDate = ((TLMessage) msg).getDate();
        } else if (msg instanceof TLMessageService){
            offDate = ((TLMessageService) msg).getDate();
        } else {
            offDate = 0;
        }
        return offDate;
    }

}
