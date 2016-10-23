package net.yupol.transmissionremote.app.model;

import android.support.annotation.NonNull;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.typeface.IIcon;

import org.apache.commons.io.FilenameUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum FileType {

    FILE(FontAwesome.Icon.faw_file_o, ""),
    TEXT(FontAwesome.Icon.faw_file_text_o, "txt", "sub", "str", "csv", "conf", "lst", "text", "manifest", "sha1", "readme"),
    IMAGE(FontAwesome.Icon.faw_file_image_o, "ai", "bmp", "gif", "ico", "jpeg", "jpg", "png", "ps", "psd", "svg", "tif", "tiff"),
    ARCHIVE(FontAwesome.Icon.faw_file_archive_o, "7z", "arj", "deb", "pkg", "rar", "rpm", "tar", "gz", "z", "zip"),
    AUDIO(FontAwesome.Icon.faw_file_audio_o, "aif", "cda", "mid", "midi", "mp3", "mpa", "ogg", "wav", "wma", "wpl"),
    VIDEO(FontAwesome.Icon.faw_file_video_o, "3g2", "3gp", "avi", "flv", "h264", "m4v", "mkv", "mov", "mp4", "mpg", "mpeg", "rm", "swf", "vob", "wmv"),
    CODE(FontAwesome.Icon.faw_file_code_o, "c", "class", "cpp", "cs", "h", "htm", "html", "java", "sh", "swift", "vb", "xml"),
    PDF(FontAwesome.Icon.faw_file_pdf_o, "pdf"),
    WORD(FontAwesome.Icon.faw_file_word_o, "doc", "docx", "odt", "rtf", "tex", "wks", "wps", "wpd"),
    SPREADSHEET(FontAwesome.Icon.faw_file_excel_o, "ods", "xlr", "xls", "xlsx"),
    PRESENTATION(FontAwesome.Icon.faw_file_powerpoint_o, "key", "odp", "pps", "ppt", "pptx");

    public final IIcon icon;
    private final Set<String> extensions;

    FileType(IIcon icon, String... extensions) {
        this.icon = icon;
        this.extensions = new HashSet<>(Arrays.asList(extensions));
    }

    public static IIcon iconFromName(@NonNull String name) {
        String extension = FilenameUtils.getExtension(name).toLowerCase();

        for (FileType type : values()) {
            if (type.extensions.contains(extension)) return type.icon;
        }
        return FILE.icon;
    }
}
