package cz.sio2.sdlnot.engine;

import java.net.URI;
import java.util.List;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.hp.hpl.jena.query.QuerySolution;

import cz.sio2.sdlnot.model.QueryEngineType;
import cz.sio2.sdlnot.model.Rule;
import cz.sio2.sdlnot.model.RuleSpec;

public interface SparqlDLNotRulesEngineController {
	
	public OWLOntologyManager getOWLOntologyManager();
	
	public void setStatus( String status );
	
	public void updateOntology( final OWLOntology generatedOntology,  final IRI generatedOntologyIRI, final IRI previousOntologyIRI, final URI physicalURI );
	
	public QueryEngineType getQueryEngineType();

	public boolean isTreatAllVariablesDistinguished();
	
	public RuleSpec getRuleSpec();
	
	public void setSelect( final Rule r, final List<String> vars, final List<QuerySolution> resultSet);

	public URI getOntologyPhysicalURI(final OWLOntology o);
	
	public void clearResults();
}
