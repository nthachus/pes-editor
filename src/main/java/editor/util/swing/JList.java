package editor.util.swing;

import java.util.Vector;

@SuppressWarnings("unchecked")
public class JList<T> extends javax.swing.JList {
	public JList(JListModel<T> dataModel) {
		super(dataModel);
	}

	public JList(T[] listData) {
		super(new ArrayListModel<T>(listData));
	}

	public JList(Vector<T> listData) {
		super(new VectorListModel<T>(listData));
	}

	public JList() {
		super(new BlankListModel<T>());
	}

	@Override
	public JListModel<T> getModel() {
		return (JListModel<T>) super.getModel();
	}

	@Override
	public void setModel(javax.swing.ListModel model) {
		if (null == model) throw new NullPointerException("model");
		if (!(model instanceof JListModel<?>)) throw new IllegalArgumentException("model");
		super.setModel(model);
	}

	@Override
	public void setListData(Object[] listData) {
		super.setModel(new ArrayListModel<T>((T[]) listData));
	}

	@Override
	public void setListData(Vector listData) {
		super.setModel(new VectorListModel<T>((Vector<T>) listData));
	}

	@Override
	public T getSelectedValue() {
		return (T) super.getSelectedValue();
	}
}
