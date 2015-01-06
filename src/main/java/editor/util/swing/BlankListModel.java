package editor.util.swing;

public class BlankListModel<T> extends AbstractListModel<T> {
	public BlankListModel() {
	}

	public int getSize() {
		return 0;
	}

	public T getElementAt(int index) {
		return null;
	}
}
