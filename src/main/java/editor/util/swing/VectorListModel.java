package editor.util.swing;

import java.util.Vector;

public class VectorListModel<T> extends AbstractListModel<T> {
	private final Vector<?> listData;

	public VectorListModel(Vector<?> listData) {
		if (null == listData) throw new NullPointerException("listData");
		this.listData = listData;
	}

	/**
	 * Returns the length of the list.
	 */
	public int getSize() {
		return listData.size();
	}

	@SuppressWarnings("unchecked")
	public T getElementAt(int index) {
		return (T) listData.elementAt(index);
	}
}
