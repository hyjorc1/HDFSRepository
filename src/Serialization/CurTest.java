package Serialization;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.eclipse.jgit.internal.storage.file.ByteArrayFile;
import org.eclipse.jgit.internal.storage.file.ByteArrayRepositoryBuilder;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

public class CurTest {

	private static final ByteArrayOutputStream buffer = new ByteArrayOutputStream(4096);

	public static void main(String[] args) throws IOException {

//		testHDFSFile();

		testHDFSRepositoryBuilder();
	}

	private static void testHDFSFile() throws IOException {
		// CHECKME test HDFSFile
		String path = "/Users/hyj/Desktop/hyjorc1";
//				File f = new File(path);
//				System.out.println(f.getName());
//				System.out.println(f.getPath());
//				System.out.println(f.getParent());
//				
		ByteArrayFile node = new ByteArrayFile(path);

		String output = "/Users/hyj/Desktop/sample.o";
		writeToFile(node, output);
		ByteArrayFile node1 = readFromFile(output);
		node1.writeContentsToDir("/Users/hyj/Desktop/repos");
		Repository repo2 = new FileRepositoryBuilder()
				.setGitDir(new File("/Users/hyj/Desktop/repos/hyjorc1/my-example/.git")).build();
		System.out.println(Test.getFileContent2(repo2, "0aae0f6ea47d4181e10dc57ecf7a822df8c1373e"));

//		byte[] data = SerializationUtils.serialize(node);
//		node = SerializationUtils.deserialize(data);
//		node.writeContentsToDir("/Users/hyj/Desktop/repos");
//		Repository repo2 = new FileRepositoryBuilder()
//				.setGitDir(new File("/Users/hyj/Desktop/repos/hyjorc1/my-example/.git")).build();
//		System.out.println(Test.getFileContent2(repo2));
	}

	private static void testHDFSRepositoryBuilder() throws IOException {
		// CHECKME test HDFSRepositoryBuilder
		String path = "/Users/hyj/Desktop/hyjorc1/my-example/.git";
//		String path = "/Users/hyj/Desktop/Activiti/Activiti/.git";
//		String path = "/Users/hyj/git/BoaData/DataSet/new_activiti/repos/Activiti/Activiti/.git";
		ByteArrayFile node = new ByteArrayFile(path);
		
//		deleteDirectory(new File(path));
		
		String oid1 = "0aae0f6ea47d4181e10dc57ecf7a822df8c1373e"; // newExample
		String oid2 = "3a1fb12eb0a5e087698c54f794b46571e9c309d7"; // Activiti
		Repository fRepo = new FileRepositoryBuilder().setGitDir(new File(path)).build();
		Repository oRepo = new ByteArrayRepositoryBuilder().setGitDir(node).build();
		System.out.println(getFileContent2(oRepo, oid1));
		System.out.println(getFileContent2(oRepo, oid2));

//		deleteDirectory(new File(path));
//		
//		List<String> lines = readAsList("/Users/hyj/Desktop/oids.text");
//		System.out.println(lines.size());
//		for (String line : lines) {
//			String oid = line;
//			if (oid.length() != 40) {
//				System.out.println("invalid: " + oid);
//			} else {
//				if (!getFileContent2(fRepo, oid).equals(getFileContent2(oRepo, oid)))
//					System.out.println("unmatched: " + oid);
//			}
//		}
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

	private static void writeToFile(ByteArrayFile node, String filepath) {
		try (FileOutputStream fout = new FileOutputStream(filepath, true);
				ObjectOutputStream oos = new ObjectOutputStream(fout)) {
			oos.writeObject(node);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static ByteArrayFile readFromFile(String filepath) {
		try (FileInputStream streamIn = new FileInputStream(filepath);
				ObjectInputStream objectinputstream = new ObjectInputStream(streamIn)) {
			ByteArrayFile node = (ByteArrayFile) objectinputstream.readObject();
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

	public static List<String> readAsList(String path) throws IOException {
		String line;
		File file = new File(path);
		List<String> al = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		while ((line = br.readLine()) != null)
			al.add(line);
		br.close();
		return al;
	}

	public static boolean deleteDirectory(File dir) {
		if (dir.isDirectory()) {
			File[] children = dir.listFiles();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDirectory(children[i]);
				if (!success) {
					return false;
				}
			}
		} // either file or an empty directory 
		System.out.println("removing file or directory : " + dir.getName()); 
		return dir.delete();
	}

}
