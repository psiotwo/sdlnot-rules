package cz.sio2.sdlnot.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import cz.sio2.sdlnot.model.QueryEngineType;

/**
 * @author Petr Křemen
 */
public class SparqlDLNotRulesPanelView extends JPanel {

	private static Object[] YES_NO_OPTIONS = { "Yes", "No" };	

	private static final long serialVersionUID = -8883469080896399271L;

	private RTextScrollPane scpQuery;
	private RSyntaxTextArea txpQuery;
	private JScrollPane scpRules;
	private JTable tblRules;

	private JButton btnReload;
	private JButton btnRunActive;
	private JButton btnCreateRule;
	private JButton btnDeleteSelectedRule;
	private JButton btnSaveAll;
	private JButton btnRevertSelectedRule;
	private JButton btnMoveUpSelectedRule;
	private JButton btnInvertActive;
	private JButton btnDeactivateAll;
	private JButton btnActivateAll;
	private JComboBox<QueryEngineType> lstRegime;
	private JCheckBox chbTreatAllVariablesDistinguished = new JCheckBox();

	private JFileChooser fcRuleDir;
	private JButton btnOpenDir;
	private JLabel lblCurrentDir;
	private JLabel lblStatus;
//	private JCheckBox chbUseImportsClosure;

	public SparqlDLNotRulesPanelView() {}

	public void init() {
		initView();
	}
	
