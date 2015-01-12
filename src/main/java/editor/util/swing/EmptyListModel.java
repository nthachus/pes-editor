package editor.util.swing;

public class EmptyListModel<T> extends javax.swing.AbstractListModel implements JListModel<T> {
	public EmptyListModel() {
	}

	public int getSize() {
		return 0;
	}

	public T getElementAt(int index) {
		return null;
	}
}
