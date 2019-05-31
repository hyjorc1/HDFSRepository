
package Serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.jgit.util.FS;

public class FileNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private boolean isDirectory;
	private String name;
	private FileNode parent;
	private int idx;
	private List<FileNode> list;
	private byte[] bytes;
	
	// for HDFSFileSnapshot
	private long modified;
	private long size;

	public FileNode(File file) {
		build(file);
	}

	public FileNode(String name) {
		this.name = name;
		this.parent = null;
		this.setIdx(-1);
		this.list = null;
		this.bytes = null;
		this.isDirectory = false;
		this.setModified(-1);
		this.setSize(-1);
	}

	public FileNode(String name, FileNode parent, int idx) {
		this(name);
		this.parent = parent;
		this.setIdx(idx);
	}

	public void writeContentsToDir(String outputPath) throws IOException {
		if (!outputPath.endsWith(File.separator))
			outputPath += File.separator;
		writeToFile(outputPath, this);
	}

	private void writeToFile(String parentPath, FileNode node) throws IOException {
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
			} else {
				File file = new File(curParentPath, curNode.name);
				file.setReadable(true, false);
				file.setWritable(true, false);

				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(file);
					fos.write(curNode.bytes);
					fos.flush();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (fos != null)
						fos.close();
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
				curNode.updateModifiedAndSize(curFile);
				curNode.isDirectory = false;
				curNode.bytes = getBytes(curFile);
			} else {
				curNode.updateModifiedAndSize(curFile);
				curNode.isDirectory = true;
				curNode.list = new ArrayList<FileNode>();
				for (File f : curFile.listFiles()) {
					FileNode childNode = new FileNode(f.getName(), curNode, list.size());
					curNode.list.add(childNode);
					nodes.push(childNode);
					files.push(f);
				}
			}
		}
	}

	private void updateModifiedAndSize(File curFile) {
		BasicFileAttributes fileAttributes;
		try {
			fileAttributes = FS.DETECTED.fileAttributes(curFile);
			this.setModified(fileAttributes.lastModifiedTime().toMillis());
			this.setSize(fileAttributes.size());
		} catch (IOException e) {
			this.setModified(curFile.lastModified());
			this.setSize(curFile.length());
		}
	}

	private byte[] getBytes(File file) {
		byte[] bytes = new byte[(int) file.length()];

		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(bytes);
			fileInputStream.close();
			return bytes;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public long getModified() {
		return modified;
	}

	public void setModified(long modified) {
		this.modified = modified;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

}
