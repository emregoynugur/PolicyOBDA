package policy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.semanticweb.owlapi.model.IRI;
import org.semarglproject.vocab.RDF;
import org.semarglproject.vocab.XSD;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import utils.Config;
import utils.SchemaDateFormat;
import utils.SystemFunctionParser;

public class PolicyReader {

	private String ontologyIri = Config.getInstance().getOntologyIri();

	private final static String REGEX = ",(?=(?:[^']*'[^']*')*[^']*$)(?=(?:[^()]*\\([^()]*\\))*[^()]*$)";
	
	public Triple getObjectPropertyTriple(String s, String rel, String o) {
		Node sub = createQueryNode(s);
		Node pred = NodeFactory.createURI(ontologyIri + rel);
		Node obj = createQueryNode(o);
		return new Triple(sub, pred, obj);
	}

	public Triple getMembershipTriple(String cls, String var) {
		Node sub = createQueryNode(var);
		Node pred = NodeFactory.createURI(RDF.TYPE);
		Node obj = NodeFactory.createURI(ontologyIri + cls);
		return new Triple(sub, pred, obj);
	}

	public Triple getDataPropertyTriple(String s, String rel, String d) {
		Node sub = createQueryNode(s);
		Node pred = NodeFactory.createURI(ontologyIri + rel);
		Node data = getDataNode(d);
		return new Triple(sub, pred, data);
	}

	public Node createQueryNode(String var) {
		try {
			String v = var;
			if (v.contains("'")) {
				v = v.replace("'", "");
				return NodeFactory.createURI(ontologyIri + v);
			}
			URL url = new URL(v);
			return NodeFactory.createURI(IRI.create(url).toString());
		} catch (Exception e) {
			return NodeFactory.createVariable(var);
		}
	}

	private Node getDataNode(String data) {
		if (data.charAt(0) == '\"')
			return NodeFactory.createLiteral(data.replace("\"", ""), XSD.STRING);

		if (SchemaDateFormat.isDateTime(data))
			return NodeFactory.createLiteral(data, XSD.TIME);

		try {
			Integer.parseInt(data);
			return NodeFactory.createLiteral(data, XSD.INT);
		} catch (NumberFormatException e) {
			System.out.println(data + " is not a int");
		}

		try {
			Double.parseDouble(data);
			return NodeFactory.createLiteral(data, XSD.DOUBLE);
		} catch (NumberFormatException e) {
			System.out.println(data + " is not a double");
		}

		try {
			Boolean.parseBoolean(data);
			return NodeFactory.createLiteral(data, XSD.BOOLEAN);
		} catch (NumberFormatException e) {
			System.out.println(data + " is not a boolean");
		}

		return null;
	}

	public Query parseConditions(String[] predicates) {

		ElementTriplesBlock pattern = new ElementTriplesBlock();
		Set<String> systemFunctions = new HashSet<String>();
		Set<String> variables = new HashSet<String>();
		for (String p : predicates) {
			if (p.contains("sys:")) {
				systemFunctions.add(p.substring(p.indexOf("sys:") + 4).replace("'", ""));
			} else {
				String pred = p.substring(0, p.indexOf("("));
				String[] params = p.substring(p.indexOf("(") + 1, p.indexOf(")")).split(",");

				if (params.length == 1) {
					String var = params[0].replace("?", "");
					pattern.addTriple(getMembershipTriple(pred, var));
					variables.add(var);
				} else if (params.length == 2) {
					String sub = params[0].replace("?", "");
					if (params[1].charAt(0) == '?' || params[1].charAt(0) == '\'') {
						String obj = params[1].replace("?", "");
						pattern.addTriple(getObjectPropertyTriple(sub, pred, obj));
						variables.add(sub);
						variables.add(obj);
					} else {
						// data property
						pattern.addTriple(getDataPropertyTriple(sub, pred, params[1]));
						variables.add(sub);
					}
				}
			}
		}

		Query q = QueryFactory.make();

		if (systemFunctions != null && systemFunctions.size() > 0) {
			ElementGroup group = new ElementGroup();
			group.addElement(pattern);

			List<ElementFilter> filters = new SystemFunctionParser().parseSystemFunctions(systemFunctions);
			for (ElementFilter filter : filters)
				group.addElementFilter(filter);
			q.setQueryPattern(group);
		} else {
			q.setQueryPattern(pattern);
		}
		q.setQuerySelectType();

		for (String v : variables)
			if (!v.contains("'"))
				q.addResultVar(v);

		return q;
	}

	public ArrayList<Policy> readPolicies() throws ParserConfigurationException, IOException, SAXException {
		ArrayList<Policy> policies = new ArrayList<Policy>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		Document document = builder.parse(new File(Config.getInstance().getPolicyFile()));
		NodeList nodeList = document.getDocumentElement().getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			org.w3c.dom.Node node = nodeList.item(i);
			if (node.getNodeName().equals("Policy")) {
				String name = node.getAttributes().getNamedItem("Name").getNodeValue();
				String modality = node.getAttributes().getNamedItem("Modality").getNodeValue();
				String addressee = node.getAttributes().getNamedItem("Addressee").getNodeValue();
				String addresseeRole = "";
				String actionVar = "";

				Query actionDescription = null;
				Query activation = null;
				Query expiration = null;

				double cost = 0;
				double deadline = 0;
				TimeUnit unit = TimeUnit.SECONDS;

				NodeList childList = node.getChildNodes();
				for (int j = 0; j < childList.getLength(); j++) {
					org.w3c.dom.Node childNode = childList.item(j);

					String label = childNode.getNodeName().replace(" ", "");
					String text = childNode.getTextContent().replace(" ", "");

					if (text.trim().length() != 0) {
						switch (label) {
						case "AddresseeRole":
							addresseeRole = text;
							break;
						case "Activation":
							text = addresseeRole + "," + text;
							activation = parseConditions(text.split(REGEX));
							break;
						case "ActionDescription":
							actionVar = childNode.getAttributes().getNamedItem("Var").getNodeValue();
							actionDescription = parseConditions(text.split(REGEX));
							break;
						case "Expiration":
							expiration = parseConditions(text.split(REGEX));
							break;
						case "Deadline":
							String[] condition = text.split(":");
							if (condition.length == 2) {
								deadline = Double.parseDouble(condition[0]);
								unit = TimeUnit.valueOf(condition[1].toUpperCase());
							}
							break;
						case "Cost":
							cost = Double.parseDouble(text);
							break;
						default:
							break;
						}
					}
				}

				policies.add(new Policy(name, modality, addressee, addresseeRole, actionVar, actionDescription,
						activation, cost, deadline, unit, expiration));
			}
		}

		return policies;
	}

	public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
//		PolicyReader reader = new PolicyReader();
//		ArrayList<Policy> readPolicies = reader.readPolicies();
	}
}
