package simulation.generator.util;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Shishir Bharathi
 */
public class ConfigParser {
    private Map<String, Distribution> vars;

    public ConfigParser() {
        this.vars = new HashMap<String, Distribution>();
    }
    
    /*
     * Create <Variable type, Distribution> for all entries in the config file.
     */
    public Map<String, Distribution> populate(String filename) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(filename);
        process(document.getDocumentElement());
        return vars;
    }
    
    private void process(Node node) {
        if (node == null) {
            return;
        }

        String name = ((Element) node).getTagName().trim();
        if (name.equals("distributions")) {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                process(child);
            }
        } else {
            NamedNodeMap attrs = node.getAttributes();
            Node nType = attrs.getNamedItem("type");
            String type = nType.getNodeValue().trim();
            if (type.equals("constant")) {
                processConstant(name, node);
            } else if (type.equals("uniform")) {
                processUniform(name, node);
            } else if (type.equals("truncatednormal")) {
                processTruncatedNormal(name, node);
            }
        }

    }

    private void processConstant(String name, Node node) {
        NamedNodeMap attrs = node.getAttributes();
        Node nValue = attrs.getNamedItem("value");
        double value = Double.parseDouble(nValue.getNodeValue().trim());
        Node nScalingFactor = attrs.getNamedItem("scalingFactor");
        double scalingFactor = 1.0;
        if (nScalingFactor != null) {
            scalingFactor = Double.parseDouble(nScalingFactor.getNodeValue().trim());

        } else {
            this.vars.put(name, Distribution.getConstantDistribution(value, scalingFactor));
        }
    }

    private void processUniform(String name, Node node) {
        NamedNodeMap attrs = node.getAttributes();
        Node nMin = attrs.getNamedItem("min");
        double min = Double.parseDouble(nMin.getNodeValue().trim());
        Node nMax = attrs.getNamedItem("max");
        double max = Double.parseDouble(nMax.getNodeValue().trim());
        Node nScalingFactor = attrs.getNamedItem("scalingFactor");
        double scalingFactor = 1.0;
        if (nScalingFactor != null) {
            scalingFactor = Double.parseDouble(nScalingFactor.getNodeValue().trim());
        } else {
            this.vars.put(name, Distribution.getUniformDistribution(min, max, scalingFactor));
        }
    }

    private void processTruncatedNormal(String name, Node node) {
        NamedNodeMap attrs = node.getAttributes();
        Node nMean = attrs.getNamedItem("mean");
        double mean = Double.parseDouble(nMean.getNodeValue().trim());
        Node nVariance = attrs.getNamedItem("variance");
        double variance = Double.parseDouble(nVariance.getNodeValue().trim());
        Node nScalingFactor = attrs.getNamedItem("scalingFactor");
        double scalingFactor = 1.0;
        if (nScalingFactor != null) {
            scalingFactor = Double.parseDouble(nScalingFactor.getNodeValue().trim());
        } else {
            this.vars.put(name, Distribution.getTruncatedNormalDistribution(mean, variance, scalingFactor));
        }
    }

    public static void main(String[] args) throws Exception {
        ConfigParser cp = new ConfigParser();
        Map<String, Distribution> vars = cp.populate(args[0]);
        for (String var : vars.keySet()) {
            System.out.println(var + ": " + vars.get(var).getClass());
        }
    }
}