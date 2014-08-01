package cz.sio2.sdlnot.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;

public class MappingFileParser {

	private static Map<String, URI> parseMappings(final File mf) {
		final Map<String, URI> map = new HashMap<String, URI>();
		String line = null;
		final File defaultDir = mf.getParentFile();
		BufferedReader r = null;
		try {
			r = new BufferedReader(new InputStreamReader(
					new FileInputStream(mf)));
			while ((line = r.readLine()) != null) {
				final StringTokenizer t = new StringTokenizer(line, ">");
				if (t.countTokens() != 2) {
					System.out
							.println("Ignoring line '" + line
									+ "' - invalid number of tokens="
									+ t.countTokens());
					continue;
				}

				final String uriName = t.nextToken().trim();
				final String fileName = t.nextToken().trim();
				final File actualFile = (new File(fileName).isAbsolute()) ? new File(
						fileName) : new File(defaultDir, fileName);

				map.put(uriName, actualFile.toURI());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (r != null) {
				try {
					r.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return map;
	}

	public static OWLOntologyIRIMapper getMappings(final File mf) {
		final Map<String, URI> map = parseMappings(mf);
		return new OWLOntologyIRIMapper() {

			@Override
			public IRI getDocumentIRI(IRI ontologyIRI) {
				final URI value = map.get(ontologyIRI.toString());

				if (value == null) {
					return null;
				} else {
					return IRI.create(value);
				}
			}
		};
	}
}
