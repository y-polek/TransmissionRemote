package net.yupol.transmissionremote.app.torrentdetails;

import net.yupol.transmissionremote.app.model.Dir;
import net.yupol.transmissionremote.app.model.json.FileStat;

public class FilesPageBindingUtils {

    public static boolean isDirChecked(Dir dir, FileStat[] fileStats) {
        if (dir == null || fileStats == null) return false;
        return isDirWanted(dir, fileStats);
    }

    private static boolean isDirWanted(Dir dir, FileStat[] fileStats) {
        for (Integer fileIndex : dir.getFileIndices()) {
            if (!fileStats[fileIndex].isWanted()) return false;
        }
        for (Dir subDir : dir.getDirs()) {
            if (!isDirWanted(subDir, fileStats)) return false;
        }

        return true;
    }

    public static boolean isFileChecked(FileStat fileStat) {
        return fileStat != null && fileStat.isWanted();
    }
}
