package editor.util.swing;

public class ArrayListModel<T> extends AbstractListModel<T> {
	private final Object[] listData;

	public ArrayListModel(Object[] listData) {
		if (null == listData) throw new NullPointerException("listData");
		this.listData = listData;
	}

	/**
	 * Returns the length of the list.
	 */
	public int getSize() {
		return listData.length;
	}

	@SuppressWarnings("unchecked")
	public T getElementAt(int index) {
		return (T) listData[index];
	}
}
