package simulation.generator.app;

import java.io.IOException;
import java.io.Writer;
import org.griphyn.vdl.dax.Filename;
import org.griphyn.vdl.classes.LFN;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Shishir Bharathi
 */
public class AppFilename extends Filename {

    private Map<String, String> annotations;

    public AppFilename(String filename, int type, long size) {
        this(filename, type, size, LFN.XFER_MANDATORY, true);
    }

    public AppFilename(String filename, int type, long size, int transfer, boolean register) {
        super(filename, type);
        this.annotations = new HashMap<String, String>();
        this.annotations.put("size", String.valueOf(size));
        super.setTransfer(transfer);
        super.setRegister(register);
    }

    public Map<String, String> getAnnotations() {
        return this.annotations;
    }

    private String annotatedXML(String temp) {
        int idx = temp.indexOf("/>");
        StringBuffer result = new StringBuffer(temp.length() + 32);
        result.append(temp.substring(0, idx));
        for (Map.Entry<String, String> entry : this.annotations.entrySet()) {
            result.append(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
        }
        result.append("/>\n");

        return result.toString();
    }

    public long getSize() {
        String sizeStr = this.annotations.get("size");
        return Long.parseLong(sizeStr);
    }

    @Override
    public String shortXML(String indent, String namespace, int flag) {
        String temp = super.shortXML(indent, namespace, flag);

        return annotatedXML(temp);
    }

    @Override
    public void shortXML(Writer stream, String indent, String namespace, int flag) throws IOException {
        stream.write(shortXML(indent, namespace, flag));
    }

    @Override
    public String toXML(String indent, String namespace) {
        String temp = super.toXML(indent, namespace);
        temp = temp.replaceFirst("<filename", "<uses");

        return annotatedXML(temp);
    }

    @Override
    public void toXML(Writer stream, String indent, String namespace)
            throws IOException {
        stream.write(toXML(indent, namespace));
    }

    @Override
    public Object clone() {
        AppFilename f = (AppFilename) super.clone();
        f.annotations.putAll(this.annotations);

        return f;
    }

    /*
     * Simple hashCode() and equals().
     */
    @Override
    public int hashCode() {
        return getFilename().hashCode() + getType();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AppFilename other = (AppFilename) obj;
        return getFilename().equals(other.getFilename()) && getType() == other.getType();
    }
}