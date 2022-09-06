# sdlnot-rules [![Java CI with Maven](https://github.com/psiotwo/sdlnot-rules/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/psiotwo/sdlnot-rules/actions/workflows/maven.yml)

SPARQL-DL Not rules - A Protege 4 plugin for running SPARQL CONSTRUCT queries as SPARQL-DL Not rules.

The rules are read and saved from a predefined directory. Upon execution, the CONSTRUCT query result 
is saved as a special ontology which is imported to the current one.

The tool has two interfaces CLI and Protege Plugin

## CLI
CLI can be accessed by running the SparqlDLNotRulesCLI class. The help about command line options is provided. This option is suitable for scripting. CLI distribution can be obtained by

	mvn -P cli assembly:assembly

## Protege plugin
1) binary distribution can be created using
    
      mvn clean package
      
2) Eclipse project for plugin development in OSGI runtime can be created using

      mvn eclipse:eclipse package
   
   In addition to the project, this generates the MANIFEST.MF in target/classes/META-INF. 
   To run the package in the Eclipse OSGI bundle, just copy the META-INF into the project 
   root. Also, put the 'plugin.xml' file into the root.
 

References
----------

	[1]   pellet.owldl.com/papers/sirin07sparqldl.pdf
	[2]   pellet.owldl.com/papers/kremen08sparqldl.pdf

