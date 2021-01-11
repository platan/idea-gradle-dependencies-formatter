package com.github.platan.idea.dependencies.maven;

import static com.google.common.base.Charsets.UTF_8;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MavenDependenciesDeserializerImpl implements MavenDependenciesDeserializer {

    @Override
    @NotNull
    public List<MavenDependency> deserialize(@NotNull String mavenDependencyXml) throws UnsupportedContentException {
        String wrapperMavenDependencyXml = wrapWithRoot(mavenDependencyXml);
        Document doc = parseDocument(wrapperMavenDependencyXml);
        NodeList nodeList = findNodes(doc);
        return unmarshall(nodeList);
    }

    private String wrapWithRoot(String mavenDependencyXml) {
        return String.format("<root>%s</root>", mavenDependencyXml);
    }

    private Document parseDocument(String wrapperMavenDependencyXml) throws UnsupportedContentException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(wrapperMavenDependencyXml.getBytes(UTF_8)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new UnsupportedContentException();
        }
    }

    private NodeList findNodes(Document doc) throws UnsupportedContentException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression xPathExpression;
        try {
            xPathExpression = xPath.compile("/root/dependency | /root/dependencies/dependency");
        } catch (XPathExpressionException e) {
            throw new IllegalStateException("Invalid XPath expression. ", e);
        }
        try {
            return (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new UnsupportedContentException();
        }
    }

    private List<MavenDependency> unmarshall(NodeList nodeList) throws UnsupportedContentException {
        Schema schema = getSchema();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(MavenDependency.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(schema);
            List<MavenDependency> dependencies = new ArrayList<>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                MavenDependency dependency = unmarshall(nodeList.item(i), unmarshaller);
                dependencies.add(dependency);
            }
            return dependencies;
        } catch (JAXBException e) {
            if (e.getLinkedException() instanceof SAXParseException) {
                throw new DependencyValidationException(e.getLinkedException().getMessage());
            }
            throw new UnsupportedContentException();
        }
    }

    private MavenDependency unmarshall(Node node, Unmarshaller unmarshaller) throws JAXBException {
        return (MavenDependency) unmarshaller.unmarshal(node);
    }

    private Schema getSchema() {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL resource = this.getClass().getResource("/dependency.xsd");
        Schema schema;
        try {
            schema = schemaFactory.newSchema(resource);
        } catch (SAXException e) {
            throw new IllegalStateException(e);
        }
        return schema;
    }

}
