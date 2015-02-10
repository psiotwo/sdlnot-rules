package cz.sio2.sdlnot.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * @author Petr Křemen
 */
public class ResultSetTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -8114688446930065744L;
		
	private List<QuerySolution> querySolutions = new ArrayList<>();
	private List<String> queryVariables = new ArrayList<>();	
	
	public ResultSetTableModel(final List<String> queryVariables, final List<QuerySolution> querySolutions) {
		this.queryVariables = queryVariables;
		this.querySolutions = querySolutions;
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
	    return false; 
	}
	
	@Override
	public int getRowCount() {
		return querySolutions.size();
	}

	@Override
	public int getColumnCount() {
		return queryVariables.size();
	}
	
	@Override
	public Class<?> getColumnClass(int column) {
		return String.class;
	}

	@Override
	public String getColumnName(int column) {
		return queryVariables.get(column);
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		final RDFNode r = querySolutions.get(rowIndex).get(queryVariables.get(columnIndex));
		
		if ( r == null ) {
			return null;
		}
		if ( r.isResource() ) {
			return r.asResource().getLocalName();
		} else if ( r.isLiteral() ) {
			return r.asLiteral().getLexicalForm();
		} else {
			return r;
		}
	}
	@Override
	public void setValueAt(Object o, int rowIndex, int columnIndex) {
		throw new IllegalArgumentException();
	}
	
	public void clear() {
		querySolutions.clear();
		queryVariables.clear();
	}
}
