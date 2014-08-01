package cz.sio2.sdlnot.protege;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.OWLWorkspace;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveImport;
import org.semanticweb.owlapi.model.SetOntologyID;


import cz.sio2.sdlnot.engine.SparqlDLNotRulesEngine;
import cz.sio2.sdlnot.engine.SparqlDLNotRulesEngineController;
import cz.sio2.sdlnot.model.QueryEngineType;
import cz.sio2.sdlnot.model.Rule;
import cz.sio2.sdlnot.model.RuleSpec;
import cz.sio2.sdlnot.view.CheckBoxEditor;
import cz.sio2.sdlnot.view.SparqlDLNotRulesPanelView;
import cz.sio2.sdlnot.view.SparqlDLNotRulesTableCellRenderer;
import cz.sio2.sdlnot.view.SparqlDLNotTableModel;

// UNUSED - only for easier generation of valid Eclipse project
import org.apache.commons.cli.CommandLine;


/**
 * @author Petr Křemen
 */
public class SparqlDLNotRulesController implements SparqlDLNotRulesEngineController {

	private static final Logger log = Logger
			.getLogger(SparqlDLNotRulesController.class.getName());

	public static String PREFIX = "QUERY:";
	
	private static final String DEFAULT_SPARQL = " PREFIX owl: <http://www.w3.org/2002/07/owl#>"
			+ "\n CONSTRUCT {"
			+ "\n   ?x a owl:NamedIndividual."
			+ "\n } WHERE {" + "\n   ?x a owl:Class" + " }";

	private static final long serialVersionUID = -8883469080896399271L;
	
	private SparqlDLNotRulesPanelView view;	
	private SparqlDLNotTableModel model;
	
	private OWLWorkspace workspace;
	
	private SparqlDLNotRulesEngine engine;	
	private RuleSpec rulespec;
	
	SparqlDLNotRulesController(final OWLWorkspace workspace, final SparqlDLNotRulesPanelView view) {
		this.view = view;
		this.workspace = workspace;
		this.init();
	}

	private void init() {
		this.engine = new SparqlDLNotRulesEngine(this);
		this.rulespec = new RuleSpec();
		
		this.view.init();
		initControllers();
		initModels();
		updateActions();		
	}

	private void updateActions() {
		view.getBtnDeleteSelectedRule().setEnabled(view.getTblRules().getSelectedRowCount() > 0);
		boolean isSomeSelected = view.getTblRules().getSelectedRowCount() > 0;
		boolean isSomeActive = rulespec.getActiveCount() > 0;
		boolean rulesNotEmpty = rulespec.getRuleList().size() > 0;
		view.getBtnRunActive().setEnabled(isSomeActive);
		view.getBtnDeleteSelectedRule().setEnabled(isSomeSelected);
		view.getBtnRevertSelectedRule().setEnabled(isSomeSelected);
		view.getBtnSaveAll().setEnabled(isSomeSelected);
		view.getBtnMoveUpSelectedRule().setEnabled(isSomeSelected);
		view.getBtnCreateRule().setEnabled(rulespec.getCurrentDir() != null);
		view.getBtnActivateAll().setEnabled(rulesNotEmpty);
		view.getBtnDeactivateAll().setEnabled(rulesNotEmpty);
		view.getBtnInvertActive().setEnabled(rulesNotEmpty);
		view.getBtnReload().setEnabled(rulespec.getCurrentDir() != null);
		view.resizeRulesTableColumnWidth();
	}
	
	public void setOntology(OWLOntology ontology) {
		engine.setOntology(ontology);
	}

	public void setStatus(String status) {
		view.getLblStatus().setText(new Date()+" : "+status);
	}	
			
	private OWLModelManager getOWLModelManager() {
		return workspace.getOWLEditorKit()
				.getOWLModelManager();
	}


	private Rule getSelectedRule() {
		int index = view.getTblRules().getSelectedRow();
		if ( index < 0 ) {
			return null;
		} else {
			return rulespec.getRuleAtIndex(index);	
		}		
	}
	
