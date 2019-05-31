package Serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.lang.SerializationUtils;
import org.eclipse.jgit.internal.storage.file.HDFSFile;
import org.eclipse.jgit.internal.storage.file.HDFSRepositoryBuilder;
import org.eclipse.jgit.lib.Repository;

public class CurTest {

	public static void main(String[] args) throws IOException {
		String path = "/Users/hyj/Desktop/hyjorc1";
		File f = new File(path);
		System.out.println(f.getName());
		System.out.println(f.getPath());
		System.out.println(f.getParent());
		
		HDFSFile node = new HDFSFile(path).build();
		
//		String output = "/Users/hyj/Desktop/sample.o";
//		writeToFile(node, output);
//		NodeFile node1 = readFromFile(output);
//		node1.writeContentsToDir("/Users/hyj/Desktop/repos");
//		Repository repo2 = new FileRepositoryBuilder()
//				.setGitDir(new File("/Users/hyj/Desktop/repos/hyjorc1/my-example/.git")).build();
//		System.out.println(Test.getFileContent2(repo2));
		
		byte[] data = SerializationUtils.serialize(node);
//		node = SerializationUtils.deserialize(data);
//		node.writeContentsToDir("/Users/hyj/Desktop/repos");
//		Repository repo2 = new FileRepositoryBuilder()
//				.setGitDir(new File("/Users/hyj/Desktop/repos/hyjorc1/my-example/.git")).build();
//		System.out.println(Test.getFileContent2(repo2));
		
		Repository repo3 = new HDFSRepositoryBuilder()
				.setGitDir(new File("/Users/hyj/Desktop/hyjorc1/my-example/.git"))
				.build();
		System.out.println(Test.getFileContent2(repo3));
	}
	
	private static void writeToFile(HDFSFile node, String filepath) {
		try (FileOutputStream fout = new FileOutputStream(filepath, true);
				ObjectOutputStream oos = new ObjectOutputStream(fout)) {
			oos.writeObject(node);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static HDFSFile readFromFile(String filepath) {
		try (FileInputStream streamIn = new FileInputStream(filepath);
				ObjectInputStream objectinputstream = new ObjectInputStream(streamIn)) {
			HDFSFile node = (HDFSFile) objectinputstream.readObject();
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
