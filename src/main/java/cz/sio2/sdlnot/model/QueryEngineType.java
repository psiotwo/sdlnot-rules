package cz.sio2.sdlnot.model;

/**
 * @author Petr Křemen
 */
public enum QueryEngineType {

	ARQ, PelletARQ, PelletMIXED, PelletONLY;
	
//	public static QueryEngineType fromPellet(com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory.QueryEngineType type) {
//		switch (type) {
//			case ARQ : return PelletARQ;
//			case MIXED : return PelletMIXED;
//			case PELLET: return PelletONLY;
//			default: return null;
//		}
//	}
	
	public com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory.QueryEngineType toPellet() {
		switch (this) {
			case ARQ : return null;
			case PelletARQ : return com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory.QueryEngineType.ARQ;
			case PelletMIXED : return com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory.QueryEngineType.MIXED;
			case PelletONLY: return com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory.QueryEngineType.PELLET;
			default: return null;
		}
	}	
}
