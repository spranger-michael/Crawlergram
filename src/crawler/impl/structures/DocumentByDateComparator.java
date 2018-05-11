/*
 * Title: CrawlerMain.java
 * Project: telegramJ
 * Creator: mikriuko
 */

package crawler.impl.structures;

import java.util.Comparator;

/**
 * This comparator sorts the Documents by the date. If the dates are equal it sorts, additionally by id of the Document.
 */

public class DocumentByDateComparator implements Comparator<Document> {

    @Override
    public int compare(Document d2, Document d1) {
        //if dates are equal - compare IDs
        return d1.date.compareTo(d2.date) == 0 ? d1.id.compareTo(d2.id) : d1.date.compareTo(d2.date);
    }

}
