package org.jenkinsci.remoting.overthere;

import com.xebialabs.overthere.OverthereFile;
import com.xebialabs.overthere.OverthereProcess;
import hudson.remoting.Channel;
import hudson.remoting.RemoteInputStream;
import hudson.remoting.RemoteOutputStream;
import org.jenkinsci.remoting.overthere.delegate.DelegatingOverthereProcess;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * @author Kohsuke Kawaguchi
 */
class OverthereProcessStub extends DelegatingOverthereProcess implements Serializable {
    OverthereProcessStub(OverthereProcess base) {
        super(base);
    }

    private Object writeReplace() {
        Channel ch = Channel.current();
        return ch.export(OverthereProcess.class,this);
    }


//
//
// The rest is the interception of OverthereProcess
//
//

    @Override
    public OutputStream getStdin() {
        return new RemoteOutputStream(super.getStdin());
    }

    @Override
    public InputStream getStdout() {
        return new RemoteInputStream(super.getStdout());
    }

    @Override
    public InputStream getStderr() {
        return new RemoteInputStream(super.getStderr());
    }

    private static final long serialVersionUID = 1L;
}
