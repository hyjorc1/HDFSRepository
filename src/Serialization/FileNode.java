
package Serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FileNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private boolean isDirectory;
	private String name;
	private FileNode parent;
	private List<FileNode> list;
	private byte[] bytes;

	public FileNode(File file) {
		build(file);
	}
	
	public FileNode(String name) {
		this.name = name;
		this.parent = null;
		this.list = null;
		this.bytes = null;
		this.isDirectory = false;
	}
	
	public FileNode(String name, FileNode parent) {
		this(name);
		this.parent = parent;
	}

	public void writeContentsToDir(String outputPath) throws IOException {
		if (!outputPath.endsWith(File.separator))
			outputPath += File.separator;
		writeToFile(outputPath, this);
	}

	private void writeToFile(String parentPath, FileNode node) {
		Stack<String> paths = new Stack<String>();
		Stack<FileNode> nodes = new Stack<FileNode>();
		nodes.push(node);
		paths.push(parentPath);
		while (!nodes.isEmpty()) {
			FileNode curNode = nodes.pop();
			String curParentPath = paths.pop();
			if (curNode.isDirectory) {
				curParentPath += File.separator + curNode.name;
				new File(curParentPath).mkdirs();
				for (FileNode childNode : curNode.list) {
					nodes.push(childNode);
					paths.push(curParentPath);
				}
			} else if (curNode.bytes != null) {
				new File(curParentPath).mkdirs();
				File file = new File(curParentPath, curNode.name);
				file.setReadable(true, false);
				file.setWritable(true, false);			
				try (FileOutputStream fos = new FileOutputStream(file)) {
					fos.write(curNode.bytes);
					fos.flush();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		}
	}

	public void build(File file) {
		this.name = file.getName();
		Stack<File> files = new Stack<File>();
		Stack<FileNode> nodes = new Stack<FileNode>();
		nodes.push(this);
		files.push(file);
		while (!files.isEmpty()) {
			FileNode curNode = nodes.pop();
			File curFile = files.pop();
			if (curFile.isFile()) {
				curNode.isDirectory = false;
				curNode.bytes = getBytes(curFile);
			} else {
				curNode.isDirectory = true;
				curNode.list = new ArrayList<FileNode>();
				for (File f : curFile.listFiles()) {
					FileNode childNode = new FileNode(f.getName(), curNode);
					curNode.list.add(childNode);
					nodes.push(childNode);
					files.push(f);
				}
			}
		}
	}

	private byte[] getBytes(File file) {
		byte[] bytes = new byte[(int) file.length()];
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			fileInputStream.read(bytes);
			fileInputStream.close();
			return bytes;
		} catch (IOException e ) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean isDirectory() {
		return this.isDirectory;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<FileNode> getList() {
		return this.list;
	}
	
	public byte[] getBytes() {
		return this.bytes;
	}
	
	public FileNode getParent() {
		return this.parent;
	}
	
	public void settParent(FileNode parent) {
		this.parent = parent;
	}

}
