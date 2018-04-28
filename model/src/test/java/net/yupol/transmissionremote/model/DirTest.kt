package net.yupol.transmissionremote.model

import com.google.common.collect.Ordering
import com.squareup.moshi.Moshi
import io.kotlintest.Description
import io.kotlintest.matchers.containAll
import io.kotlintest.matchers.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import net.yupol.transmissionremote.model.json.File

class DirTest : StringSpec() {

    companion object {

        private const val NESTED_FILES = "[" +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-1/f1-1-1\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-1/f1-1-3\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-1/f1-1-2\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-2/f1-2-1\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-2/f1-2-2\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-2/f1-2-3\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-3/f1-3-2\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-3/f1-3-1\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-3/f1-3-3\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-3/f3-3-3\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-1/f3-1-1\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-1/f3-1-2\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-1/f3-1-3\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-2/f3-2-1\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-2/f3-2-2\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-2/f3-2-3\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-3/f3-3-1\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-3/f3-3-2\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-1/f2-1-1\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-1/f2-1-2\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-1/f2-1-3\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-2/f2-2-3\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-2/f2-2-1\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-2/f2-2-2\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-3/f2-3-1\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-3/f2-3-2\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-3/f2-3-3\"}," +
                "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/r1.txt\"}]"

        private const val SINGLE_FILE = "[{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"r1.txt\"}]"
    }

    private lateinit var nestedFiles: Array<File>
    private lateinit var singleFile: Array<File>

    override fun beforeTest(description: Description) {
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(Array<File>::class.java)
        nestedFiles = adapter.fromJson(NESTED_FILES) ?: throw RuntimeException("Can't parse JSON")
        singleFile = adapter.fromJson(SINGLE_FILE) ?: throw RuntimeException("Can't parse JSON")
    }

    init {

        "test nested files tree inflation" {
            val dir = Dir.createFileTree(nestedFiles)

            dir.name shouldBe "/"
            dir.dirs.size shouldBe 1
            dir.fileIndices.size shouldBe 0

            val d0 = dir.dirs[0]
            d0.name shouldBe "Test Tree"
            d0.dirs.size shouldBe 3
            d0.fileIndices.size shouldBe 1
            nestedFiles[d0.fileIndices[0]].name shouldBe "r1.txt"

            val d01 = d0.dirs[0]
            d01.name shouldBe "d1"
            d01.dirs.size shouldBe 3
            d01.fileIndices.size shouldBe 0

            val d011 = d01.dirs[0]
            d011.name shouldBe "d1-1"
            d011.dirs.size shouldBe 0
            d011.fileIndices.size shouldBe 3
            nestedFiles[d011.fileIndices[0]].name shouldBe "f1-1-1"
            nestedFiles[d011.fileIndices[1]].name shouldBe "f1-1-2"
            nestedFiles[d011.fileIndices[2]].name shouldBe "f1-1-3"
        }

        "test single file tree inflation" {
            val dir = Dir.createFileTree(singleFile)

            dir.name shouldBe "/"
            dir.dirs.size shouldBe 0
            dir.fileIndices.size shouldBe 1
            singleFile[dir.fileIndices[0]].name shouldBe "r1.txt"
        }

        "files should be sorted" {
            fun testFilesSortedRecursively(dir: Dir) {
                val fileNames = dir.fileIndices.map { nestedFiles[it].path.split('/').last() }
                Ordering.natural<String>().isOrdered(fileNames) shouldBe true

                dir.dirs.forEach { testFilesSortedRecursively(it) }
            }

            val dir = Dir.createFileTree(nestedFiles)
            testFilesSortedRecursively(dir)
        }

        "dirs should be sorted" {
            fun testDirsSortedRecursively(dir: Dir) {
                val dirNames = dir.dirs.map { it.name }
                Ordering.natural<String>().isOrdered(dirNames) shouldBe true

                dir.dirs.forEach { testDirsSortedRecursively(it) }
            }

            val dir = Dir.createFileTree(nestedFiles)
            testDirsSortedRecursively(dir)
        }

        "test files in dir recursively" {
            val dir = Dir.createFileTree(nestedFiles)

            val fileIndices = Dir.filesInDirRecursively(dir)

            fileIndices.size shouldBe nestedFiles.size
            fileIndices should containAll((0 until nestedFiles.size).toList())
        }
    }
}
