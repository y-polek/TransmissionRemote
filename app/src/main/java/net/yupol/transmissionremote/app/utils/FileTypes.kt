package net.yupol.transmissionremote.app.utils

import androidx.annotation.DrawableRes
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.model.json.File

@DrawableRes
fun fileTypeIcon(extension: String): Int {
    return when (extension.toLowerCase()) {
        "3g2", "3gp", "avi", "flv", "h264", "m4v", "mkv",
        "mov", "mp4", "mpg", "mpeg", "rm", "swf", "vob",
        "wmv"
        -> R.drawable.ic_file_video

        "mp3", "flac", "m3u", "ogg", "wav", "aif", "cda",
        "mid", "midi", "mpa", "wma", "wpl"
        -> R.drawable.ic_file_audio

        "ai", "bmp", "gif", "ico", "jpeg", "jpg", "png",
        "ps", "psd", "svg", "tif", "tiff"
        -> R.drawable.ic_file_image

        "7z", "arj", "deb", "pkg", "rar", "rpm", "tar",
        "gz", "z", "zip"
        -> R.drawable.ic_file_archive

        "txt", "sub", "str", "csv", "conf", "lst", "text",
        "manifest", "sha1", "readme"
        -> R.drawable.ic_file_text

        "pdf" -> R.drawable.ic_file_pdf

        "doc", "docx", "odt", "rtf", "tex", "wks", "wps", "wpd"
        -> R.drawable.ic_file_word

        "c", "class", "cpp", "cs", "h", "htm", "html",
        "java", "sh", "swift", "vb", "xml"
        -> R.drawable.ic_file_code

        "ods", "xlr", "xls", "xlsx"
        -> R.drawable.ic_file_excel

        "key", "odp", "pps", "ppt", "pptx"
        -> R.drawable.ic_file_powerpoint

        else -> R.drawable.ic_file_regular
    }
}

fun String.extension(): String {
    val dotIdx = lastIndexOf('.')
    if (dotIdx <= 0) return ""

    return substring(dotIdx + 1)
}
