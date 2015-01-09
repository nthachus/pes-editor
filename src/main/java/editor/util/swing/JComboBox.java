package editor.util.swing;

import java.util.Vector;

@SuppressWarnings("unchecked")
public class JComboBox<T> extends javax.swing.JComboBox {
	public JComboBox(JComboBoxModel<T> model) {
		super(model);
	}

	public JComboBox(T[] items) {
		super(new DefaultComboBoxModel<T>(items));
	}

	public JComboBox(Vector<T> items) {
		super(new DefaultComboBoxModel<T>(items));
	}

	public JComboBox() {
		super(new DefaultComboBoxModel<T>());
	}

	@Override
	public JComboBoxModel<T> getModel() {
		return (JComboBoxModel<T>) super.getModel();
	}

	@Override
	public void setModel(javax.swing.ComboBoxModel model) {
		if (null == model) throw new NullPointerException("model");
		if (!(model instanceof JComboBoxModel<?>)) throw new IllegalArgumentException("model");
		super.setModel(model);
	}

	@Override
	public T getSelectedItem() {
		return (T) super.getSelectedItem();
	}

	@Override
	public T getItemAt(int index) {
		return (T) super.getItemAt(index);
	}

	@Override
	public void addItem(Object anObject) {
		super.addItem(anObject);
	}
}
