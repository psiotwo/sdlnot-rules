package cz.sio2.sdlnot.cli;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.RemoveImport;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import cz.sio2.sdlnot.engine.SparqlDLNotRulesEngine;
import cz.sio2.sdlnot.engine.SparqlDLNotRulesEngineController;
import cz.sio2.sdlnot.model.QueryEngineType;
import cz.sio2.sdlnot.model.Rule;
import cz.sio2.sdlnot.model.RuleSpec;
import cz.sio2.sdlnot.util.MappingFileParser;

/**
 * @author Petr Křemen
 */
public class SparqlDLNotRulesCLIController implements
		SparqlDLNotRulesEngineController {

	private static final Logger log = LoggerFactory
			.getLogger(SparqlDLNotRulesCLIController.class.getName());

	public static String PREFIX = "QUERY:";

	private static final long serialVersionUID = -8883469080896399271L;

	private SparqlDLNotRulesEngine engine;
	private RuleSpec rulespec;

	private QueryEngineType queryEngineType = QueryEngineType.PelletMIXED;
	private Boolean treatAllVarsDistinguished = false;
	private OWLOntologyManager m;

	SparqlDLNotRulesCLIController(
			final String ontologyIRI, 
			final File mappingFile,
			final File ruleDir, final QueryEngineType queryEngineType,
			final Boolean treatVarsDistinguished) {
		this.rulespec = new RuleSpec();
		rulespec.setCurrentDir(ruleDir);
		rulespec.reload();

		this.engine = new SparqlDLNotRulesEngine(this);
		this.m = OWLManager.createOWLOntologyManager();
		this.engine.setOntology(loadOntology(ontologyIRI, mappingFile));
		this.queryEngineType = queryEngineType;
		this.treatAllVarsDistinguished = treatVarsDistinguished;
	}

	private OWLOntology loadOntology(final String owlOntologyName,
			final File mappingFile) {
		if (mappingFile != null) {
			log.info("Using mapping file '" + mappingFile + "'.");
			final OWLOntologyIRIMapper mapper =MappingFileParser.getMappings(
					mappingFile);
			m.addIRIMapper(mapper);
			log.info("Mapping file succesfully parsed.");
		}

		log.info("Loading ontology " + owlOntologyName + " ... ");
				
		OWLOntology ontology = null;
		try {
			ontology=m.loadOntology(IRI.create(owlOntologyName));
		} catch (OWLOntologyCreationException e) {
			log.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
		return ontology;
	} 

	public void setOntology(OWLOntology ontology) {
		engine.setOntology(ontology);
	}

	public void setStatus(String status) {
		log.info(status);
	}

	public void run() {
		engine.executeRuleSpec(rulespec);
		try {
			m.saveOntology(this.engine.getMergedOntology());
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public OWLOntologyManager getOWLOntologyManager() {
		return m;
	}

	@Override
	public void updateOntology(
			OWLOntology generatedOntology,OWLOntology mergedOntology, IRI generatedOntologyIRI,
			URI physicalURI) {
		IRI iri = generatedOntologyIRI;
		OWLOntology generatedOntologyToDelete = null;

		final List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		final Collection<OWLOntology> ontologies = m.getOntologies();
		for (OWLOntology oo : ontologies) {
			if (iri.equals(oo.getOntologyID().getOntologyIRI())) {
				log.info("Removing ontology " + iri);
				generatedOntologyToDelete = oo;
			}
			changes.add(new RemoveImport(oo, getOWLOntologyManager()
					.getOWLDataFactory().getOWLImportsDeclaration(iri)));
		}
		m.applyChanges(changes);
		changes.clear();

		if (generatedOntologyToDelete != null) {
			m.removeOntology(generatedOntologyToDelete);
		}

		changes.add(new SetOntologyID(generatedOntology, iri));
		changes.add(new AddImport(mergedOntology, m.getOWLDataFactory()
				.getOWLImportsDeclaration(iri)));
		m.applyChanges(changes);
		m.setOntologyDocumentIRI(generatedOntology, IRI.create(physicalURI));		
		try {
			m.saveOntology(generatedOntology);
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public QueryEngineType getQueryEngineType() {
		return queryEngineType;
	}

	@Override
	public boolean isTreatAllVariablesDistinguished() {
		return treatAllVarsDistinguished;
	}

	public RuleSpec getRuleSpec() {
		return rulespec;
	}

	@Override
	public void setSelect(Rule r, List<String> vars, List<QuerySolution> solutions) {
		// TODO Auto-generated method stub		
	}
	
	@Override
	public void clearResults() {
		
	}
}