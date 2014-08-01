package cz.sio2.sdlnot.engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import cz.sio2.sdlnot.model.QueryEngineType;
import cz.sio2.sdlnot.model.Rule;
import cz.sio2.sdlnot.model.RuleSpec;

/**
 * @author Petr Křemen
 */
public class SparqlDLNotRulesEngine {

	private static final Logger log = Logger
			.getLogger(SparqlDLNotRulesEngine.class.getName());

	public static String PREFIX = "QUERY:";

	private static final String DEFAULT_SPARQL = " PREFIX owl: <http://www.w3.org/2002/07/owl#>"
			+ "\n CONSTRUCT {"
			+ "\n   ?x a owl:NamedIndividual."
			+ "\n } WHERE {" + "\n   ?x a owl:Class" + " }";

	private static final long serialVersionUID = -8883469080896399271L;

	/**
	 * Current query ontology
	 */
	private OWLOntology queryOntology;

	private SparqlDLNotRulesEngineController controller;

	public SparqlDLNotRulesEngine(
			final SparqlDLNotRulesEngineController controller) {
		this.controller = controller;
	}

	public void setOntology(OWLOntology ontology) {
		if (ontology != this.queryOntology) {
			this.queryOntology = ontology;
		}
	}

	public OWLOntology getOntology() {
		return queryOntology;
	}

	public OWLOntology getMergedOntology() {
		final IRI mergedOntologyIRI = IRI.create(queryOntology.getOntologyID()
				.getDefaultDocumentIRI() + "-merged");
		final OWLOntologyManager mm = controller.getOWLOntologyManager();
		if (mm.contains(mergedOntologyIRI)) {
			return mm.getOntology(mergedOntologyIRI);
		} else {
			try {
				final OWLOntology mergedOntology = mm
						.createOntology(mergedOntologyIRI);
				mm.setOntologyFormat(mergedOntology, new RDFXMLOntologyFormat());
				final String mergedOntologyFileName = mergedOntologyIRI
						.toURI()
						.toString()
						.substring(
								mergedOntologyIRI.toURI().toString()
										.lastIndexOf("/") + 1)
						+ ".owl";
				mm.setOntologyDocumentIRI(
						mergedOntology,
						IRI.create(controller.getRuleSpec().getOutputDir().toURI() + "/"
								+ mergedOntologyFileName));
				mm.applyChange(new AddImport(mergedOntology, mm
						.getOWLDataFactory().getOWLImportsDeclaration(
								queryOntology.getOntologyID().getDefaultDocumentIRI())));
				return mergedOntology;
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}

	private Dataset kb2ds(KnowledgeBase kb) {
		final PelletInfGraph graph = new org.mindswap.pellet.jena.PelletReasoner()
				.bind(kb);
		return DatasetFactory.create(ModelFactory.createInfModel(graph));
	}

	private IRI getOntologyIRIForRuleName(final String ruleName) {
		return IRI.create(queryOntology.getOntologyID().getOntologyIRI()
				.toString()
				+ "/" + ruleName);
	}

	/**
	 * This method gets an OWL ontology used as an input for the supplied rule.
	 */
	private OWLOntology getInputOntologyForRule(Rule r)
			throws OWLOntologyCreationException {
		String inputOntology = r.getInputOntologyIRI();

		if (inputOntology == null) {
			return getMergedOntology();
		}

		IRI iri;
		if (inputOntology.startsWith(PREFIX)) {
			iri = getOntologyIRIForRuleName(inputOntology.substring(PREFIX
					.length()));
		} else {
			iri = IRI.create(inputOntology);
		}

		final OWLOntologyManager m = controller.getOWLOntologyManager();
		OWLOntology o;
		if (m.contains(iri)) {
			o = m.getOntology(iri);
		} else {
			o = m.loadOntology(iri);
		}
		return o;
	}

	public void executeRuleSpec(final RuleSpec r) {
		for(final Rule rule : r.getRuleList() ) {
			if (rule.isActive()) {
				executeRule(rule);
			}
		}
	}
	
	private void executeRule(final Rule r) {
		try {
			if (r.getException() != null) {
				log.info("The query " + r.getName()
						+ " is not valid, execution cancelled.");
				controller.setStatus(r.getException().getMessage());
				return;
			}

			PelletOptions.TREAT_ALL_VARS_DISTINGUISHED = controller
					.isTreatAllVariablesDistinguished();

			final OWLOntology queryOntology = getInputOntologyForRule(r);

			final PelletReasoner reasoner = PelletReasonerFactory.getInstance()
					.createReasoner(queryOntology);

			log.info("Ontology size: " + reasoner.getKB().getInfo());

			QueryEngineType type = (QueryEngineType) controller
					.getQueryEngineType();

			final QueryExecution qe = SparqlDLExecutionFactory.create(
					r.getQuery(), kb2ds(reasoner.getKB()), null,
					type.toPellet());

			final ByteArrayOutputStream w = new ByteArrayOutputStream();
			qe.execConstruct().write(w);

			// loaded generated ontology
			final OWLOntology generatedOntology = controller
					.getOWLOntologyManager().loadOntologyFromOntologyDocument(
							new ByteArrayInputStream(w.toByteArray()));
			controller.updateOntology(generatedOntology, getMergedOntology(),
					getOntologyIRIForRuleName(r.getName()), controller
							.getRuleSpec().getResultFile(r).toURI());
			controller.setStatus("Rule " + r.getName()
					+ " successfully executed");
		} catch (OWLOntologyCreationException e1) {
			controller.setStatus(e1.getMessage());
		}
	}
}
