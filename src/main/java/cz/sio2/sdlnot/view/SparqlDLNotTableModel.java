package cz.sio2.sdlnot.view;

import java.util.HashSet;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import cz.sio2.sdlnot.model.Rule;
import cz.sio2.sdlnot.model.RuleSpec;
import cz.sio2.sdlnot.model.RuleSpecListener;

/**
 * @author Petr Křemen
 */
public class SparqlDLNotTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -8114688446930065744L;
	
	private RuleSpec ruleSpec;
	
	private Set<Rule> dirty = new HashSet<Rule>();

	private Set<Rule> deleted = new HashSet<Rule>();

	public SparqlDLNotTableModel(final RuleSpec ruleSpec) {
		this.ruleSpec = ruleSpec;
		ruleSpec.addListener(new RuleSpecListener() {

			@Override
			public void notifyRuleAdded(Rule rule) {
				fireTableDataChanged();
			};

			@Override
			public void notifyRuleRemoved(Rule rule) {
				fireTableDataChanged();
			}

			@Override
			public void notifyRuleMovedUp(int index, Rule rule) {
				fireTableRowsUpdated(index-1,index);				
			}			
		});
	}	

	public Rule getRuleAt(int index) {
		return ruleSpec.getRuleAtIndex(index);
	}
	
	public Boolean isDirty(Rule rule) {
		return dirty.contains(rule);
	}
	
	public Boolean isDeleted(Rule rule) {
		return deleted.contains(rule);
	}
	
	public void setDirty(Rule rule, Boolean d) {
		if (( d == null || !d)) {
			dirty.remove(rule);
		} else {
			dirty.add(rule);
		}
		fireTableCellUpdated(ruleSpec.indexOf(rule.getName()),1);
	}
	
	public void setDeleted(Rule rule, Boolean d) {
		if (( d == null || !d)) {
			deleted.remove(rule);
		} else {
			deleted.add(rule);
		}
		fireTableCellUpdated(ruleSpec.indexOf(rule.getName()),1);
	}
	
	public int getDirtyCount() {
		return dirty.size();
	}
	public int getDeletedCount() {
		return deleted.size();
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
	    return (col == 0); 
	}
	
	@Override
	public int getRowCount() {
		if ( ruleSpec == null ) {
			return 0;
		} else {
			return ruleSpec.getRuleList().size();
		}
	}

	@Override
	public int getColumnCount() {
		return 2;
	}
	
	@Override
	public Class<?> getColumnClass(int column) {
		switch (column) {
			case 0: return Boolean.class; 
			case 1: return Integer.class; 
		}
		throw new IllegalArgumentException();
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
			case 0: return ""; 
			case 1: return "rule"; 
		}
		throw new IllegalArgumentException();
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		final Rule rule = ruleSpec.getRuleAtIndex(rowIndex);
		
		switch (columnIndex) {
			case 0: return rule.isActive(); 
			case 1: return rule.getName(); 
		}
		throw new IllegalArgumentException();
	}
	@Override
	public void setValueAt(Object o, int rowIndex, int columnIndex) {
		if (columnIndex != 0) {
			throw new IllegalArgumentException();
		}
		final Rule rule = ruleSpec.getRuleAtIndex(rowIndex);
		
		rule.setActive((Boolean) o);
	}
	
	public void clear() {
		dirty.clear();
		deleted.clear();
	}
}
