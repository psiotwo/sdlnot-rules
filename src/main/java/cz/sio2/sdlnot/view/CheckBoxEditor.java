package cz.sio2.sdlnot.view;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;

/**
 * @author Petr Křemen
 */
public abstract class CheckBoxEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 1L;
	private JCheckBox checkBox;

	public CheckBoxEditor(JCheckBox checkBox) {
		super(checkBox);
		this.checkBox = checkBox;
		this.checkBox.addItemListener(new ItemListener() {			
			@Override
			public void itemStateChanged(ItemEvent e) {
				fireEditingStopped();
				fireStateChanged();
			}
		});
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		checkBox.setSelected((Boolean) value);
		return super.getTableCellEditorComponent(table, value, isSelected, row,
				column);
	}

	protected abstract void fireStateChanged();	
}