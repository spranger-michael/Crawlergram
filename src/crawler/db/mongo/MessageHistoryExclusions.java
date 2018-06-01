/*
 * Title: MessageHistoryExclusions.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

package crawler.db.mongo;

import crawler.db.DBStorage;
import org.telegram.api.dialog.TLDialog;

public class MessageHistoryExclusions {

    Integer minId; // min msg id in db
    Integer maxId; // max msg id in db
    Integer offDate; // new offset date (corresponds to min)
    Integer offId; // new offset id (minId)

    public MessageHistoryExclusions(DBStorage dbStorage, TLDialog dialog){
        this.minId = dbStorage.getMessageMinId(dialog);
        this.offId = this.minId;
        this.maxId = dbStorage.getMessageMaxId(dialog);
        this.offDate = dbStorage.getMessageMinIdDate(dialog);
    }

    public boolean exist(){
        return ((this.maxId != null) && (this.minId != null) && (this.offDate != null) && (this.offId != null));
    }

}
