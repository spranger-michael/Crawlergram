/*
 * Title: CrawlerMain.java
 * Project: telegramJ
 * Creator: mikriuko
 */

package crawler.impl.methods.setobjects;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Scanner;

import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.chat.channel.TLChannel;
import org.telegram.api.chat.channel.TLChannelForbidden;
import org.telegram.api.dialog.TLDialog;
import org.telegram.api.functions.auth.TLRequestAuthImportAuthorization;
import org.telegram.api.functions.auth.TLRequestAuthSendCode;
import org.telegram.api.functions.auth.TLRequestAuthSignIn;
import org.telegram.api.functions.auth.TLRequestAuthSignUp;
import org.telegram.api.functions.messages.TLRequestMessagesGetDialogs;
import org.telegram.api.functions.messages.TLRequestMessagesGetHistory;
import org.telegram.api.functions.upload.TLRequestUploadGetFile;
import org.telegram.api.input.filelocation.TLAbsInputFileLocation;
import org.telegram.api.input.filelocation.TLInputDocumentFileLocation;
import org.telegram.api.input.filelocation.TLInputFileLocation;
import org.telegram.api.input.peer.*;
import org.telegram.api.peer.TLAbsPeer;
import org.telegram.api.peer.TLPeerChannel;
import org.telegram.api.peer.TLPeerChat;
import org.telegram.api.peer.TLPeerUser;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.tl.TLBytes;

public class SetTLObjectsMethods {

    /**
     * Sets TLRequestAuthImportAuthorization
     * @param	authKey	auth key
     * @param	userId	user id
     * @see	TLRequestAuthImportAuthorization
     */
    public static TLRequestAuthImportAuthorization importAuthSet(TLBytes authKey, int userId){
        TLRequestAuthImportAuthorization authReq = new TLRequestAuthImportAuthorization();
        authReq.setBytes(authKey);
        authReq.setId(userId);
        return authReq;
    }

    /**
     * Sets TLRequestAuthImportAuthorization
     * @param	apiHash api hash
     * @param	apiKey api key
     * @param	phoneNum phone number
     * @see	TLRequestAuthImportAuthorization
     */
    public static TLRequestAuthSendCode sendCodeSet(String apiHash, int apiKey, String phoneNum){
        TLRequestAuthSendCode sendCode = new TLRequestAuthSendCode();
        sendCode.setApiHash(apiHash);
        sendCode.setApiId(apiKey);
        sendCode.setPhoneNumber(phoneNum);
        return sendCode;
    }

    /**
     * Sets TLRequestAuthSignIn
     * @param	phonenumber	number of the phone to sign in
     * @param	phoneCodeHash	hash of the phone code from sent code
     * @see	TLRequestAuthSignIn
     */
    public static TLRequestAuthSignIn signInSet(String phonenumber, String phoneCodeHash){
        TLRequestAuthSignIn signIn = new TLRequestAuthSignIn();
        Scanner reader = new Scanner(System.in);
        System.out.println("Enter a number : ");
        String n = reader.next();
        reader.close();
        signIn.setPhoneNumber(phonenumber);
        signIn.setPhoneCodeHash(phoneCodeHash);
        signIn.setPhoneCode(n);
        return signIn;
    }

    /**
     * Sets TLRequestAuthSignUp
     * @param	phonenumber	number of the phone to sign in
     * @param	phoneCodeHash	hash of the phone code from sent code
     * @param	name first name
     * @param	surname last name
     * @see	TLRequestAuthSignUp
     */
    public static TLRequestAuthSignUp signUpSet(String phonenumber, String phoneCodeHash, String name, String surname){
        TLRequestAuthSignUp signUp = new TLRequestAuthSignUp();
        Scanner reader = new Scanner(System.in);
        System.out.println("Enter a number: ");
        String n = reader.next();
        reader.close();
        signUp.setPhoneNumber(phonenumber);
        signUp.setPhoneCodeHash(phoneCodeHash);
        signUp.setPhoneCode(n);
        signUp.setFirstName(name);
        signUp.setFirstName(surname);
        return signUp;
    }