	private void initControllers() {
		view.getBtnOpenDir().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = view.getFcRuleDir().showOpenDialog(view);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					rulespec.setCurrentDir(view.getFcRuleDir().getSelectedFile());
					rulespec.reload();
					view.getLblCurrentDir().setText(rulespec.getCurrentDir().getPath());
					updateActions();
				}
			}
		});
		view.getBtnReload().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (model.getDirtyCount() == 0
								|| view.showModifiedDialog() == 0) {
							view.invalidate();
							rulespec.reload();
							view.validate();
						}
					}
				});
			}
		});
		view.getTblRules().getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						if (e.getValueIsAdjusting()) {
							return;
						}

						final Rule selectedRule = getSelectedRule();						
						if ( selectedRule != null ) {
							view.getTxpQuery().setText(selectedRule.getRuleString());							
							updateActions();
						}
					}
				});
		view.getTxpQuery().addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				final Rule selectedRule = getSelectedRule();
				if (selectedRule != null) {
					selectedRule.setRuleString(view.getTxpQuery().getText());
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
			}
		});
		view.getTxpQuery().addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				final Rule selectedRule = getSelectedRule();
				if (selectedRule != null && !model.isDeleted(selectedRule)) {
					model.setDirty(selectedRule, true);
					repaintTable();
				}
			}
		});
		view.getBtnCreateRule().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String ruleName = view.showCreateNewDialog();
				
				if ( ruleName.contains(" ")) { // TODO extend to whitespace
					ruleName = ruleName.replace(" ","");				
				}
				
				String postfix = "";
				int i = -1;
				while ( (rulespec.getRuleForName(ruleName+postfix) ) != null ) {
					i++;
					postfix = ""+i;
				}
				
				ruleName = ruleName+postfix;
									
				Rule r = new Rule();
				r.setName(ruleName);
				r.setRuleString(DEFAULT_SPARQL);
				rulespec.addRule(r);				
				int index = rulespec.indexOf(r.getName());				
				model.setDirty(r, true);
				view.getTblRules().setRowSelectionInterval(index, index);
				repaintTable();
			}
		});
		// TODO currently inefficient - just single selection -, but fast to
		// implement
		view.getBtnDeleteSelectedRule().addActionListener(new UniversalActionListener() {
			protected void doFor(boolean isSelected, boolean isActive,
					Rule rule) {
				if (isSelected) {
//					rulespec.removeRule(rule);
					model.setDeleted(rule, true);
					repaintTable();
				}
			}
		});
		// TODO currently inefficient - just single selection -, but fast to
		// implement
		view.getBtnRevertSelectedRule().addActionListener(new UniversalActionListener() {
			protected void doFor(boolean isSelected, boolean isActive,
					Rule r) {
				try {
					if (isSelected) {
						rulespec.revert(r);
						model.setDirty(r, false);
						model.setDeleted(r, false);
						repaintTable();
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		// TODO currently inefficient - just single selection -, but fast to
		// implement
		view.getBtnSaveAll().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (model.getDeletedCount() > 0 && view.showConfirmDeleteDialog() == 0) {
					for(final Rule rule : new ArrayList<Rule>(rulespec.getRuleList())) {
						if ( model.isDeleted(rule)) {
							rulespec.removeRule(rule);
						}
					}
				}
				rulespec.saveAll();
				model.clear();
				repaintTable();
			}
		});
		view.getBtnMoveUpSelectedRule().addActionListener(new UniversalActionListener() {
			@Override
			protected void doFor(boolean isSelected, boolean active,
					Rule r) {
				if (isSelected)
					rulespec.moveupRule(r);
			}
		});
		view.getBtnRunActive().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				engine.executeRuleSpec(rulespec);
			}
		});
		view.getBtnActivateAll().addActionListener(new UniversalActionListener() {

			@Override
			protected void doFor(boolean isSelected, boolean isActive,
					Rule r) {
				r.setActive(true);
				repaintTable();
			}
		});
		view.getBtnDeactivateAll().addActionListener(new UniversalActionListener() {

			@Override
			protected void doFor(boolean isSelected, boolean isActive,
					Rule r) {
				r.setActive(false);
				repaintTable();
			}
		});
		view.getBtnInvertActive().addActionListener(new UniversalActionListener() {

			@Override
			protected void doFor(boolean isSelected, boolean isActive,
					Rule r) {
				r.setActive(!isActive);
				repaintTable();
			}
		});
	}	

	private void repaintTable() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				view.getTblRules().repaint();
			}
		});		
	}

	private void initModels() {
		model = new SparqlDLNotTableModel(rulespec);
		view.getTblRules().setModel(model);
		view.getTblRules().getColumnModel().getColumn(0)
				.setCellEditor(new CheckBoxEditor(new JCheckBox()) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void fireStateChanged() {
						updateActions();
					}
				});
		view.getTblRules().getColumnModel().getColumn(1)
				.setCellRenderer(new SparqlDLNotRulesTableCellRenderer());
	}

	public abstract class UniversalActionListener implements ActionListener {

		protected abstract void doFor(boolean selected, boolean active,
				final Rule r);

		public void actionPerformed(ActionEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < rulespec.getRuleList().size(); i++) {
						final Rule r = rulespec.getRuleAtIndex(i);
						doFor(i == view.getTblRules().getSelectedRow(), r.isActive(), r);
					}
					view.repaint();
				}
			});
		}
	}

	@Override
	public OWLOntologyManager getOWLOntologyManager() {
		return getOWLModelManager().getOWLOntologyManager();
	}

	@Override
	public void updateOntology(	OWLOntology generatedOntology, 
			OWLOntology mergedOntology, 
			IRI generatedOntologyIRI, 
			URI physicalURI) {
		final OWLModelManager mm = getOWLModelManager();
		IRI iri = generatedOntologyIRI;
		OWLOntology generatedOntologyToDelete = null;
		
		final List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		final Collection<OWLOntology> ontologies = mm.getOntologies();
		for (OWLOntology oo : ontologies) {
			if (iri.equals(oo.getOntologyID().getOntologyIRI())) {
				log.info("Removing ontology " + iri);
				generatedOntologyToDelete = oo;
			}
			changes.add(new RemoveImport(oo, getOWLOntologyManager().getOWLDataFactory()
					.getOWLImportsDeclaration(iri)));
		}
		mm.applyChanges(changes);
		changes.clear();
		
		if (generatedOntologyToDelete != null) {
			if ( !mm.removeOntology(generatedOntologyToDelete) ) {
				log.info("Removing ontology " + generatedOntologyToDelete.getOntologyID() + " NOT succesful.");
			}
		}
		
		changes.add(new SetOntologyID(generatedOntology, iri));
		changes.add(new AddImport(mergedOntology, mm.getOWLDataFactory()
				.getOWLImportsDeclaration(iri)));
		mm.applyChanges(changes);	
		mm.setPhysicalURI(generatedOntology, physicalURI);		
	}

	@Override
	public QueryEngineType getQueryEngineType() {
		return (QueryEngineType) view.getLstRegime().getSelectedItem();
	}

	@Override
	public boolean isTreatAllVariablesDistinguished() {
		return view.getChbTreatAllVariablesDistinguished().isSelected();
	}

	public RuleSpec getRuleSpec() {
		return rulespec;
	}
}