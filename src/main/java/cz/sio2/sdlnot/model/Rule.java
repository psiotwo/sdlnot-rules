package cz.sio2.sdlnot.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.mindswap.pellet.utils.FileUtils;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

/**
 * @author Petr Křemen
 */
public class Rule {

	private boolean active;

	private String name;

	private String ruleString;

	private String inputOntologyIRI;

	private String outputOntologyIRI;

	private Query query;

	private Exception parseException = null;

	public Query getQuery() {
		return query;
	}

	public Exception getException() {
		return parseException;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRuleString() {
		return ruleString;
	}

	public void setRuleString(String ruleString) {
		this.ruleString = ruleString;
		try {
			query = QueryFactory.create(ruleString);
			parseException = null;
		} catch (Exception e) {
			parseException = e;
		}
	}

	public String getInputOntologyIRI() {
		return inputOntologyIRI;
	}

	public void setInputOntologyIRI(String inputOntologyIRI) {
		this.inputOntologyIRI = inputOntologyIRI;
	}

	public String getOutputOntologyIRI() {
		return outputOntologyIRI;
	}

	public void setOutputOntologyIRI(String outputOntologyIRI) {
		this.outputOntologyIRI = outputOntologyIRI;
	}

	public static Rule createRuleReferenceFromString(String ruleString) {
		final Rule rule = new Rule();

		final String[] l = ruleString.trim().split("(\\s)+");
		if (l.length > 4 || l.length < 2) {
			System.out.println("Skipping rule '" + ruleString
					+ "' - got "+l.length+ " arguments");
			return null;
		}

		// first argument - rule (file) name
		rule.setActive(l[0].equals("A"));
		
		// first argument - rule (file) name
		rule.setName(l[1]);

		// second argument - input ontology for rule execution
		if (l.length > 2) {
			rule.setInputOntologyIRI(l[2]);
		}

		// third argument - output ontology
		if (l.length > 3) {
			rule.setOutputOntologyIRI(l[3]);
		}

		return rule;
	}

	public static String createStringForRuleReference(final Rule rule)
			throws IOException {
		String ruleString = "";

		ruleString = rule.isActive() ? "A":"P";

		ruleString += " " + rule.getName();

		if (rule.getInputOntologyIRI() != null) {
			ruleString += " " + rule.getInputOntologyIRI();
		}

		if (rule.getOutputOntologyIRI() != null) {
			ruleString += " " + rule.getOutputOntologyIRI();
		}

		return ruleString;
	}
	
	private File getRuleFile(final File currentDir) {
		return new File(currentDir, this.getName());
	}
	
	public void reload(File currentDir) throws IOException {
		this.setRuleString(FileUtils.readFile(getRuleFile(currentDir)));
	}
	
	public void save(File currentDir) throws IOException {
		PrintWriter out = null;
		Rule rule = this;
		try {
//			if (isSelected) {
				out = new PrintWriter(getRuleFile(currentDir));
				out.println(rule.getRuleString());
//				setDirty(rule, false);
//			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (out != null)
				out.close();
		}
	}
}