    /**
     * Sets TLRequestMessagesGetDialogs
     * @param	limit   maximal number of dialogs to be retrieved
     * @param	offId	message Id to be used as offset (use 0 as default)
     * @param	peer	peer used as offset (use TLInputPeerEmpty as default
     * @param	date    date used as offset
     * @see	TLRequestMessagesGetDialogs
     */
    public static TLRequestMessagesGetDialogs getDialogsSet(int limit, int offId, TLAbsInputPeer peer, int date){
        TLRequestMessagesGetDialogs getDialogs = new TLRequestMessagesGetDialogs();
        getDialogs.setOffsetId(offId);
        getDialogs.setOffsetPeer(peer);
        getDialogs.setLimit(limit);
        getDialogs.setOffsetDate(date);
        return getDialogs;
    }

    /**
     * Sets TLRequestMessagesGetDialogs (returns all dialogs): offId = 0, peer = new TLInputPeerEmpty()
     * @see	TLRequestMessagesGetDialogs
     */
    public static TLRequestMessagesGetDialogs getDialogsSet(){
        TLRequestMessagesGetDialogs getDialogs = new TLRequestMessagesGetDialogs();
        getDialogs.setOffsetId(0);
        getDialogs.setLimit(100); // default Telegram server value
        getDialogs.setOffsetPeer(new TLInputPeerEmpty());
        return getDialogs;
    }

    /**
     * Sets TLRequestMessagesGetDialogs (returns all dialogs): offId = 0, peer = new TLInputPeerEmpty()
     * @param	peer	peer used as offset (use TLInputPeerEmpty as default
     * @see	TLRequestMessagesGetDialogs
     */
    public static TLRequestMessagesGetDialogs getDialogsSet2(int id){
        TLRequestMessagesGetDialogs getDialogs = new TLRequestMessagesGetDialogs();
        getDialogs.setOffsetId(id);
        getDialogs.setLimit(3); // default Telegram server value
        getDialogs.setOffsetPeer(new TLInputPeerEmpty());
        return getDialogs;
    }

    /**
     * Sets TLRequestMessagesGetHistory
     * @param	peer	input peer
     * @param   limit   maximal number of messages to be retrieved (use the number not higher than 100)
     * @param   offDate   date used as offset
     * @param	offId	message Id to be used as offset (use 0 as default)
     * @param	minId	messages with id lower than this will be excluded (use 0 as default)
     * @param	maxId	messages with id higher than this will be excluded (use 0 as default)
     * @param	addOff	additional offset (use 0 as default)
     * @see	TLRequestMessagesGetHistory
     */
    public static TLRequestMessagesGetHistory getMesHistSet(TLAbsInputPeer peer, int limit, int offDate, int offId, int minId, int maxId, int addOff){
        TLRequestMessagesGetHistory getMesHist = new TLRequestMessagesGetHistory();
        getMesHist.setPeer(peer);
        getMesHist.setLimit(limit);
        getMesHist.setOffsetDate(offDate);
        getMesHist.setOffsetId(offId);
        getMesHist.setMinId(minId);
        getMesHist.setMaxId(maxId);
        getMesHist.setAddOffset(addOff);
        return getMesHist;
    }

    /**
     * Sets TLRequestMessagesGetHistory (returns N last messages)
     * @param	peer	input peer
     * @param   limit   maximal number of messages to be retrieved (use the number not higher than 100)
     * @see	TLRequestMessagesGetHistory
     */
    public static TLRequestMessagesGetHistory getMesHistSet(TLAbsInputPeer peer, int limit){
        TLRequestMessagesGetHistory getMesHist = new TLRequestMessagesGetHistory();
        getMesHist.setPeer(peer);
        getMesHist.setLimit(limit);
        getMesHist.setOffsetId(0);
        getMesHist.setMinId(0);
        getMesHist.setMaxId(0);
        getMesHist.setAddOffset(0);
        return getMesHist;
    }

