package cz.sio2.sdlnot.cli;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cz.sio2.sdlnot.model.QueryEngineType;

public class SparqlDLNotRulesCLI {
	private final static Options options = 			new Options();
	private final static Option oOntology = 		new Option("o",true,"Ontology Logical IRI");
	private final static Option oMappingFile = 		new Option("m",true,"Mapping file for resolving physical URIs of the input ontology and its imports");
	private final static Option oDirectory = 		new Option("d",true,"Directory with rules. This directory should contain the file 'rulespec' with the definition of rules in the following format: <STATE> <RULE_NAME> {<INPUT_ONTOLOGY_IRI> {<OUTPUT_ONTOLOGY_IRI>}}");
	private final static Option oRegime = 			new Option("e",true,"Entailment regime to be used - valid values are {MIXED|PELLET|ARQ}");
	private final static Option oDistinguished = 	new Option("v",false,"Treat all variables as distinguished ones. This option makes the query result incomplete, but is significantly faster.");
	private final static Option oHelp = 			new Option("h",false,"This help.");
					
	static {	
		oOntology.setRequired(true);
		options.addOption(oOntology);
		oMappingFile.setRequired(true);
		options.addOption(oMappingFile);
		oDirectory.setRequired(true);
		options.addOption(oDirectory);
		options.addOption(oRegime);
		options.addOption(oDistinguished);
		options.addOption(oHelp);
	}
	
	public static void main(String[] args) {
		final Options options = SparqlDLNotRulesCLI.options;
		
		final CommandLineParser parser = new GnuParser();
		try {
			final CommandLine cmd = parser.parse( options, args);
			
			if (cmd.getOptions().length == 0 || cmd.hasOption("-h")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "sr-cli", options );
				return;
			}			
			
			String ontologyIRI = cmd.getOptionValue(oOntology.getOpt());

			File dir = new File(cmd.getOptionValue(oDirectory.getOpt()));
			
			File mappingFile = null;
			if (cmd.hasOption(oMappingFile.getOpt())) {
				mappingFile = new File(cmd.getOptionValue(oMappingFile.getOpt(),""));
			}
			
			Boolean dist=false;
			if (cmd.hasOption(oDistinguished.getOpt())) {
				dist = true;
			}
			
			QueryEngineType regime = QueryEngineType.PelletMIXED;
			if (cmd.hasOption(oDistinguished.getOpt())) {
				dist = true;
			}			
			
			final SparqlDLNotRulesCLIController controller = 
					new SparqlDLNotRulesCLIController(
							ontologyIRI,
							mappingFile,
							dir,
							regime,
							dist);
			controller.run();			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
