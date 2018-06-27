/*
 * Title: TopicExtractionMessageComparator.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

package topicextractor.structures;

import java.util.Comparator;

public class TopicExtractionMessageComparator implements Comparator<TopicExtractionMessage> {
    @Override
    public int compare(TopicExtractionMessage d1, TopicExtractionMessage d2) {
        //if dates are equal - compare IDs
        return d2.getDate().compareTo(d1.getDate()) == 0 ? d2.getId().compareTo(d1.getId()) : d2.getDate().compareTo(d1.getDate());
    }
}