    /**
     * Sets TLRequestMessagesGetHistory (returns N last messages with given offset)
     * @param	peer	input peer
     * @param   limit   maximal number of messages to be retrieved (use the number not higher than 100)
     * @param   offDate offset date
     * @param   offId  offset id
     * @see	TLRequestMessagesGetHistory
     */
    public static TLRequestMessagesGetHistory getMesHistSet(TLAbsInputPeer peer, int limit, int offDate, int offId){
        TLRequestMessagesGetHistory getMesHist = new TLRequestMessagesGetHistory();
        getMesHist.setPeer(peer);
        getMesHist.setLimit(limit);
        getMesHist.setOffsetId(offId);
        getMesHist.setMinId(0);
        getMesHist.setMaxId(0);
        getMesHist.setAddOffset(0);
        getMesHist.setOffsetDate(offDate);
        return getMesHist;
    }

    /**
     * Sets TLInputPeerChannel
     * @param	id	channel id
     * @param   accessHash   access hash
     * @see	TLInputPeerChannel
     */
    public static TLInputPeerChannel inputPeerChannelSet(int id, long accessHash){
        TLInputPeerChannel channel = new TLInputPeerChannel();
        channel.setChannelId(id);
        channel.setAccessHash(accessHash);
        return channel;
    }

    /**
     * Sets TLInputPeerUser
     * @param	id	user id
     * @param   accessHash   access hash
     * @see	TLInputPeerUser
     */
    public static TLInputPeerUser inputPeerUserSet(int id, long accessHash){
        TLInputPeerUser user = new TLInputPeerUser();
        user.setUserId(id);
        user.setAccessHash(accessHash);
        return user;
    }

    /**
     * Sets TLInputPeerChat
     * @param	id	chat id
     * @see	TLInputPeerChat
     */
    public static TLInputPeerChat inputPeerChatSet(int id){
        TLInputPeerChat chat = new TLInputPeerChat();
        chat.setChatId(id);
        return chat;
    }

    /**
     * Sets TLInputFileLocation
     * @param	localId local id
     * @param	volumeID    volume id
     * @see	TLInputFileLocation
     */
    public static TLInputFileLocation inputFileLocationSet(int localId, long secret, long volumeID){
        TLInputFileLocation inputFileLoc = new TLInputFileLocation();
        inputFileLoc.setLocalId(localId);
        inputFileLoc.setSecret(secret);
        inputFileLoc.setVolumeId(volumeID);
        return inputFileLoc;
    }

    /**
     * Sets TLInputDocumentFileLocation
     * @param	id  id
     * @param	accessHash  access hash
     * @param   version document version
     * @see	TLInputDocumentFileLocation
     */
    public static TLInputDocumentFileLocation inputDocumentFileLocationSet(long id, long accessHash, int version){
        TLInputDocumentFileLocation inputDocFileLoc = new TLInputDocumentFileLocation();
        inputDocFileLoc.setId(id);
        inputDocFileLoc.setAccessHash(accessHash);
        inputDocFileLoc.setVersion(version);
        return inputDocFileLoc;
    }

    /**
     * Sets TLRequestUploadGetFile
     * @param	offset  offset in bytes
     * @param	limit   limit in bytes
     * @see	TLRequestUploadGetFile
     */
    public static TLRequestUploadGetFile getFileSet(TLAbsInputFileLocation absInputFileLocation, int offset, int limit){
        TLRequestUploadGetFile getFileReq = new TLRequestUploadGetFile();
        getFileReq.setLocation(absInputFileLocation);
        getFileReq.setOffset(offset);
        getFileReq.setLimit(limit);
        return getFileReq;
    }

