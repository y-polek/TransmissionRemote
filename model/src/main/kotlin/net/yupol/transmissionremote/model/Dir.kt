package net.yupol.transmissionremote.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.yupol.transmissionremote.model.json.File
import java.util.*

@Parcelize
class Dir private constructor(
        val name: String,
        val dirs: MutableList<Dir>,
        val fileIndices: MutableList<Int>) : Parcelable
{

    constructor(name: String) : this(name, mutableListOf<Dir>(), mutableListOf<Int>()) {}

    companion object {

        @JvmStatic
        fun createFileTree(files: Array<File>): Dir {
            val root = Dir("/")

            for (i in files.indices) {
                val pathParts = files[i].path.split('/').dropWhile { it.isEmpty() }
                parsePath(pathParts, root, i)
            }

            sortRecursively(root, files.map { it.path.split('/').last() })

            return root
        }

        private fun sortRecursively(dir: Dir, fileNames: List<String>) {
            dir.dirs.sortBy { it.name }
            dir.fileIndices.sortBy { fileNames[it] }
            dir.dirs.forEach { sortRecursively(it, fileNames) }
        }

        @JvmStatic
        fun filesInDirRecursively(dir: Dir): List<Int> {
            val fileIndices = LinkedList<Int>()
            collectFilesInDir(dir, fileIndices)
            return fileIndices
        }

        private fun collectFilesInDir(dir: Dir, fileIndices: MutableList<Int>) {
            fileIndices.addAll(dir.fileIndices)
            for (subDir in dir.dirs) {
                collectFilesInDir(subDir, fileIndices)
            }
        }

        private fun parsePath(pathParts: List<String>, parentDir: Dir, fileIndex: Int) {
            if (pathParts.size == 1) {
                parentDir.fileIndices.add(fileIndex)
                return
            }

            val dirName = pathParts[0]
            var dir = findDirWithName(dirName, parentDir.dirs)
            if (dir == null) {
                dir = Dir(dirName)
                parentDir.dirs.add(dir)
            }
            parsePath(pathParts.subList(1, pathParts.size), dir, fileIndex)
        }

        private fun findDirWithName(name: String, dirs: List<Dir>): Dir? {
            return dirs.find {
                it.name == name
            }
        }
    }
}
