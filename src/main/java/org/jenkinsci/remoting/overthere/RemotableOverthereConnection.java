package org.jenkinsci.remoting.overthere;

import com.xebialabs.overthere.OverthereConnection;
import hudson.remoting.Channel;
import org.jenkinsci.remoting.overthere.delegate.DelegatingOverthereConnection;

import java.io.Serializable;

/**
 * Wraps {@link OverthereConnection} such that it can be sent to the other side of the {@link Channel}.
 *
 * @author Kohsuke Kawaguchi
 */
public class RemotableOverthereConnection extends DelegatingOverthereConnection implements Serializable {
    public RemotableOverthereConnection(OverthereConnection base) {
        super(base);
    }

    private Object writeReplace() {
        return new OverthereConnectionStub(base);
    }

    private static final long serialVersionUID = 1L;
}
