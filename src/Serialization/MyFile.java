package Serialization;

import java.io.File;
import java.io.IOException;

public class MyFile extends java.io.File {

	private static final long serialVersionUID = 1L;

	private FileNode node;
	
	public MyFile(FileNode node) {
		super(node.getName());
		this.node = node;
	}
	
	@Override
	public String getName() {
		String name = node.getName();
		int index = name.lastIndexOf(separatorChar);
        return name.substring(index + 1);
    }
	
	@Override
	public String getParent() {
		String name = node.getName();
		int index = name.lastIndexOf(separatorChar);
        return name.substring(0, index);
    }
	
	@Override
	public MyFile getParentFile() {
        return new MyFile(this.node.getParent());
    }
	
	@Override
	public String getPath() {
        return this.node.getName();
    }
	
	@Override
	public boolean isAbsolute() {
		return true;
	}
	
	@Override
	public String getAbsolutePath() {
        return getPath();
    }
	
	@Override
	public MyFile getAbsoluteFile() {
        return this;
    }
	
	@Override
	public String getCanonicalPath() {
        return getPath();
    }
	
	@Override
	public MyFile getCanonicalFile() {
        return this;
    }
	
	@Override
	public boolean canRead() {
		return true;
	}
	
	@Override
	public boolean canWrite() {
		return false;
	}
	
	@Override
	public boolean exists() {
		return true;
	}
	
	@Override
	public boolean isDirectory() {
		return this.node.isDirectory();
	}
	
	@Override
	public boolean isFile() {
		return !this.node.isDirectory();
	}
	
	@Override
	public boolean isHidden() {
		return false;
	}
	
	@Override
	public long lastModified() {
		return 0L;
	}
	
	@Override
	public long length() {
		return 0L;
	}
}
