package editor.ui;

import editor.data.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.Serializable;

public class PlayerTransferable implements Serializable, Transferable {
	private static final long serialVersionUID = 209565230974819915L;
	private static final Logger log = LoggerFactory.getLogger(Player.class);

	private final Player data;

	public PlayerTransferable(Player player) {
		data = player;
	}

	private static volatile DataFlavor dataFlavor = null;

	public static DataFlavor getDataFlavor() {
		if (null == dataFlavor) {
			try {
				dataFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Player.class.getName());
			} catch (ClassNotFoundException e) {
				log.warn("Unable to create data flavor: {}", e.toString());
			}
		}
		return dataFlavor;
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		if (!isDataFlavorSupported(flavor)) throw new UnsupportedFlavorException(flavor);
		return data;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{getDataFlavor()};
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return getDataFlavor().equals(flavor);
	}
}
