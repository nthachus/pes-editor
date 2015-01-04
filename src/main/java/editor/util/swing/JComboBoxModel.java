package editor.util.swing;

public interface JComboBoxModel<T> extends javax.swing.ComboBoxModel {
	T getElementAt(int index);

	T getSelectedItem();
}
