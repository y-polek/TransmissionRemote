package net.yupol.transmissionremote.app.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import net.yupol.transmissionremote.app.R;

import org.apache.commons.io.FilenameUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum FileType {

    FILE(R.drawable.ic_file_type_default, ""),
    TEXT(R.drawable.ic_file_type_text, "txt", "sub", "str", "csv", "conf", "lst", "text", "manifest", "sha1", "readme"),
    IMAGE(R.drawable.ic_file_type_image, "ai", "bmp", "gif", "ico", "jpeg", "jpg", "png", "ps", "psd", "svg", "tif", "tiff"),
    ARCHIVE(R.drawable.ic_file_type_archive, "7z", "arj", "deb", "pkg", "rar", "rpm", "tar", "gz", "z", "zip"),
    AUDIO(R.drawable.ic_file_type_audio, "aif", "cda", "mid", "midi", "mp3", "mpa", "ogg", "wav", "wma", "wpl"),
    VIDEO(R.drawable.ic_file_type_video, "3g2", "3gp", "avi", "flv", "h264", "m4v", "mkv", "mov", "mp4", "mpg", "mpeg", "rm", "swf", "vob", "wmv"),
    CODE(R.drawable.ic_file_type_code, "c", "class", "cpp", "cs", "h", "htm", "html", "java", "sh", "swift", "vb", "xml"),
    PDF(R.drawable.ic_file_type_pdf, "pdf"),
    WORD(R.drawable.ic_file_type_word, "doc", "docx", "odt", "rtf", "tex", "wks", "wps", "wpd"),
    SPREADSHEET(R.drawable.ic_file_type_excel, "ods", "xlr", "xls", "xlsx"),
    PRESENTATION(R.drawable.ic_file_type_powerpoint, "key", "odp", "pps", "ppt", "pptx");

    @DrawableRes public final int iconRes;
    private final Set<String> extensions;

    FileType(@DrawableRes int iconRes, String... extensions) {
        this.iconRes = iconRes;
        this.extensions = new HashSet<>(Arrays.asList(extensions));
    }

    public static FileType byFileName(@NonNull String name) {
        String extension = FilenameUtils.getExtension(name).toLowerCase();
        for (FileType type : values()) {
            if (type.extensions.contains(extension)) return type;
        }
        return FILE;
    }
}
