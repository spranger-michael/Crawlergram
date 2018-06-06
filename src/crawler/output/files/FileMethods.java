/*
 * Title: FilesMethods.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

package crawler.output.files;

import org.telegram.api.file.location.TLFileLocation;
import org.telegram.api.photo.size.TLAbsPhotoSize;
import org.telegram.api.photo.size.TLPhotoSize;
import org.telegram.tl.TLVector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileMethods {

    /**
     * Checks if the file and path to file exist. If not - creates them.
     * @param filePath file path
     */
    public static void checkFilePath(String filePath) {
        File file = new File(filePath);
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            file.createNewFile();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Creates file and directory (if not exist). Appends bytes to the file.
     * @param	filename	path to the file
     * @param	bytes	bytes for writing
     * @see    FileOutputStream
     * @see File
     */
    public static void appendBytesToFile(String filename, byte[] bytes){
        try {
            checkFilePath(filename);
            // append bytes to file
            FileOutputStream output = new FileOutputStream(filename, true);
            output.write(bytes);
            output.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Creates file and directory if not exist (recreates file, if exist). Writes  bytes to the file. DOESN'T APPEND.
     * @param	filename	path to the file
     * @param	bytes	bytes for writing
     * @see	FileOutputStream
     * @see File
     */
    public static void writeBytesToFile(String filename, byte[] bytes){
        try {
            checkFilePath(filename);
            // write bytes to file
            FileOutputStream output = new FileOutputStream(filename);
            output.write(bytes);
            output.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Calculates optimal size for a file chunk based on the original size of the file.
     * @param	size    file size
     * @return optimal part size in KB, null if file is too big
     */
    public static Integer getFilePartSize(int size){
        if (size <= 1048576){ //10 MB
            return 64;
        } else if (size <= 104857600){ //100 MB
            return 128;
        } else if (size <= 786432000){ //750 MB
            return 256;
        } else if (size <= 1572864000){ //1500 MB
            return 512;
        } else {
            return 64;
        }
    }

    /**
     * concatenates two byte arrays
     * @param a array
     * @param b array
     */
    public static byte[] concatenateByteArrays(byte[] a, byte[] b) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            if (a != null){output.write(a);}
            if (b != null){output.write(b);}
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toByteArray();
    }

    /**
     * gets the location of the largest (and last one in the list) accessible photo
     * @param apss abs photo size
     * @return doc
     */
    public static TLPhotoSize getLargestAvailablePhotoSize(TLVector<TLAbsPhotoSize> apss){
        // getting the last and largest TLPhotoSize
        TLPhotoSize aps = null;
        for (int i = apss.size()-1; i >= 0; i--){
            if (sizeAvailable(apss.get(i))){
                aps = (TLPhotoSize) apss.get(i);
                break;
            }
        }
        return aps;
    }

    /**
     * checks if file location available
     * @param aps
     * @return
     */
    private static boolean sizeAvailable(TLAbsPhotoSize aps){
        boolean f = false;
        if (aps instanceof TLPhotoSize){
            if (((TLPhotoSize) aps).getLocation() instanceof TLFileLocation){
                f = true;
            }
        }
        return f;
    }

}