	private void initView() {
		this.setLayout(new GridBagLayout());
		fcRuleDir = new JFileChooser();
		fcRuleDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		final JToolBar tlbTop = new JToolBar();
		tlbTop.setRollover(true);
		this.setLayout(new GridBagLayout());

		btnReload = new JButton("Reload");
		btnReload.setToolTipText("Reloads the rule set from the current directory.");
		tlbTop.add(btnReload);

		btnOpenDir = new JButton("Open Dir");
		btnOpenDir.setToolTipText("Loads the rule set from a user-specified directory.");
		tlbTop.add(btnOpenDir);

		btnCreateRule = new JButton("Create rule");
		btnCreateRule.setToolTipText("Creates a new rule and appends it at the end of the rule list.");
		tlbTop.add(btnCreateRule);

		btnSaveAll = new JButton("Save All");
		btnSaveAll.setToolTipText("Saves the rule set, i.e. all rules, their active status and order.");
		tlbTop.add(btnSaveAll);

		tlbTop.addSeparator();

		btnDeleteSelectedRule = new JButton("Delete");
		btnDeleteSelectedRule.setToolTipText("Deletes selected rule.");
		tlbTop.add(btnDeleteSelectedRule);

		btnRevertSelectedRule = new JButton("Revert");
		btnRevertSelectedRule.setToolTipText("Reverts the current rule.");
		tlbTop.add(btnRevertSelectedRule);

		btnMoveUpSelectedRule = new JButton("Move up");
		btnMoveUpSelectedRule.setToolTipText("Moves the current rule one position up in the list.");
		tlbTop.add(btnMoveUpSelectedRule);

		tlbTop.addSeparator();

		btnActivateAll = new JButton("Activate all");
		btnActivateAll.setToolTipText("Activates all rules in the rule set.");
		tlbTop.add(btnActivateAll);

		btnDeactivateAll = new JButton("Deactivate all");
		btnDeactivateAll.setToolTipText("Deactivates all rules in the rule set.");
		tlbTop.add(btnDeactivateAll);

		btnInvertActive = new JButton("Invert activation");
		btnInvertActive.setToolTipText("Deactivates all active rules and activates all inactive rules");
		tlbTop.add(btnInvertActive);

		tlbTop.addSeparator();

		btnRunActive = new JButton("Run");
		btnRunActive.setToolTipText("Runs all active rules in the order specified in the rule list.");
		tlbTop.add(btnRunActive);

		lstRegime = new JComboBox<QueryEngineType>(QueryEngineType.values());
		tlbTop.add(lstRegime);

		tlbTop.addSeparator();

//		chbUseImportsClosure = new JCheckBox("Use merged ontology");
//		tlbTop.add(chbUseImportsClosure);

		chbTreatAllVariablesDistinguished = new JCheckBox("Treat vars dist.");
		chbTreatAllVariablesDistinguished.setToolTipText("Treats all variables as distinguished. If not checked, bnodes in the query are considered undistinguished, which gives more precise results w.r.t. the semantics of OWL, but significantly slows down the execution.");
		tlbTop.add(chbTreatAllVariablesDistinguished);

		lblCurrentDir = new JLabel("");

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(tlbTop, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(lblCurrentDir, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.3;
		gbc.weighty = 0.9;
		this.add(createMainPanel(), gbc);

		lblStatus = new JLabel("");
		lblStatus.setPreferredSize(new Dimension(0, 10));
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0;
		gbc.weighty = 0;
		this.add(lblStatus, gbc);
	}
	
	private JComponent createMainPanel() {
		txpQuery = new RSyntaxTextArea();
		txpQuery.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
		scpQuery = new RTextScrollPane(txpQuery, true);
		scpQuery.setFoldIndicatorEnabled(true);
		scpQuery.setViewportView(txpQuery);

		scpRules = new JScrollPane();
		tblRules = new JTable();
		tblRules.setFillsViewportHeight(true);
		tblRules.setPreferredSize(new Dimension(100, 100));
		tblRules.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblRules.setEditingColumn(0);
		tblRules.setAutoCreateColumnsFromModel(true);
		tblRules.setAutoCreateRowSorter(true);
		tblRules.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		scpRules.setSize(tblRules.getPreferredSize());
		scpRules.setViewportView(tblRules);
		JSplitPane spp = new JSplitPane();
		spp.setRightComponent(scpQuery);
		spp.setDividerLocation(0.3);
		spp.setLeftComponent(tblRules);
		return spp;
	}
	
	public void resizeRulesTableColumnWidth() {
		final TableColumnModel columnModel = tblRules.getColumnModel();
		columnModel.getColumn(0).setMaxWidth(30);
	}
	
	public RTextScrollPane getScpQuery() {
		return scpQuery;
	}

	public RSyntaxTextArea getTxpQuery() {
		return txpQuery;
	}

	public JScrollPane getScpRules() {
		return scpRules;
	}

	public JTable getTblRules() {
		return tblRules;
	}

	public JButton getBtnReload() {
		return btnReload;
	}

	public JButton getBtnRunActive() {
		return btnRunActive;
	}

	public JButton getBtnCreateRule() {
		return btnCreateRule;
	}

	public JButton getBtnDeleteSelectedRule() {
		return btnDeleteSelectedRule;
	}

	public JButton getBtnSaveAll() {
		return btnSaveAll;
	}

	public JButton getBtnRevertSelectedRule() {
		return btnRevertSelectedRule;
	}

	public JButton getBtnMoveUpSelectedRule() {
		return btnMoveUpSelectedRule;
	}

	public JButton getBtnInvertActive() {
		return btnInvertActive;
	}

	public JButton getBtnDeactivateAll() {
		return btnDeactivateAll;
	}

	public JButton getBtnActivateAll() {
		return btnActivateAll;
	}

	public JComboBox<QueryEngineType> getLstRegime() {
		return lstRegime;
	}

	public JCheckBox getChbTreatAllVariablesDistinguished() {
		return chbTreatAllVariablesDistinguished;
	}

	public JFileChooser getFcRuleDir() {
		return fcRuleDir;
	}

	public JButton getBtnOpenDir() {
		return btnOpenDir;
	}

//	public JCheckBox getChbUseImportsClosure() {
//		return chbUseImportsClosure;
//	}

	public JLabel getLblStatus() {
		return lblStatus;
	}
	
	public JLabel getLblCurrentDir() {
		return lblCurrentDir;
	}
	
	public int showModifiedDialog() {
		return JOptionPane
		.showOptionDialog(
				this,
				"Some rules are modified. Do you want to revert them and proceed ? ",
				"Rules modified warning",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null, YES_NO_OPTIONS,
				YES_NO_OPTIONS[1]);		
	}
	
	public int showConfirmDeleteDialog() {
		return JOptionPane
				.showOptionDialog(this,
						"The rule will be deleted also from the file system. Proceed ? ",
						"Delete rule warning",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null,
						YES_NO_OPTIONS, YES_NO_OPTIONS[1]);		
	}
	
	public String showCreateNewDialog() {
		return (String) JOptionPane.showInputDialog(
				this, "Rule Name:\n",
				"Create a new SPARQL-DL NOT rule",
				JOptionPane.PLAIN_MESSAGE, null, null, null);		
	}
}
