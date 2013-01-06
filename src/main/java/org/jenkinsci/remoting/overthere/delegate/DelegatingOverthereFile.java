package org.jenkinsci.remoting.overthere.delegate;

import com.xebialabs.overthere.OverthereConnection;
import com.xebialabs.overthere.OverthereFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * {@link OverthereFile} that delegates to another {@link OverthereFile}.
 *
 * @author Kohsuke Kawaguchi
 */
public class DelegatingOverthereFile implements OverthereFile {
    protected final OverthereFile base;

    public DelegatingOverthereFile(OverthereFile base) {
        this.base = base;
    }

    public OverthereConnection getConnection() {
        return base.getConnection();
    }

    public String getPath() {
        return base.getPath();
    }

    public String getName() {
        return base.getName();
    }

    public OverthereFile getParentFile() {
        return base.getParentFile();
    }

    public OverthereFile getFile(String child) {
        return base.getFile(child);
    }

    public boolean exists() {
        return base.exists();
    }

    public boolean canRead() {
        return base.canRead();
    }

    public boolean canWrite() {
        return base.canWrite();
    }

    public boolean canExecute() {
        return base.canExecute();
    }

    public boolean isFile() {
        return base.isFile();
    }

    public boolean isDirectory() {
        return base.isDirectory();
    }

    public boolean isHidden() {
        return base.isHidden();
    }

    public long lastModified() {
        return base.lastModified();
    }

    public long length() {
        return base.length();
    }

    public InputStream getInputStream() {
        return base.getInputStream();
    }

    public OutputStream getOutputStream() {
        return base.getOutputStream();
    }

    public void setExecutable(boolean executable) {
        base.setExecutable(executable);
    }

    public void delete() {
        base.delete();
    }

    public void deleteRecursively() {
        base.deleteRecursively();
    }

    public List<OverthereFile> listFiles() {
        return base.listFiles();
    }

    public void mkdir() {
        base.mkdir();
    }

    public void mkdirs() {
        base.mkdirs();
    }

    public void renameTo(OverthereFile dest) {
        base.renameTo(dest);
    }

    public void copyTo(OverthereFile dest) {
        base.copyTo(dest);
    }
}
