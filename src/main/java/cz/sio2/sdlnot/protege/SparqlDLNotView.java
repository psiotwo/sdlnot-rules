package cz.sio2.sdlnot.protege;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.view.AbstractActiveOntologyViewComponent;
import org.semanticweb.owlapi.model.OWLOntology;

import cz.sio2.sdlnot.view.SparqlDLNotRulesPanelView;

/**
 * @author Petr Křemen
 */
public class SparqlDLNotView extends AbstractActiveOntologyViewComponent {
	private static final long serialVersionUID = 1L;

	private SparqlDLNotRulesPanelView pnlQueryRules;
	private SparqlDLNotRulesController ctlQueryRules;
	
	private OWLModelManagerListener updateOntologyListener = new OWLModelManagerListener() {
		
		@Override
		public void handleChange(OWLModelManagerChangeEvent event) {
			switch (event.getType()) {
				case ACTIVE_ONTOLOGY_CHANGED:
					updateOntology();
					break;
				default:;
			}
		}
	};
	
	protected void initialiseOntologyView() throws Exception {
		initComponents();		
		getOWLModelManager().addListener(updateOntologyListener);
		updateOntology();
	}
	
	private void updateOntology() {
		ctlQueryRules.setOntology(getOWLModelManager().getActiveOntology());
	}
	
	protected void disposeOntologyView() {
		getOWLModelManager().removeListener(updateOntologyListener);
	}

	
	private void initComponents() {
		final JPanel pnlMain = new JPanel(new BorderLayout());
		pnlQueryRules = new SparqlDLNotRulesPanelView();
		ctlQueryRules = new SparqlDLNotRulesController(getOWLWorkspace(),pnlQueryRules);
		pnlMain.add(pnlQueryRules, BorderLayout.CENTER);		
		this.setLayout(new BorderLayout());
		this.add(pnlMain, BorderLayout.CENTER);
	}
	
	@Override
	protected void updateView(OWLOntology activeOntology) throws Exception {
		// TODO Auto-generated method stub
	}
}
