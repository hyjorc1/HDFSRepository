
package Serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node1 implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean isDirectory;

	private String filename;

	// In case the node is a directory, here are its child nodes
	private List<Node1> list;

	// In case the node is a file, this byte array is its content
	private byte[] bytes;

	public Node1() {
	}

	public Node1(String filePath) throws IOException {
		Node1 node = getNode(new File(filePath));
		this.isDirectory = node.isDirectory;
		this.filename = node.filename;
		this.list = node.list;
		this.bytes = node.bytes;
	}

	public void writeContentsToDir(String outputPath) throws IOException {
		if (!outputPath.endsWith(File.separator)) {
			outputPath = outputPath + File.separator;
		}
		writeToFile(outputPath, this);
	}

	private void writeToFile(String parentPath, Node1 node) throws IOException {
		if (node.isDirectory) {
			parentPath = parentPath + File.separator + node.filename;
			for (Node1 df : node.list) {
				writeToFile(parentPath, df);
			}
		} else {
			new File(parentPath).mkdirs();
			File file = new File(parentPath, node.filename);
			file.setReadable(true, false);
			file.setWritable(true, false);

			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				fos.write(node.bytes);
				fos.flush();
			} finally {
				if (fos != null) {
					fos.close();
				}
			}
		}
	}

	private Node1 getNode(File file) throws IOException {

		Node1 node = new Node1();
		node.filename = file.getName();

		if (file.isFile()) {
			node.isDirectory = false;
			node.bytes = getBytes(file);
		} else {
			node.isDirectory = true;
			for (File f : file.listFiles()) {
				node.addToList(getNode(f));
			}
		}
		return node;
	}

	private void addToList(Node1 node) {
		if (list == null) {
			list = new ArrayList<Node1>();
		}
		list.add(node);
	}

	private byte[] getBytes(File file) throws IOException {
		FileInputStream fileInputStream = null;

		byte[] bytes = new byte[(int) file.length()];

		try {
			// convert file into array of bytes
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(bytes);
			return bytes;
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
