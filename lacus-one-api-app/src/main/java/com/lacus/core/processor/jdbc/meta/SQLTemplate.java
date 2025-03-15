package com.lacus.core.processor.jdbc.meta;

import com.lacus.core.processor.jdbc.fragment.AbstractFragment;
import com.lacus.core.processor.jdbc.fragment.ChooseFragment;
import com.lacus.core.processor.jdbc.fragment.ForEachFragment;
import com.lacus.core.processor.jdbc.fragment.IfFragment;
import com.lacus.core.processor.jdbc.fragment.MixedSQLFragment;
import com.lacus.core.processor.jdbc.fragment.OgnlCache;
import com.lacus.core.processor.jdbc.fragment.SetFragment;
import com.lacus.core.processor.jdbc.fragment.TextFragment;
import com.lacus.core.processor.jdbc.fragment.TrimFragment;
import com.lacus.core.processor.jdbc.fragment.WhereFragment;
import com.lacus.core.processor.jdbc.token.GenericTokenParser;
import com.lacus.service.vo.RequestParamsVO;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLTemplate {

    private AbstractFragment root;

    private Configuration cfg;

    public SQLTemplate(AbstractFragment root, Configuration cfg) {
        super();
        this.root = root;
        this.cfg = cfg;
    }

    public SQLMeta process(Map<String, String> requestKeys, Object data) {
        Context context = new Context(cfg, data, requestKeys);
        calculate(context);
        parseParameter(context);
        return new SQLMeta(context.getSql(), context.getParameter());
    }

    private void parseParameter(final Context context) {
        String sql = context.getSql();
        GenericTokenParser parser = new GenericTokenParser("#{", "}",
                content -> {
                    Object value = OgnlCache.getValue(content,
                            context.getBinding());
                    if (value == null) {
                        throw new RuntimeException("Can not found "
                                + content + " value");
                    }
                    RequestParamsVO param = new RequestParamsVO();
                    param.setColumnName(content);
                    param.setColumnDemo(value.toString());
                    context.addParameter(param);
                    return "?";
                });
        sql = parser.parse(sql);
        context.setSql(sql);
    }

    private void calculate(Context context) {
        this.root.apply(context);
    }

    static public class SqlTemplateBuilder {

        private Configuration cfg;

        private String templateContent;


        public SqlTemplateBuilder(Configuration cfg, String templateContent) {
            super();
            this.cfg = cfg;
            this.templateContent = templateContent;
        }

        public SQLTemplate build() {
            Document document = null;
            try {
                document = buildXml(templateContent);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error constructing the XML object");
            }
            List<AbstractFragment> contents = buildDynamicTag(document.getElementsByTagName("script").item(0));
            return new SQLTemplate(new MixedSQLFragment(contents), cfg);

        }

        private List<AbstractFragment> buildDynamicTag(Node node) {
            List<AbstractFragment> contents = new ArrayList<>();
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                String nodeName = child.getNodeName();
                if (child.getNodeType() == Node.CDATA_SECTION_NODE
                        || child.getNodeType() == Node.TEXT_NODE) {
                    String sql = child.getTextContent();
                    TextFragment textFragment = new TextFragment(sql);
                    contents.add(textFragment);
                } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                    TagHandler tagHandler = this.nodeHandlers.get(nodeName.toLowerCase());
                    if (tagHandler != null) {
                        tagHandler.handleNode(child, contents);
                    }
                }
            }

            return contents;
        }

        private Document buildXml(String templateContent)
                throws ParserConfigurationException, SAXException, IOException {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(false);
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(false);
            factory.setCoalescing(false);
            factory.setExpandEntityReferences(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver((publicId, systemId) -> new InputSource(SQLTemplate.class.getResourceAsStream("script-1.0.dtd")));
            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void error(SAXParseException exception)
                        throws SAXException {
                    throw exception;
                }

                @Override
                public void fatalError(SAXParseException exception)
                        throws SAXException {
                    throw exception;
                }

                @Override
                public void warning(SAXParseException exception)
                        throws SAXException {
                }
            });
            InputSource inputSource = new InputSource(new StringReader(String.format("<?xml version = \"1.0\" ?>\r\n<!DOCTYPE script SYSTEM \"script-1.0.dtd\">\r\n<script>%s</script>", templateContent)));
            return builder.parse(inputSource);
        }

        private Map<String, TagHandler> nodeHandlers = new HashMap<String, TagHandler>() {
            private static final long serialVersionUID = 7123056019193266281L;

            {
                put("trim", new TrimHandler());
                put("where", new WhereHandler());
                put("set", new SetHandler());
                put("foreach", new ForEachHandler());
                put("if", new IfHandler());
                put("choose", new ChooseHandler());
                put("when", new IfHandler());
                put("otherwise", new OtherwiseHandler());
            }
        };

        private interface TagHandler {
            void handleNode(Node nodeToHandle, List<AbstractFragment> targetContents);
        }

        private class TrimHandler implements TagHandler {
            @Override
            public void handleNode(Node nodeToHandle,
                                   List<AbstractFragment> targetContents) {
                List<AbstractFragment> contents = buildDynamicTag(nodeToHandle);
                MixedSQLFragment mixedSqlFragment = new MixedSQLFragment(
                        contents);

                NamedNodeMap attributes = nodeToHandle.getAttributes();
                Node prefixAtt = attributes
                        .getNamedItem("prefix");

                String prefix = prefixAtt == null ? null : prefixAtt.getTextContent();

                Node prefixOverridesAtt = attributes
                        .getNamedItem("prefixOverrides");

                String prefixOverrides = prefixOverridesAtt.getTextContent();

                Node suffixAtt = attributes
                        .getNamedItem("suffix");

                String suffix = suffixAtt == null ? null : suffixAtt.getTextContent();

                Node suffixOverridesAtt = attributes
                        .getNamedItem("suffixOverrides");

                String suffixOverrides = suffixOverridesAtt == null ? null : suffixOverridesAtt.getTextContent();
                TrimFragment trim = new TrimFragment(mixedSqlFragment, prefix,
                        suffix, prefixOverrides, suffixOverrides);
                targetContents.add(trim);
            }
        }

        private class WhereHandler implements TagHandler {
            @Override
            public void handleNode(Node nodeToHandle,
                                   List<AbstractFragment> targetContents) {
                List<AbstractFragment> contents = buildDynamicTag(nodeToHandle);
                MixedSQLFragment mixedSqlFragment = new MixedSQLFragment(
                        contents);
                WhereFragment where = new WhereFragment(mixedSqlFragment);
                targetContents.add(where);
            }
        }

        private class SetHandler implements TagHandler {
            @Override
            public void handleNode(Node nodeToHandle,
                                   List<AbstractFragment> targetContents) {
                List<AbstractFragment> contents = buildDynamicTag(nodeToHandle);
                MixedSQLFragment mixedSqlFragment = new MixedSQLFragment(
                        contents);
                SetFragment set = new SetFragment(mixedSqlFragment);
                targetContents.add(set);
            }
        }

        private class ForEachHandler implements TagHandler {
            @Override
            public void handleNode(Node nodeToHandle,
                                   List<AbstractFragment> targetContents) {
                List<AbstractFragment> contents = buildDynamicTag(nodeToHandle);
                MixedSQLFragment mixedSqlFragment = new MixedSQLFragment(
                        contents);
                NamedNodeMap attributes = nodeToHandle.getAttributes();
                Node collectionAtt = attributes.getNamedItem("collection");

                if (collectionAtt == null) {
                    throw new RuntimeException(nodeToHandle.getNodeName() + " must has a collection attribute !");
                }

                String collection = collectionAtt.getTextContent();

                Node itemAtt = attributes.getNamedItem("item");
                String item = itemAtt == null ? "item" : itemAtt.getTextContent();
                Node indexAtt = attributes.getNamedItem("index");
                String index = indexAtt == null ? "index" : indexAtt.getTextContent();
                Node openAtt = attributes.getNamedItem("open");
                String open = openAtt == null ? null : openAtt.getTextContent();
                Node closeAtt = attributes.getNamedItem("close");
                String close = closeAtt == null ? null : closeAtt.getTextContent();
                Node sparatorAtt = attributes.getNamedItem("separator");
                String separator = sparatorAtt == null ? null : sparatorAtt.getTextContent();
                ForEachFragment forEachSqlFragment = new ForEachFragment(
                        mixedSqlFragment, collection, index, item, open, close,
                        separator);
                targetContents.add(forEachSqlFragment);
            }
        }

        private class IfHandler implements TagHandler {
            @Override
            public void handleNode(Node nodeToHandle,
                                   List<AbstractFragment> targetContents) {
                List<AbstractFragment> contents = buildDynamicTag(nodeToHandle);
                MixedSQLFragment mixedSqlFragment = new MixedSQLFragment(
                        contents);

                NamedNodeMap attributes = nodeToHandle.getAttributes();
                Node testAtt = attributes.getNamedItem("test");
                if (testAtt == null) {
                    throw new RuntimeException(nodeToHandle.getNodeName() + " must has test attribute ! ");
                }
                String test = testAtt.getTextContent();
                IfFragment ifSqlFragment = new IfFragment(mixedSqlFragment,
                        test);
                targetContents.add(ifSqlFragment);
            }
        }

        private class OtherwiseHandler implements TagHandler {
            @Override
            public void handleNode(Node nodeToHandle,
                                   List<AbstractFragment> targetContents) {
                List<AbstractFragment> contents = buildDynamicTag(nodeToHandle);
                MixedSQLFragment mixedSqlFragment = new MixedSQLFragment(
                        contents);
                targetContents.add(mixedSqlFragment);
            }
        }

        private class ChooseHandler implements TagHandler {
            @Override
            public void handleNode(Node nodeToHandle,
                                   List<AbstractFragment> targetContents) {
                List<AbstractFragment> whenSqlFragments = new ArrayList<>();
                List<AbstractFragment> otherwiseSqlFragments = new ArrayList<>();
                handleWhenOtherwiseNodes(nodeToHandle, whenSqlFragments,
                        otherwiseSqlFragments);
                AbstractFragment defaultSqlFragment = getDefaultSqlFragment(otherwiseSqlFragments);
                ChooseFragment chooseSqlFragment = new ChooseFragment(
                        whenSqlFragments, defaultSqlFragment);
                targetContents.add(chooseSqlFragment);
            }

            private void handleWhenOtherwiseNodes(Node chooseSqlFragment,
                                                  List<AbstractFragment> ifSqlFragments,
                                                  List<AbstractFragment> defaultSqlFragments) {
                NodeList children = chooseSqlFragment.getChildNodes();

                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        String nodeName = child.getNodeName();
                        TagHandler handler = nodeHandlers.get(nodeName);
                        if (handler instanceof IfHandler) {
                            handler.handleNode(child, ifSqlFragments);
                        } else if (handler instanceof OtherwiseHandler) {
                            handler.handleNode(child, defaultSqlFragments);
                        }
                    }
                }

            }

            private AbstractFragment getDefaultSqlFragment(
                    List<AbstractFragment> defaultSqlFragments) {
                AbstractFragment defaultSqlFragment = null;
                if (defaultSqlFragments.size() == 1) {
                    defaultSqlFragment = defaultSqlFragments.get(0);
                } else if (defaultSqlFragments.size() > 1) {
                    throw new RuntimeException(
                            "Too many default (otherwise) elements in choose statement.");
                }
                return defaultSqlFragment;
            }
        }

    }

}
