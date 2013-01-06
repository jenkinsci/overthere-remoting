package org.jenkinsci.remoting.overthere.delegate;

import com.xebialabs.overthere.OverthereProcess;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Kohsuke Kawaguchi
 */
public class DelegatingOverthereProcess implements OverthereProcess {
    protected final OverthereProcess base;

    public DelegatingOverthereProcess(OverthereProcess base) {
        this.base = base;
    }

    public OutputStream getStdin() {
        return base.getStdin();
    }

    public InputStream getStdout() {
        return base.getStdout();
    }

    public InputStream getStderr() {
        return base.getStderr();
    }

    public int waitFor() throws InterruptedException {
        return base.waitFor();
    }

    public void destroy() {
        base.destroy();
    }

    public int exitValue() throws IllegalThreadStateException {
        return base.exitValue();
    }
}
