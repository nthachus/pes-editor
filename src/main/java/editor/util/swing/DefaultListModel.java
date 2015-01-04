package editor.util.swing;

@SuppressWarnings("unchecked")
public class DefaultListModel<T> extends javax.swing.DefaultListModel implements JListModel<T> {
	public DefaultListModel() {
	}

	@Override
	public T getElementAt(int index) {
		return (T) super.getElementAt(index);
	}

	@Override
	public T get(int index) {
		return (T) super.get(index);
	}
}