    /**
     * Wrapper of getMesHistSet
     * @param   dialog    dialog
     * @param   chatsHashMap    chats hashmap
     * @param   usersHashMap    users hashmap
     * @param   limit   maximum number of retrieved messages from each dialog
     * @param   offDate offset date
     * @param   offId  offset id
     * @see TLRequestMessagesGetHistory
     * @see HashMap <Integer,  TLAbsUser >
     * @see HashMap<Integer,  TLAbsChat >
     */
    public static TLRequestMessagesGetHistory getHistoryRequestSet(TLDialog dialog, HashMap<Integer, TLAbsChat> chatsHashMap, HashMap<Integer, TLAbsUser> usersHashMap, int limit, int offDate, int offId){
        TLRequestMessagesGetHistory getHistory = new TLRequestMessagesGetHistory();
        int peerId = dialog.getPeer().getId();
        // if dialog is a channel
        if (dialog.getPeer() instanceof TLPeerChannel) {
            TLInputPeerChannel inputPeerChannel = getInputPeerChannelSet(peerId, chatsHashMap);
            getHistory = getMesHistSet(inputPeerChannel, limit, offDate, offId);
            // if dialog is an user
        } else if (dialog.getPeer() instanceof TLPeerUser) {
            TLInputPeerUser inputPeerUser = getInputPeerUserSet(peerId, usersHashMap);
            getHistory = getMesHistSet(inputPeerUser, limit, offDate, offId);
            //if dialog is a chat
        } else if (dialog.getPeer() instanceof TLPeerChat) {
            TLInputPeerChat inputPeerChat = inputPeerChatSet(peerId);
            getHistory = getMesHistSet(inputPeerChat, limit, offDate, offId);
        }
        return getHistory;
    }

    /**
     * Gets TLAbsInputPeer using the TLAbsPeer instance
     * @param   peer    peer
     * @param   chatsHashMap    chats hashmap
     * @param   usersHashMap    users hashmap
     * @see TLAbsInputPeer
     * @see HashMap <Integer,  TLAbsUser >
     * @see HashMap<Integer,  TLAbsChat >
     */
    public static TLAbsInputPeer getAbsInputPeerSet(TLAbsPeer peer, HashMap<Integer, TLAbsChat> chatsHashMap, HashMap<Integer, TLAbsUser> usersHashMap){
        TLAbsInputPeer absPeer = new TLInputPeerEmpty();
        int peerId = peer.getId();
        if (peer instanceof TLPeerChannel) {
            absPeer = getInputPeerChannelSet(peerId, chatsHashMap);
            // if peer is an user
        } else if (peer instanceof TLPeerUser) {
            absPeer = getInputPeerUserSet(peerId, usersHashMap);
            //if peer is a chat
        } else if (peer instanceof TLPeerChat) {
            absPeer = inputPeerChatSet(peerId);
        }
        return absPeer;
    }

    /**
     * Wrapper of inputPeerChannelSet
     * @param   peerId peer id
     * @param  chatsHashMap hashmap with chats
     * @see  TLInputPeerChannel
     * @see Hashtable <Integer, TLAbsChat>
     */
    private static TLInputPeerChannel getInputPeerChannelSet(int peerId, HashMap<Integer, TLAbsChat> chatsHashMap){
        TLInputPeerChannel inputPeerChannel = new TLInputPeerChannel();
        // ... regular channel or...
        if (chatsHashMap.get(peerId) instanceof TLChannel) {
            TLChannel channel = (TLChannel) chatsHashMap.get(peerId);
            inputPeerChannel = inputPeerChannelSet(peerId, channel.getAccessHash());
            // ... forbidden channel
        } else if (chatsHashMap.get(peerId) instanceof TLChannelForbidden) {
            TLChannelForbidden channel = (TLChannelForbidden) chatsHashMap.get(peerId);
            inputPeerChannel = inputPeerChannelSet(peerId, channel.getAccessHash());
        }
        return  inputPeerChannel;
    }

    /**
     * Wrapper of inputPeerUserSet
     * @param   peerId peer id
     * @param  usersHashMap hashmap with users
     * @see  TLInputPeerUser
     * @see Hashtable<Integer, TLAbsUser>
     */
    private static TLInputPeerUser getInputPeerUserSet(int peerId, HashMap<Integer, TLAbsUser> usersHashMap){
        TLUser user = (TLUser) usersHashMap.get(peerId);
        TLInputPeerUser inputPeerUser = SetTLObjectsMethods.inputPeerUserSet(peerId, user.getAccessHash());
        return inputPeerUser;
    }

}
