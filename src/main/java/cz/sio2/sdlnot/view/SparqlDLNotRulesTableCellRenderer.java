package cz.sio2.sdlnot.view;

import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import cz.sio2.sdlnot.model.Rule;

/**
 * @author Petr Křemen
 */
public class SparqlDLNotRulesTableCellRenderer extends DefaultTableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);
		
		if ( table.getModel() instanceof SparqlDLNotTableModel) {
			final SparqlDLNotTableModel model = (SparqlDLNotTableModel) table.getModel();
			final Rule rule = model.getRuleAt(row);
			if ((c instanceof JLabel)) {
				final JLabel l = (JLabel) c;
				final Map<TextAttribute,Object> map = new HashMap<TextAttribute, Object>();
				if (model.isDeleted(rule)) {
					map.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
					l.setFont(l.getFont().deriveFont(Font.BOLD).deriveFont(map));
				} else if (model.isDirty(rule)) {
					l.setFont(l.getFont().deriveFont(Font.BOLD));
				}
			}
		}
		return c;
	}
}