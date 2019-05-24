
package Serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class Test {

	public static void main(String[] args) throws Exception {
		// Serializing the directory
		String path = "/Users/hyj/git/BoaData/DataSet/newExample/repos/hyjorc1";
		FileNode node = new FileNode(new File(path));
		
		// Serializable Object
//		Serializable serializable = (Serializable) node;

		
		byte[] data = SerializationUtils.serialize(node);
		
		// Writing the Object/ Directory in a file
		writeToFile(node, "/Users/hyj/Desktop/sample.o");

		// Reading the object again from file
		FileNode node1 = readFromFile("/Users/hyj/Desktop/sample.o");

		// Writing the contents of Object back to file system
		node1.writeContentsToDir("/Users/hyj/Desktop");

//		Repository repo = new FileRepositoryBuilder()
//				.setGitDir(new File("/Users/hyj/git/BoaData/DataSet/newExample/repos/hyjorc1/my-example/.git")).build();

		Repository repo1 = new FileRepositoryBuilder()
				.setGitDir(new File("/Users/hyj/Desktop/hyjorc1/my-example/.git"))
				.build();
		
		FileNode node2 = SerializationUtils.deserialize(data);
		
		System.out.println("sample1");
		node2.writeContentsToDir("/Users/hyj/Desktop/sample1");
		
		Repository repo2 = new FileRepositoryBuilder()
				.setGitDir(new File("/Users/hyj/Desktop/sample1/hyjorc1/my-example/.git"))
				.build();

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
