package net.yupol.transmissionremote.app.model;

import static com.google.common.truth.Truth.assertThat;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.collect.Ordering;

import net.yupol.transmissionremote.app.model.json.File;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class DirTest {

    private static final String NESTED_FILES = "[" +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-1/f1-1-1\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-1/f1-1-3\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-1/f1-1-2\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-2/f1-2-1\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-2/f1-2-2\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-3/f1-3-1\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-2/f1-2-3\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-3/f1-3-2\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-1/f2-1-2\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-3/f1-3-3\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-1/f2-1-1\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-2/f2-2-1\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-2/f2-2-3\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-3/f2-3-1\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-3/f3-3-3\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-3/f2-3-3\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-1/f2-1-3\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-2/f3-2-2\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-2/f2-2-2\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-1/f3-1-1\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-3/f2-3-2\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-1/f3-1-2\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-3/f3-3-2\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-1/f3-1-3\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-3/f3-3-1\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-2/f3-2-1\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-2/f3-2-3\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/r1.txt\"}]";

    private static final String SINGLE_FILE = "[{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"r1.txt\"}]";

    private File[] nestedFiles;
    private File[] singleFile;

    @Before
    public void setUp() throws Exception {
        JacksonFactory jsonFactory = new JacksonFactory();
        nestedFiles = jsonFactory.fromString(NESTED_FILES, File[].class);
        singleFile = jsonFactory.fromString(SINGLE_FILE, File[].class);
    }

    @Test
    public void testCreateFileTreeNestedFiles() {
        Dir dir = Dir.createFileTree(nestedFiles);

        assertThat(dir.getName()).isEqualTo("/");
        assertThat(dir.getDirs().size()).isEqualTo(1);
        assertThat(dir.getFileIndices().size()).isEqualTo(0);

        Dir d0 = dir.getDirs().get(0);
        assertThat(d0.getName()).isEqualTo("Test Tree");
        assertThat(d0.getDirs().size()).isEqualTo(3);
        assertThat(d0.getFileIndices().size()).isEqualTo(1);
        assertThat(nestedFiles[d0.getFileIndices().get(0)].getName()).isEqualTo("r1.txt");

        Dir d01 = d0.getDirs().get(0);
        assertThat(d01.getName()).isEqualTo("d1");
        assertThat(d01.getDirs().size()).isEqualTo(3);
        assertThat(d01.getFileIndices().size()).isEqualTo(0);

        Dir d011 = d01.getDirs().get(0);
        assertThat(d011.getName()).isEqualTo("d1-1");
        assertThat(d011.getDirs().size()).isEqualTo(0);
        assertThat(d011.getFileIndices().size()).isEqualTo(3);
        assertThat(nestedFiles[d011.getFileIndices().get(0)].getName()).isEqualTo("f1-1-1");
        assertThat(nestedFiles[d011.getFileIndices().get(1)].getName()).isEqualTo("f1-1-2");
        assertThat(nestedFiles[d011.getFileIndices().get(2)].getName()).isEqualTo("f1-1-3");
    }

    @Test
    public void testCreateFileTreeSingleFile() {
        Dir dir = Dir.createFileTree(singleFile);

        assertThat(dir.getName()).isEqualTo("/");
        assertThat(dir.getDirs().size()).isEqualTo(0);
        assertThat(dir.getFileIndices().size()).isEqualTo(1);
        assertThat(singleFile[dir.getFileIndices().get(0)].getName()).isEqualTo("r1.txt");
    }

    @Test
    public void testFilesSorted() {
        Dir dir = Dir.createFileTree(nestedFiles);

        testFilesSortedRecursively(dir);
    }

    @Test
    public void testDirsSorted() {
        Dir dir = Dir.createFileTree(nestedFiles);

        testDirsSortedRecursively(dir);
    }

    private void testFilesSortedRecursively(Dir dir) {
        List<String> fileNames = dir.getFileIndices().stream().map(index -> {
            String[] segments = nestedFiles[index].getPath().split("/");
            return segments[segments.length - 1];
        }).collect(Collectors.toList());

        assertThat(Ordering.natural().isOrdered(fileNames)).isTrue();

        for (Dir subDir : dir.getDirs()) {
            testFilesSortedRecursively(subDir);
        }
    }

    private void testDirsSortedRecursively(Dir dir) {
        List<String> dirNames = dir.getDirs().stream().map(Dir::getName).collect(Collectors.toList());

        assertThat(Ordering.natural().isOrdered(dirNames)).isTrue();

        for (Dir subDir : dir.getDirs()) {
            testDirsSortedRecursively(subDir);
        }
    }
}
