package org.jenkinsci.remoting.overthere;

import com.xebialabs.overthere.OverthereConnection;
import com.xebialabs.overthere.OverthereFile;
import hudson.remoting.Channel;
import hudson.remoting.RemoteInputStream;
import hudson.remoting.RemoteOutputStream;
import org.jenkinsci.remoting.overthere.delegate.DelegatingOverthereFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 */
class OverthereFileStub extends DelegatingOverthereFile implements Serializable {
    private final OverthereConnectionStub connection;

    OverthereFileStub(OverthereConnectionStub connection, OverthereFile base) {
        super(base);
        this.connection = connection;
    }

    OverthereFile getBase() {
        return base;
    }

    private Object writeReplace() {
        Channel ch = Channel.current();
        return ch.export(OverthereFile.class,this);
    }


//
//
// The rest is the interception of OverthereFile
//
//
    @Override
    public OverthereConnection getConnection() {
        return connection;
    }

    @Override
    public OverthereFile getParentFile() {
        return connection.export(super.getParentFile());
    }

    @Override
    public OverthereFile getFile(String child) {
        return connection.export(super.getFile(child));
    }

    @Override
    public InputStream getInputStream() {
        return new RemoteInputStream(super.getInputStream());
    }

    @Override
    public OutputStream getOutputStream() {
        return new RemoteOutputStream(super.getOutputStream());
    }

    @Override
    public List<OverthereFile> listFiles() {
        List<OverthereFile> r = super.listFiles();
        if (r==null)    return null;
        for (int i=0; i<r.size(); i++)
            r.set(i, connection.export(r.get(i)));
        return r;
    }

    @Override
    public void renameTo(OverthereFile dest) {
        super.renameTo(connection.unstub(dest));
    }

    @Override
    public void copyTo(OverthereFile dest) {
        super.copyTo(connection.unstub(dest));
    }

    private static final long serialVersionUID = 1L;
}
