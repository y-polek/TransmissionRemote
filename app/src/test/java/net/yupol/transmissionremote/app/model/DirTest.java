package net.yupol.transmissionremote.app.model;

import com.google.api.client.json.jackson2.JacksonFactory;

import junit.framework.TestCase;

import net.yupol.transmissionremote.model.json.File;

public class DirTest extends TestCase {

    private static final String NESTED_FILES = "[" +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-1/f1-1-1\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-1/f1-1-2\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-1/f1-1-3\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-2/f1-2-1\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-2/f1-2-2\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-2/f1-2-3\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-3/f1-3-1\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-3/f1-3-2\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d1/d1-3/f1-3-3\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-1/f2-1-1\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-1/f2-1-2\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-1/f2-1-3\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-2/f2-2-1\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-2/f2-2-2\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-2/f2-2-3\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-3/f2-3-1\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-3/f2-3-2\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d2/d2-3/f2-3-3\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-1/f3-1-1\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-1/f3-1-2\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-1/f3-1-3\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-2/f3-2-1\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-2/f3-2-2\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-2/f3-2-3\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-3/f3-3-1\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-3/f3-3-2\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/d3/d3-3/f3-3-3\"}," +
            "{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"Test Tree/r1.txt\"}]";

    private static final String SINGLE_FILE = "[{\"bytesCompleted\":1937408,\"length\":1937408,\"name\":\"r1.txt\"}]";

    private File[] nestedFiles;
    private File[] singleFile;

    @Override
    protected void setUp() throws Exception {
        JacksonFactory jsonFactory = new JacksonFactory();
        nestedFiles = jsonFactory.fromString(NESTED_FILES, File[].class);
        singleFile = jsonFactory.fromString(SINGLE_FILE, File[].class);
    }

    public void testCreateFileTreeNestedFiles() {
        Dir dir = Dir.createFileTree(nestedFiles);

        assertEquals("/", dir.getName());
        assertEquals(1, dir.getDirs().size());
        assertEquals(0, dir.getFileIndices().size());

        Dir d0 = dir.getDirs().get(0);
        assertEquals("Test Tree", d0.getName());
        assertEquals(3, d0.getDirs().size());
        assertEquals(1, d0.getFileIndices().size());
        assertEquals("r1.txt", nestedFiles[d0.getFileIndices().get(0)].getName());

        Dir d01 = d0.getDirs().get(0);
        assertEquals("d1", d01.getName());
        assertEquals(3, d01.getDirs().size());
        assertEquals(0, d01.getFileIndices().size());

        Dir d011 = d01.getDirs().get(0);
        assertEquals("d1-1", d011.getName());
        assertEquals(0, d011.getDirs().size());
        assertEquals(3, d011.getFileIndices().size());
        assertEquals("f1-1-1", nestedFiles[d011.getFileIndices().get(0)].getName());
        assertEquals("f1-1-2", nestedFiles[d011.getFileIndices().get(1)].getName());
        assertEquals("f1-1-3", nestedFiles[d011.getFileIndices().get(2)].getName());
    }

    public void testCreateFileTreeSingleFile() {
        Dir dir = Dir.createFileTree(singleFile);

        assertEquals("/", dir.getName());
        assertEquals(0, dir.getDirs().size());
        assertEquals(1, dir.getFileIndices().size());
        assertEquals("r1.txt", singleFile[dir.getFileIndices().get(0)].getName());
    }

    public void testFileIndices() {
        Dir dir = Dir.createFileTree(nestedFiles);

        Dir d11 = dir.getDirs().get(0).getDirs().get(0).getDirs().get(0);
        assertEquals(Integer.valueOf(0), d11.getFileIndices().get(0));
        assertEquals(Integer.valueOf(1), d11.getFileIndices().get(1));
        assertEquals(Integer.valueOf(2), d11.getFileIndices().get(2));

        assertEquals(Integer.valueOf(27), dir.getDirs().get(0).getFileIndices().get(0));
    }
}
