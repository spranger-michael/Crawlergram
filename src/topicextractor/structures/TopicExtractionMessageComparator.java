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
        return d1.getDate().compareTo(d2.getDate()) == 0 ? d1.getId().compareTo(d2.getId()) : d1.getDate().compareTo(d2.getDate());
    }
}
