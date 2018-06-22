/*
 * Title: MessageMergingMethods.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

package topicextractor.extractormethods;

import topicextractor.maths.gaussnewton.ExpRegMethods;
import topicextractor.maths.gaussnewton.GaussNewton;
import topicextractor.maths.gaussnewton.NoSquareException;
import topicextractor.structures.TopicExtractionDialog;
import topicextractor.structures.TopicExtractionMessage;
import topicextractor.structures.TopicExtractionMessageComparator;

import java.util.*;

public class MessageMergingMethods {

    /**
     * Merges short messages of supergroups and chats to larger messages (merges by topic).
     * We assume that topic can be defined from time of respond.
     * If two messages were written in a short time interval they are likely to have the same context or topic.
     * Such messages can be used to define most popular topics of the chat (e.g. sport, news, etc.).
     * We need to merge only messages from Chats, supergroups (type of Channels) and Users.
     * @param dialog dialog
     * @param msgs original messages list
     * @param docThreshold if chat has very low number of messages (< docThreshold) -> all chat is merged
     */
    public static List<TopicExtractionMessage> mergeMessages(TopicExtractionDialog dialog,
                                                             List<TopicExtractionMessage> msgs,
                                                             int docThreshold){
        List<TopicExtractionMessage> merged = new LinkedList<>();
        //if chat, supergroup or user (not regular channel)
        // if flags' 9th bit is "1" - channel is supergroup (0001 0000 0000 = 256d)
        if (!(dialog.getType().equals("Channel") && ((dialog.getFlags() & 256) == 0))) {
                merged = mergeChat(msgs, docThreshold);
        } else {
            merged = msgs;
        }
        return merged;
    }

    /**
     * merges (long snd short) chats to docs
     * @param   messages  messages
     * @param   docThreshold threshold
     */
    private static List<TopicExtractionMessage> mergeChat(List<TopicExtractionMessage> messages, int docThreshold){
        // if number of messages < docThreshold - short chat, else - long chat
        return ((messages.size() < docThreshold) && (messages.size() > 0)) ? mergeShortChat(messages) : apiLongChatsToDocs(messages);
    }

    /**
     * merges short chat messages (number of messages < threshold) to one message
     * @param   messages  all messages
     */
    private static List<TopicExtractionMessage> mergeShortChat(List<TopicExtractionMessage> messages) {
        List<TopicExtractionMessage> merged = new LinkedList<>();
        String text = "";
        for (TopicExtractionMessage message: messages){
            if (!message.getText().isEmpty()){
                text += message.getText() + "\n";
            }
        }
        if (!text.isEmpty()){
            // id and date of last message are taken
            TopicExtractionMessage first = messages.get(0);
            merged.add(new TopicExtractionMessage(first.getId(), text, first.getDate(), first.getReplyTo()));
        }
        return merged;
    }

    /**
     * merges long chat messages (number of messages > threshold) if they fit time interval
     * @param   messages  "clean" messages (without empty and service messages)
     */
    private static List<TopicExtractionMessage> apiLongChatsToDocs(List<TopicExtractionMessage> messages) {
        Collections.sort(messages, new TopicExtractionMessageComparator());
        // get intervals between messages to array
        List<Integer> dates = new ArrayList<>();
        for (TopicExtractionMessage message: messages){
            dates.add(message.getDate());
        }
        // deltas between intervals
        List<Integer> deltas = new ArrayList<>();
        for (int i = 0; i < dates.size()-1; i++){
            deltas.add(dates.get(i) - dates.get(i+1));
        }
        // unique deltas
        Set<Integer> deltasUnique = new HashSet<>(deltas);
        // unique deltas counts
        List<Integer> deltasUniqueCounts = new ArrayList<>();
        for (Integer deltaUnique: deltasUnique){
            Integer count = 0;
            for (Integer delta: deltas){
                if (deltaUnique.equals(delta)){
                    count++;
                }
            }
            deltasUniqueCounts.add(count);
        }
        // Gauss-Newton implementation for fitting
        GaussNewton gn = new GaussNewton() {
            @Override
            public double findY(double x, double[] b) {
                return b[0] * Math.exp(-b[1] * x);
            }
        };
        // values initialization and optimisation
        double[] expModelInit = ExpRegMethods.expRegInitVals(ExpRegMethods.setToDoubles(deltasUnique), ExpRegMethods.listToDoubles(deltasUniqueCounts));
        double[] expModel = new double[2];
        try {
            expModel = gn.optimise(ExpRegMethods.setToDoubles2D(deltasUnique), ExpRegMethods.listToDoubles(deltasUniqueCounts), expModelInit);
        } catch (NoSquareException e) {
            System.out.println(e.getMessage());
        }
        int timeThreshold = (int) Math.ceil(ExpRegMethods.mathTimeThresholdCount(expModel[1], 0.01));
        // returns the list of merged documents
        return mergeByTime(messages, timeThreshold);
    }

    /**
     * concatenates messages to one, if delta time between them is lower than time threshold
     * @param messages documents
     * @param timeThreshold maximum time between messages in one doc
     */
    private static List<TopicExtractionMessage> mergeByTime(List<TopicExtractionMessage> messages, int timeThreshold){
        List<TopicExtractionMessage> mesCopy = new LinkedList<>(messages);
        for (int i = 0; i < mesCopy.size() - 1; i++){
            // date of the current and next documents
            TopicExtractionMessage d0 = mesCopy.get(i);
            TopicExtractionMessage d1 = mesCopy.get(i+1);
            // threshold criterion
            if (d0.getDate() - d1.getDate() <= timeThreshold){
                mesCopy.set(i, new TopicExtractionMessage(d0.getId(), d0.getText() + "\n" + d1.getText(), d0.getDate(), d0.getReplyTo()));
                mesCopy.remove(i+1);
                // returns i back each time, when we have merge of the messages, to check for multiple merges in row
                i--;
            }
        }
        return mesCopy;
    }

}
