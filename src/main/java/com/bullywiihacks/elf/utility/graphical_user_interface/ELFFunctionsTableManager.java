package com.bullywiihacks.elf.utility.graphical_user_interface;

import com.bullywiihacks.elf.utility.elf.ELFFunction;
import com.bullywiihacks.elf.utility.graphical_user_interface.utilities.JTableUtilities;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class ELFFunctionsTableManager
{
	private JTable table;
	private DefaultTableModel tableModel;
	private List<ELFFunction> elfFunctions;

	public ELFFunctionsTableManager(JTable table)
	{
		tableModel = JTableUtilities.getDefaultTableModel();
		JTableUtilities.setSingleSelection(table);
		table.setModel(tableModel);
		this.table = table;
		elfFunctions = new ArrayList<>();
	}

	public void configure()
	{
		String[] columnHeaderNames = new String[]{"Function Name", "File Offset", "Code Size"};
		JTableUtilities.configureTable(table, columnHeaderNames);
	}

	public void addRow(ELFFunction elfFunction)
	{
		tableModel.addRow(new Object[]{elfFunction.getName(),
				elfFunction.getOffset(),
				elfFunction.getSize()});
		elfFunctions.add(elfFunction);
	}

	public boolean areMultipleRowsSelected()
	{
		return table.getSelectedRows().length > 1;
	}

	public boolean isRowSelected()
	{
		return table.getSelectedRow() != -1;
	}

	public boolean rowExists()
	{
		return tableModel.getRowCount() > 0;
	}

	public ELFFunction getSelectedElement()
	{
		int selectedRow = table.getSelectedRow();
		return elfFunctions.get(selectedRow);
	}

	public JTable getTable()
	{
		return table;
	}

	public void removeAllRows()
	{
		JTableUtilities.deleteAllRows(table);
		elfFunctions.clear();
	}

	public boolean isTableEmpty()
	{
		return table.getRowCount() == 0;
	}

	public void selectFirstRow()
	{
		JTableUtilities.setSelectedRow(table, 0, 0);
	}

	public void setSelectedRow(int selectedRow)
	{
		JTableUtilities.setSelectedRow(table, selectedRow, selectedRow);
	}

	public int getSelectedIndex()
	{
		return table.getSelectedRow();
	}

	public int getRowCount()
	{
		return table.getRowCount();
	}
}