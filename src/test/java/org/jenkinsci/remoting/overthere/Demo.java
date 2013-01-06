package org.jenkinsci.remoting.overthere;

import com.xebialabs.overthere.CmdLine;
import com.xebialabs.overthere.ConnectionOptions;
import com.xebialabs.overthere.OverthereConnection;
import com.xebialabs.overthere.OverthereFile;
import com.xebialabs.overthere.OverthereProcess;
import com.xebialabs.overthere.local.LocalConnection;
import com.xebialabs.overthere.ssh.SshConnectionBuilder;
import com.xebialabs.overthere.util.DefaultAddressPortMapper;
import hudson.cli.CLI;
import hudson.remoting.Callable;
import hudson.remoting.Channel;
import hudson.remoting.RemoteOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.URL;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Demo.
 *
 */
public class Demo implements Serializable
{
    @Option(name="-i",usage="SSH private key if your Jenkins requires authentication")
    public File keyFile;

    @Argument(index=0,metaVar="URL",required=true,usage="URL of Jenkins")
    public URL url;

    @Argument(index=1,metaVar="PROTOCOL",usage="2nd leg of the Overthere protocol. 'local' or 'ssh'")
    public String protocol = "local";

    @Argument(index=2,metaVar="KEY=VALUE",usage="Overthere connection options for the 2nd leg")
    public List<String> args = new ArrayList<String>();

    public static void main( String[] args ) throws Exception {
        Demo demo = new Demo();
        new CmdLineParser(demo).parseArgument(args);
        demo.run();
    }
    public void run() throws Exception {
        // establish a connection to the Jenkins server
        CLI cli = new CLI(url);
        try {
            if (keyFile!=null) {
                // this is the private key that authenticates ourselves to the server
                KeyPair key = CLI.loadKey(keyFile);

                // perform authentication, and in the end obtain the public key that identifies the server
                // (the equivalent of SSH host key.) In this demo, I'm not verifying that we are talking who
                // we are supposed to be talking to, but you can do so by comparing the public key to the record.
                PublicKey server = cli.authenticate(Collections.singleton(key));
                System.out.println("Server key is "+server);
            } else {
                // if the server is unprotected (i.e., local test instance)
                // then no authentication is needed and you are already an admin.
                // Yay!
            }

            // by default, CLI connections are restricted capability-wise, to protect servers from clients.
            // But now we want to start using the channel directly with its full capability, so we try
            // to upgrade the connection. This requires the administer access to the system.
            cli.upgrade();

            // with that, we can now directly use Channel and do all the operations that it can do.
            Channel channel = cli.getChannel();

            final OverthereConnection oc = createOverthereConnection();

            final OutputStream ours = new RemoteOutputStream(System.out);

            try {
                // execute a closure on the server, send the return value (or exception) back.
                // note that Jenkins server doesn't have this code on its JVM, but the remoting layer is transparently
                // sending that for you. This includes all the Overthere code
                channel.call(new Callable<Void, IOException>() {
                    public Void call() throws IOException {
                        PrintStream out = new PrintStream(ours);
                        // this portion executes inside the Jenkins server JVM.
                        try {
                            // now 'oc' is a remote reference to the actual OverthereConnection we created above

                            for (OverthereFile f : oc.getFile("/").listFiles()) {
                                out.println(f.getPath());
                            }

                            OverthereProcess p = oc.startProcess(CmdLine.build("ls", "-la"));
                            p.getStdin().close();
                            IOUtils.copy(p.getStdout(), out);
                            out.println("exit code=" + p.waitFor());
                        } catch (InterruptedException e) {
                            e.printStackTrace(out);
                        }
                        out.flush();
                        return null;
                    }
                });
            } finally {
                oc.close();
            }
        } finally {
            cli.close();
        }
    }

    private OverthereConnection createOverthereConnection() {
        ConnectionOptions opts = new ConnectionOptions();
        for (String arg : args) {
            String[] tokens = arg.split("=");
            opts.set(tokens[0],tokens[1]);
        }

        // this is the 2nd leg of the Overthere
        OverthereConnection con=null;
        if (protocol.equals("local"))
            con = new LocalConnection("local", opts);
        if (protocol.equals("ssh"))
            con = new SshConnectionBuilder("SFTP", opts, new DefaultAddressPortMapper()).connect();
        if (con==null)
            throw new IllegalArgumentException("Unsupported protocol: "+protocol);
        return new RemotableOverthereConnection(con);
    }

    private static final long serialVersionUID = 1L;
}