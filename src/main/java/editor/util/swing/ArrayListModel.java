package editor.util.swing;

public class ArrayListModel<T> extends javax.swing.AbstractListModel implements JListModel<T> {
	private final T[] listData;

	public ArrayListModel(T[] listData) {
		if (null == listData) throw new NullPointerException("listData");
		this.listData = listData;
	}

	/**
	 * Returns the length of the list.
	 */
	public int getSize() {
		return listData.length;
	}

	public T getElementAt(int index) {
		return (T) listData[index];
	}
}
