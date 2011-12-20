package simulation.generator.app;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.griphyn.vdl.classes.LFN;
import org.griphyn.vdl.dax.Job;
import org.griphyn.vdl.dax.Leaf;
import org.griphyn.vdl.dax.Profile;

/**
 * @author Shishir Bharathi
 */
public abstract class AppJob extends Job {

    private Application app;
    private Set<AppFilename> inputs;
    private Set<AppFilename> outputs;
    private Map<String, String> annotations;

    protected AppJob(Application app, String namespace, String name, String version, String jobID) {
        super(namespace, name, version, jobID);
        this.app = app;
        this.app.getDAX().addJob(this);
        this.inputs = new HashSet<AppFilename>();
        this.outputs = new HashSet<AppFilename>();
        this.annotations = new HashMap<String, String>();
    }

    protected void addAnnotation(String key, String value) {
        this.annotations.put(key, value);
    }

    protected Application getApp() {
        return this.app;
    }

    protected Set<AppFilename> getInputs() {
        return this.inputs;
    }

    protected Set<AppFilename> getOutputs() {
        return this.outputs;
    }

    protected void input(AppFilename f) {
        if (!this.inputs.contains(f)) {
            this.addUses(f);
            this.inputs.add(f);
        }
    }

    protected void input(String filename, long size) {
        input(new AppFilename(filename, LFN.INPUT, size));
    }

    protected void input(Collection<AppFilename> filenames) {
        for (AppFilename filename : filenames) {
            input(filename);
        }
    }

    protected void output(AppFilename f) {
        if (!this.outputs.contains(f)) {
            this.addUses(f);
            this.outputs.add(f);
        }
    }

    protected void output(String filename) {
        output(filename, 0);
    }

    protected void output(String filename, long size) {
        output(new AppFilename(filename, LFN.OUTPUT, size));
    }

    protected void output(Collection<AppFilename> filenames) {
        for (AppFilename filename : filenames) {
            output(filename);
        }
    }

    protected void addLink(AppJob child, String filename) {
        addLink(child, filename, 0);
    }

    protected void addLink(AppJob child, String filename, long size) {
        addLink(child, new AppFilename(filename, LFN.OUTPUT, size, LFN.XFER_NOT, false),
                new AppFilename(filename, LFN.INPUT, size, LFN.XFER_NOT, false));
    }

    protected void addLink(AppJob child, AppFilename out, AppFilename in) {
        this.app.getDAX().addChild(child.getID(), this.getID());
        this.output(out);
        child.input(in);
    }

    protected void addChild(AppJob child) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void addChildren(List<? extends AppJob> children) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void finish() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void toXML(Writer stream, String indent, String namespace)
            throws IOException {
        String newline = System.getProperty("line.separator", "\r\n");
        String tag = (namespace != null && namespace.length() > 0) ? namespace + ":job" : "job";
        String tag2 = (namespace != null && namespace.length() > 0) ? namespace + ":argument" : "argument";

        // open tag
        if (indent != null && indent.length() > 0) {
            stream.write(indent);
        }
        stream.write('<');
        stream.write(tag);
        writeAttribute(stream, " id=\"", getID());

        // open tag: print TR
        writeAttribute(stream, " namespace=\"", getNamespace());
        writeAttribute(stream, " name=\"", getName());
        writeAttribute(stream, " version=\"", getVersion());

        // misc. attributes like the search tree depth
        if (getLevel() != -1) {
            writeAttribute(stream, " level=\"", Integer.toString(getLevel()));
        }
        if (getChain() != null && getChain().length() > 0) {
            writeAttribute(stream, " compound=\"", getChain());        // still opening tag: print DV, if available
        }
        if (getDVName() != null) {
            writeAttribute(stream, " dv-namespace=\"", getDVNamespace());
            writeAttribute(stream, " dv-name=\"", getDVName());
            writeAttribute(stream, " dv-version=\"", getDVVersion());
        }

        /*
         * Annotations.
         */
        for (Map.Entry<String, String> entry : this.annotations.entrySet()) {
            stream.write(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
        }

        // open tag: finish opening tag
        stream.write('>');
        if (indent != null) {
            stream.write(newline);        // concat all command line fragments into one big string.
        }
        String newindent = indent == null ? null : indent + "  ";
        if (this.getArgumentCount() > 0) {
            if (newindent != null) {
                stream.write(newindent);
            }
            stream.write('<');
            stream.write(tag2);
            stream.write('>');
            for (Object o : getArgumentList()) {
                // casting will print a mixed content string or Filename element
                ((Leaf)o).shortXML(stream, "", namespace, 0x00);
            }
            stream.write("</");
            stream.write(tag2);
            stream.write('>');
            if (indent != null) {
                stream.write(newline);
            }
        }

        // profiles to be dumped next
        for (Object o : getProfileList()) {
            ((Profile) o).toXML(stream, newindent, namespace);
        }

        // finally any bound stdio descriptor
        // FIXME: really need to dump a Filename element!
        if (getStdin() != null) {
            getStdin().toXML(stream, newindent, namespace);
        }
        if (getStdout() != null) {
            getStdout().toXML(stream, newindent, namespace);
        }
        if (getStderr() != null) {
            getStderr().toXML(stream, newindent, namespace);
        }
        // VDL referenced Filenames to be dumped next
        for (Object o : getUsesList()) {
            ((AppFilename) o).toXML(stream, newindent, namespace);
        }

        // finish job
        if (indent != null && indent.length() > 0) {
            stream.write(indent);
        }
        stream.write("</");
        stream.write(tag);
        stream.write('>');
        if (indent != null) {
            stream.write(newline);
        }
    }
}
