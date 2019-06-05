
package Serialization;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.jgit.internal.storage.file.ByteArrayRepositoryBuilder;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

public class Test  {

	private static final ByteArrayOutputStream buffer = new ByteArrayOutputStream(4096);
	
	public static void main(String[] args) throws Exception {
		// Serializing the directory
//		String path = "/Users/hyj/git/BoaData/DataSet/a8";
		String path = "/Users/hyj/Desktop/hyjorc1";
		long before = usedMem();
		FileNode node = new FileNode(new File(path));
		long after = usedMem();
		System.out.println(after - before);
		
		// Serializable Object
//		Serializable serializable = (Serializable) node;

		
		byte[] data = SerializationUtils.serialize(node);
		
//		// Writing the Object/ Directory in a file
//		writeToFile(node, "/Users/hyj/Desktop/sample.o");
//
//		// Reading the object again from file
//		FileNode node1 = readFromFile("/Users/hyj/Desktop/sample.o");
//
//		// Writing the contents of Object back to file system
//		node1.writeContentsToDir("/Users/hyj/Desktop");

//		Repository repo = new FileRepositoryBuilder()
//				.setGitDir(new File("/Users/hyj/git/BoaData/DataSet/newExample/repos/hyjorc1/my-example/.git")).build();

//		Repository repo1 = new FileRepositoryBuilder()
//				.setGitDir(new File("/Users/hyj/Desktop/hyjorc1/my-example/.git"))
//				.build();
		
		FileNode node2 = SerializationUtils.deserialize(data);
		
		
		
		System.out.println("sample1");
		node2.writeContentsToDir("/Users/hyj/Desktop/sample1");
		
		
		
//		Repository repo2 = new FileRepositoryBuilder()
//				.setGitDir(new File("/Users/hyj/Desktop/sample1/hyjorc1/my-example/.git"))
//				.build();

		
		
//		Repository repo3 = new FileRepositoryBuilder()
//				.setGitDir(new MyFile(node2))
//				.build();
		
//		System.out.println(getFileContent(repo2, "9e4029c363aca2648151ccfceb17ab999237430c", "src/org/birds/Bird.java"));
		
		// target method
//		System.out.println(getFileContent2(repo2));
//		System.out.println(getFileContent2(repo3));
		
		
//		new Thread(new FileIO.DirectoryRemover("/Users/hyj/Desktop/sample1/")).start();
//		new Thread(new FileIO.DirectoryRemover("/Users/hyj/Desktop/sample.o")).start();
		

//		System.out.println(getFileContent(repo2, "9e4029c363aca2648151ccfceb17ab999237430c", "src/org/birds/Bird.java"));
		
		
	}
	
	public static long usedMem() {
		return Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
	}
	
	public static final String getFileContent2(Repository repo, String oid) throws IOException {
		ObjectId fileid = ObjectId.fromString(oid);
		try {
			buffer.reset();
			buffer.write(repo.open(fileid, Constants.OBJ_BLOB).getCachedBytes());
			return buffer.toString();
		} catch (final Throwable e) {
			return null;
		}
	}
	
	public static final String getFileContent(Repository repo, String id, String fileName) throws IOException {
		ObjectId oid = repo.resolve(id);
		String content = null;
    	RevWalk revWalk = new RevWalk(repo);
        RevCommit commit = revWalk.parseCommit(oid);
        RevTree tree = commit.getTree();
        try {
        	TreeWalk tw = new TreeWalk(repo);
            tw.addTree(tree);
            tw.setRecursive(true);
            tw.setFilter(PathFilter.create(fileName));
            if (tw.next()) {
            	ObjectLoader loader = repo.open(tw.getObjectId(0));
	            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            loader.copyTo(baos);
	            content = baos.toString();
            }
        } catch (Exception e) {
        	System.err.println(e);
		} finally {
			revWalk.dispose();
            revWalk.close();
            repo.close();
		}
		return content;
	}

	private static void writeToFile(FileNode node, String filepath) {
		try (FileOutputStream fout = new FileOutputStream(filepath, true);
				ObjectOutputStream oos = new ObjectOutputStream(fout)) {
			oos.writeObject(node);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static FileNode readFromFile(String filepath) {
		try (FileInputStream streamIn = new FileInputStream(filepath);
				ObjectInputStream objectinputstream = new ObjectInputStream(streamIn)) {
			FileNode node = (FileNode) objectinputstream.readObject();
			objectinputstream.close();
			return node;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
