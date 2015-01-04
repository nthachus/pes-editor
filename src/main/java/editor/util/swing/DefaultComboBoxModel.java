package editor.util.swing;

import java.util.Vector;

@SuppressWarnings("unchecked")
public class DefaultComboBoxModel<T> extends javax.swing.DefaultComboBoxModel implements JComboBoxModel<T> {
	public DefaultComboBoxModel() {
	}

	public DefaultComboBoxModel(Object[] items) {
		super(items);
	}

	public DefaultComboBoxModel(Vector<T> v) {
		super(v);
	}

	@Override
	public T getSelectedItem() {
		return (T) super.getSelectedItem();
	}

	@Override
	public T getElementAt(int index) {
		return (T) super.getElementAt(index);
	}
}
