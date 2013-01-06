package org.jenkinsci.remoting.overthere.delegate;

import com.xebialabs.overthere.CmdLine;
import com.xebialabs.overthere.OperatingSystemFamily;
import com.xebialabs.overthere.OverthereConnection;
import com.xebialabs.overthere.OverthereFile;
import com.xebialabs.overthere.OverthereProcess;
import com.xebialabs.overthere.OverthereProcessOutputHandler;
import com.xebialabs.overthere.RuntimeIOException;

/**
 * @author Kohsuke Kawaguchi
 */
public class DelegatingOverthereConnection implements OverthereConnection {
    protected final OverthereConnection base;

    public DelegatingOverthereConnection(OverthereConnection base) {
        this.base = base;
    }

    public OperatingSystemFamily getHostOperatingSystem() {
        return base.getHostOperatingSystem();
    }

    public OverthereFile getFile(String hostPath) {
        return base.getFile(hostPath);
    }

    public OverthereFile getFile(OverthereFile parent, String child) {
        return base.getFile(parent, child);
    }

    public OverthereFile getTempFile(String nameTemplate) {
        return base.getTempFile(nameTemplate);
    }

    public OverthereFile getTempFile(String prefix, String suffix) throws RuntimeIOException {
        return base.getTempFile(prefix, suffix);
    }

    public OverthereFile getWorkingDirectory() {
        return base.getWorkingDirectory();
    }

    public void setWorkingDirectory(OverthereFile workingDirectory) {
        base.setWorkingDirectory(workingDirectory);
    }

    public int execute(OverthereProcessOutputHandler handler, CmdLine commandLine) {
        return base.execute(handler, commandLine);
    }

    public OverthereProcess startProcess(CmdLine commandLine) {
        return base.startProcess(commandLine);
    }

    public boolean canStartProcess() {
        return base.canStartProcess();
    }

    public void close() {
        base.close();
    }

    @Override
    public String toString() {
        return base.toString();
    }
}
