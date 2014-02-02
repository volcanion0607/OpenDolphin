/*
 * ImageHistoryPanel.java
 * Copyright (C) 2003 Digital Globe, Inc. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *	
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *	
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.client;

import java.beans.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;

import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.dto.ImageSearchSpec;
import open.dolphin.infomodel.SchemaModel;
import jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.*;

/**
 * ImageHistoryPanel
 * 
 * @author Kazushi Minagawa
 */
public class ImageHistoryPanel extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = -3387106645066887145L;

	private String pid;

	private CareMapDocument myParent;

	private ImageTableModel tModel;

	private JTable table;

	private int columnCount = 5;

	private int imageWidth = 132;

	private int imageHeight = 132;

	private javax.swing.Timer taskTimer;

	public ImageHistoryPanel() {
		
		super(new BorderLayout());

		tModel = new ImageTableModel();
		table = new JTable(tModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setCellSelectionEnabled(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		TableColumn column = null;
		for (int i = 0; i < columnCount; i++) {
			column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth(imageWidth);
		}
		table.setRowHeight(imageHeight + 20);

		ImageRenderer imageRenderer = new ImageRenderer();
		imageRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		table.setDefaultRenderer(java.lang.Object.class, imageRenderer);

		table.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() != 2) {
					return;
				}
				Point loc = e.getPoint();
				int row = table.rowAtPoint(loc);
				int col = table.columnAtPoint(loc);
				if (row != -1 && col != -1) {
					openImage(row, col);
				}
			}
		});

		JScrollPane scroller = new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(scroller, BorderLayout.CENTER);
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String val) {
		pid = val;
	}

	public void setMyParent(CareMapDocument doc) {
		myParent = doc;
	}

	@SuppressWarnings("unchecked")
	public void setImageList(List allImages) {

		if (allImages != null) {

			int size = allImages.size();
			List list = new ArrayList();

			for (int i = 0; i < size; i++) {
				ArrayList l = (ArrayList) allImages.get(i);
				if (l != null) {
					for (int j = 0; j < l.size(); j++) {
						list.add(l.get(j));
					}
				}
			}
			tModel.setImageList(list);
		} else {
			tModel.setImageList(allImages);
		}
	}

	public void propertyChange(PropertyChangeEvent e) {

		String prop = e.getPropertyName();

		if (prop.equals(CareMapDocument.SELECTED_DATE_PROP)) {

			String date = (String) e.getNewValue();
			// if (isMyCode()) {
			// System.out.println("my propertyChange: " + date);
			findDate(date);
			// }
		}

	}

	private void findDate(String date) {
		int index = tModel.findDate(date);
		if (index != -1) {
			int row = index / columnCount;
			int col = index % columnCount;
			table.setRowSelectionInterval(row, row);
			table.setColumnSelectionInterval(col, col);
		}
	}

	/*private boolean isMyCode() {
		// return markEvent.equals(imageCode) ? true : false;
		return markEvent.equals("image") ? true : false;
	}*/

	private void openImage(int row, int col) {

		ImageEntry entry = (ImageEntry) tModel.getValueAt(row, col);
		ImageSearchSpec spec = new ImageSearchSpec();
		spec.setCode(ImageSearchSpec.ID_SEARCH);
		spec.setId(entry.getId());

		int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
		int delay = ClientContext.getInt("task.default.delay");
		final DocumentDelegater ddl = new DocumentDelegater();
		final ImageTask worker = new ImageTask(spec, ddl, maxEstimation/delay);
		final IStatusPanel statusPanel = ((ChartPlugin) myParent.getContext()).getStatusPanel();
		taskTimer = new javax.swing.Timer(delay, new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				worker.getCurrent();
				statusPanel.setMessage(worker.getMessage());

				if (worker.isDone()) {
					statusPanel.stop();
					taskTimer.stop();
					if (ddl.isNoError()) {
						openDialog(worker.getImage());
					} else {
						JFrame parentFrame = myParent.getContext().getFrame();
						String title = ClientContext.getString("careMap.title");
						JOptionPane.showMessageDialog(
								parentFrame,
								ddl.getErrorMessage(),
								ClientContext.getFrameTitle(title),
								JOptionPane.WARNING_MESSAGE);
					}
					
				} else if (worker.isTimeOver()) {
					statusPanel.stop();
					taskTimer.stop();
					JFrame parentFrame = myParent.getContext().getFrame();
					String title = ClientContext.getString("careMap.title");
					new TimeoutWarning(parentFrame, title, null).start();
				}
			}
		});
		worker.start();
		statusPanel.start("");
		taskTimer.start();
	}

	private void openDialog(SchemaModel schema) {

		if (schema == null) {
			return;
		}

		try {
			final SchemaEditorDialog dlg = new SchemaEditorDialog((Frame) null,
					true, schema, false);
			// dlg.addPropertyChangeListener(SchemaHolder.this);
			SwingUtilities.invokeLater(new Runnable() {

				public void run() {
					dlg.run();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}

	protected class ImageTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -2683619747572366737L;
		
		private List imageList;

		public int getColumnCount() {
			return columnCount;
		}

		public int getRowCount() {
			if (imageList == null) {
				return 0;
			}

			int size = imageList.size();
			int rowCount = size / columnCount;

			return ((size % columnCount) != 0) ? rowCount + 1 : rowCount;
		}

		public Object getValueAt(int row, int col) {
			int index = row * columnCount + col;
			if (!isValidIndex(index)) {
				return null;
			}

			ImageEntry s = (ImageEntry) imageList.get(index);
			return (Object) s;
		}

		public void setImageList(List list) {
			if (imageList != null) {
				int last = getRowCount();
				imageList.clear();
				fireTableRowsDeleted(0, last);
			}
			imageList = list;
			int last = getRowCount();
			fireTableRowsInserted(0, last);
		}

		private int findDate(String date) {

			int ret = -1;

			if (imageList == null) {
				return ret;
			}

			int size = imageList.size();
			for (int i = 0; i < size; i++) {
				ImageEntry entry = (ImageEntry) imageList.get(i);
				if (entry.getConfirmDate().startsWith(date)) {
					ret = i;
					break;
				}
			}
			return ret;
		}

		private boolean isValidIndex(int index) {
			return (imageList == null || index < 0 || index >= imageList.size()) ? false
					: true;
		}
	}

	protected class ImageRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -8136363583689791913L;

		public ImageRenderer() {
			setVerticalTextPosition(JLabel.BOTTOM);
			setHorizontalTextPosition(JLabel.CENTER);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean isFocused, int row,
				int col) {
			Component compo = super.getTableCellRendererComponent(table, value,
					isSelected, isFocused, row, col);
			JLabel l = (JLabel) compo;

			if (value != null) {

				ImageEntry entry = (ImageEntry) value;
				l.setIcon(entry.getImageIcon());
				// String title = entry.getTitle();
				// if (title != null) {
				// l.setText(title);

				// } else {
				l.setText(entry.getConfirmDate().substring(0, 10));
				// }

			} else {
				l.setIcon(null);
			}
			return compo;
		}
	}
}