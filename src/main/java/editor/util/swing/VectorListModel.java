package editor.util.swing;

import java.util.Vector;

public class VectorListModel<T> extends javax.swing.AbstractListModel implements JListModel<T> {
	private final Vector<T> listData;

	public VectorListModel(Vector<T> listData) {
		if (null == listData) throw new NullPointerException("listData");
		this.listData = listData;
	}

	/**
	 * Returns the length of the list.
	 */
	public int getSize() {
		return listData.size();
	}

	public T getElementAt(int index) {
		return listData.elementAt(index);
	}
}
