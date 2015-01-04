package editor.util.swing;

import java.util.Vector;

@SuppressWarnings("unchecked")
public class JList<T> extends javax.swing.JList {
	public JList(JListModel<T> dataModel) {
		super(dataModel);
	}

	public JList(final Object[] listData) {
		super(new AbstractListModel<T>() {
			public int getSize() {
				return listData.length;
			}

			public T getElementAt(int index) {
				return (T) listData[index];
			}
		});
	}

	public JList(final Vector<T> listData) {
		super(new AbstractListModel<T>() {
			public int getSize() {
				return listData.size();
			}

			public T getElementAt(int index) {
				return listData.elementAt(index);
			}
		});
	}

	public JList() {
		super(new AbstractListModel<T>() {
			public int getSize() {
				return 0;
			}

			public T getElementAt(int index) {
				return null;
			}
		});
	}

	@Override
	public JListModel<T> getModel() {
		return (JListModel<T>) super.getModel();
	}

	@Override
	public void setModel(javax.swing.ListModel model) {
		if (null != model && !(model instanceof JListModel<?>))
			throw new IllegalArgumentException("model");
		super.setModel(model);
	}

	@Override
	public void setListData(final Object[] listData) {
		setModel(new AbstractListModel<T>() {
			public int getSize() {
				return listData.length;
			}

			public T getElementAt(int index) {
				return (T) listData[index];
			}
		});
	}

	@Override
	public void setListData(final Vector<?> listData) {
		setModel(new AbstractListModel<T>() {
			public int getSize() {
				return listData.size();
			}

			public T getElementAt(int index) {
				return (T) listData.elementAt(index);
			}
		});
	}

	@Override
	public T getSelectedValue() {
		return (T) super.getSelectedValue();
	}
}
