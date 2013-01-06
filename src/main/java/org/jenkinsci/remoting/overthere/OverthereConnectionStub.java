package org.jenkinsci.remoting.overthere;

import com.xebialabs.overthere.CmdLine;
import com.xebialabs.overthere.OverthereConnection;
import com.xebialabs.overthere.OverthereFile;
import com.xebialabs.overthere.OverthereProcess;
import com.xebialabs.overthere.RuntimeIOException;
import hudson.remoting.Channel;
import org.jenkinsci.remoting.overthere.delegate.DelegatingOverthereConnection;

import java.io.Serializable;

/**
 * @author Kohsuke Kawaguchi
 */
class OverthereConnectionStub extends DelegatingOverthereConnection implements Serializable {
    public OverthereConnectionStub(OverthereConnection base) {
        super(base);
    }

    private Object writeReplace() {
        Channel ch = Channel.current();
        return ch.export(OverthereConnection.class,this);
    }

    /**
     * {@link OverthereFile} needs to be exported to be transported to the other side of the channel.
     */
    OverthereFile export(OverthereFile f) {
        if (f==null)    return null;
        return new OverthereFileStub(this,f);
    }

    /**
     * {@link OverthereFile}s we receive in parameters are {@link OverthereFileStub}s (because
     * every {@link OverthereFile} that we return should be a stub.
     */
    OverthereFile unstub(OverthereFile f) {
        if (f==null)    return null;
        assert f.getConnection()==this;
        return ((OverthereFileStub)f).getBase();
    }

//
//
// The rest is the interception of OverthereConnection
//
//

    @Override
    public OverthereFile getFile(String hostPath) {
        return export(super.getFile(hostPath));
    }

    @Override
    public OverthereFile getFile(OverthereFile parent, String child) {
        return export(super.getFile(unstub(parent), child));
    }

    @Override
    public OverthereFile getTempFile(String nameTemplate) {
        return export(super.getTempFile(nameTemplate));
    }

    @Override
    public OverthereFile getTempFile(String prefix, String suffix) throws RuntimeIOException {
        return export(super.getTempFile(prefix, suffix));
    }

    @Override
    public OverthereFile getWorkingDirectory() {
        return export(super.getWorkingDirectory());
    }

    @Override
    public void setWorkingDirectory(OverthereFile workingDirectory) {
        super.setWorkingDirectory(unstub(workingDirectory));
    }

    @Override
    public OverthereProcess startProcess(CmdLine commandLine) {
        return new OverthereProcessStub(super.startProcess(commandLine));
    }

    private static final long serialVersionUID = 1L;
}
