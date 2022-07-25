package editor.util;

import editor.lang.NullArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * LZARI.C -- A Data Compression Program.
 * <p>
 * Use, distribute, and modify this program freely.<br/>
 * Please send me your improved versions.<br/>
 * PC-VAN		SCIENCE<br/>
 * NIFTY-Serve	PAF01022<br/>
 * CompuServe	74050,1022<br/>
 * </p>
 *
 * @author Haruhiko Okumura
 * @version 4/7/1989
 */
public final class LZAri {
	private static final Logger log = LoggerFactory.getLogger(LZAri.class);

	//********** Bit I/O **********//

	private volatile InputStream inFile;
	private volatile OutputStream outFile;

	private volatile long textSize = 0, printCount = 0;
	private volatile long codeSize = 0;
	private volatile int wBuffer = 0, wMask = 128;
	private volatile int rBuffer, rMask = 0;

	/**
	 * Output one bit (bit = 0,1).
	 */
	private synchronized void putBit(int bit) throws IOException {
		if (bit != 0) {
			wBuffer |= wMask;
		}
		if ((wMask >>>= 1) == 0) {
			// writes the specified byte to the output stream
			outFile.write(wBuffer);

			wBuffer = 0;
			wMask = 128;
			codeSize++;
		}
	}

	/**
	 * Send remaining bits.
	 */
	private void flushBitBuffer() throws IOException {
		for (int i = 0; i < 7; i++) {
			putBit(0);
		}
	}

	/**
	 * Get one bit (0 or 1).
	 */
	private synchronized int getBit() throws IOException {
		if ((rMask >>>= 1) == 0) {
			// reads the next byte of data from the input stream
			rBuffer = inFile.read();
			rMask = 128;
		}
		return ((rBuffer & rMask) != 0) ? 1 : 0;
	}

	//********** LZSS with multiple binary trees **********//

	/**
	 * Size of ring buffer.
	 */
	private static final int N = 4096;
	/**
	 * Upper limit for {@link #matchLength}.
	 */
	private static final int F = 60;
	/**
	 * Encode string into position and length if {@link #matchLength} is greater than this.
	 */
	private static final int THRESHOLD = 2;
	/**
	 * Index for root of binary search trees.
	 */
	private static final int NIL = N;

	/**
	 * Ring buffer of size {@link #N},
	 * with extra {@link #F}-1 bytes to facilitate string comparison of the longest match.
	 * These are set by the {@link #insertNode(int)} procedure.
	 */
	private final int[] textBuffer = new int[N + F - 1];
	private volatile int matchPosition, matchLength;
	/**
	 * Left & right children & parents -- These constitute binary search trees.
	 */
	private final int[] leftSon = new int[N + 1],
			rightSon = new int[N + 257],
			dad = new int[N + 1];

	/**
	 * Initialize trees.
	 * <p>
	 * For i = 0 to {@link #N} - 1, {@link #rightSon}[i] and {@link #leftSon}[i] will be the right and left children of node i.
	 * These nodes need not be initialized. Also, dad[i] is the parent of node i.
	 * These are initialized to {@link #NIL} (= {@link #N}), which stands for 'not used'.
	 * For i = 0 to 255, {@link #rightSon}[{@link #N} + i + 1] is the root of the tree for strings that begin with character i.
	 * These are initialized to {@link #NIL}.  Note there are 256 trees.
	 * </p>
	 */
	private void initTree() {
		for (int i = N + 1; i <= N + 256; i++) {
			rightSon[i] = NIL;  // root
		}
		for (int i = 0; i < N; i++) {
			dad[i] = NIL;   // node
		}
	}

	/**
	 * Inserts string of length {@link #F}, {@link #textBuffer}[r..r+F-1], into one of the
	 * trees ({@link #textBuffer}[r]'th tree) and returns the longest-match position
	 * and length via the global variables matchPosition and {@link #matchLength}.
	 * If {@link #matchLength} = {@link #F}, then removes the old node in favor of the new one,
	 * because the old one will be deleted sooner.
	 * Note <code>r</code> plays double role, as tree node and position in buffer.
	 */
	private void insertNode(int r) {
		int cmp = 1;
		int p = N + 1 + textBuffer[r];

		rightSon[r] = leftSon[r] = NIL;
		matchLength = 0;

		int i, temp;
		for (; ; ) {
			if (cmp >= 0) {
				if (rightSon[p] != NIL) {
					p = rightSon[p];
				} else {
					rightSon[p] = r;
					dad[r] = p;
					return;
				}
			} else {
				if (leftSon[p] != NIL) {
					p = leftSon[p];
				} else {
					leftSon[p] = r;
					dad[r] = p;
					return;
				}
			}

			for (i = 1; i < F; i++) {
				if ((cmp = textBuffer[r + i] - textBuffer[p + i]) != 0) {
					break;
				}
			}

			if (i > THRESHOLD) {
				if (i > matchLength) {
					matchPosition = (r - p) & (N - 1);
					if ((matchLength = i) >= F) {
						break;
					}
				} else if (i == matchLength) {
					temp = (r - p) & (N - 1);
					if (temp < matchPosition) {
						matchPosition = temp;
					}
				}
			}
		}

		dad[r] = dad[p];
		leftSon[r] = leftSon[p];
		rightSon[r] = rightSon[p];

		dad[leftSon[p]] = r;
		dad[rightSon[p]] = r;
		if (rightSon[dad[p]] == p) {
			rightSon[dad[p]] = r;
		} else {
			leftSon[dad[p]] = r;
		}

		dad[p] = NIL;   // remove p
	}

	/**
	 * Delete node <code>p</code> from tree.
	 */
	private void deleteNode(int p) {
		if (dad[p] == NIL) {
			return; // not in tree
		}

		int q;
		if (rightSon[p] == NIL) {
			q = leftSon[p];
		} else if (leftSon[p] == NIL) {
			q = rightSon[p];
		} else {
			q = leftSon[p];
			if (rightSon[q] != NIL) {
				do {
					q = rightSon[q];
				} while (rightSon[q] != NIL);

				rightSon[dad[q]] = leftSon[q];
				dad[leftSon[q]] = dad[q];

				leftSon[q] = leftSon[p];
				dad[leftSon[p]] = q;
			}

			rightSon[q] = rightSon[p];
			dad[rightSon[p]] = q;
		}

		dad[q] = dad[p];
		if (rightSon[dad[p]] == p) {
			rightSon[dad[p]] = q;
		} else {
			leftSon[dad[p]] = q;
		}

		dad[p] = NIL;
	}

	//********** Arithmetic Compression **********//

	/* If you are not familiar with arithmetic compression, you should read
		I. E. Witten, R. M. Neal, and J. G. Cleary,
			Communications of the ACM, Vol. 30, pp. 520-540 (1987),
		from which much have been borrowed.
	*/

	private static final int M = 15;

	/**
	 * Q1 (= 2 to the {@link #M}) must be sufficiently large,
	 * but not so large as the unsigned long Q1 * 4 * (Q1 - 1) overflows.
	 */
	private static final long Q1 = (1L << M);
	private static final long Q2 = (Q1 * 2);
	private static final long Q3 = (Q1 * 3);
	private static final long Q4 = (Q1 * 4);
	private static final long MAX_CUM = (Q1 - 1);

	/**
	 * Character code = 0, 1, ..., N_CHAR - 1
	 */
	private static final int N_CHAR = (256 - THRESHOLD + F);

	private volatile long low = 0, high = Q4, value = 0;
	/**
	 * Counts for magnifying low and high around {@link #Q2}
	 */
	private volatile int shifts = 0;
	private final int[] charToSym = new int[N_CHAR],
			symToChar = new int[N_CHAR + 1];

	/**
	 * Frequency for symbols
	 */
	private final long[] symFreq = new long[N_CHAR + 1];
	/**
	 * Cumulative freq for symbols
	 */
	private final long[] symCum = new long[N_CHAR + 1];
	/**
	 * Cumulative freq for positions
	 */
	private final long[] positionCum = new long[N + 1];

	/**
	 * Initialize model.
	 */
	private void startModel() {
		symCum[N_CHAR] = 0;

		int ch;
		for (int sym = N_CHAR; sym >= 1; sym--) {
			ch = sym - 1;

			charToSym[ch] = sym;
			symToChar[sym] = ch;

			symFreq[sym] = 1;
			symCum[sym - 1] = symCum[sym] + symFreq[sym];
		}

		symFreq[0] = 0;  // sentinel (!= symFreq[1])
		positionCum[N] = 0;

		for (int i = N; i >= 1; i--) {
			positionCum[i - 1] = positionCum[i] + 10000 / (i + 200);
			// empirical distribution function (quite tentative)
			// NOTE: Please devise a better mechanism!
		}
	}

	private void updateModel(int sym) {
		if (symCum[0] >= MAX_CUM) {
			int c = 0;
			for (int i = N_CHAR; i > 0; i--) {
				symCum[i] = c;
				c += (symFreq[i] = (symFreq[i] + 1) >> 1);
			}
			symCum[0] = c;
		}

		int i = sym;
		while (symFreq[i] == symFreq[i - 1]) {
			i--;
		}

		if (i < sym) {
			int chIdx = symToChar[i];
			int chSym = symToChar[sym];

			symToChar[i] = chSym;
			symToChar[sym] = chIdx;

			charToSym[chIdx] = sym;
			charToSym[chSym] = i;
		}

		symFreq[i]++;
		while (--i >= 0) {
			symCum[i]++;
		}
	}

	/**
	 * Output 1 bit, followed by its complements.
	 */
	private synchronized void output(int bit) throws IOException {
		putBit(bit);
		for (; shifts > 0; shifts--) {
			putBit((bit == 0) ? 1 : 0);
		}
	}

	private void encodeChar(int ch) throws IOException {
		int sym = charToSym[ch];
		long range = high - low;// uint

		synchronized (this) {
			high = low + (range * symCum[sym - 1]) / symCum[0];
			low += (range * symCum[sym]) / symCum[0];
		}

		for (; ; ) {
			if (high <= Q2) {
				output(0);
			} else if (low >= Q2) {
				output(1);

				synchronized (this) {
					low -= Q2;
					high -= Q2;
				}
			} else if (low >= Q1 && high <= Q3) {
				synchronized (log) {
					shifts++;
				}
				synchronized (this) {
					low -= Q1;
					high -= Q1;
				}
			} else {
				break;
			}

			synchronized (this) {
				low *= 2;
				high *= 2;
			}
		}

		updateModel(sym);
	}

	private void encodePosition(int position) throws IOException {
		long range = high - low;// uint

		synchronized (this) {
			high = low + (range * positionCum[position]) / positionCum[0];
			low += (range * positionCum[position + 1]) / positionCum[0];
		}

		for (; ; ) {
			if (high <= Q2) {
				output(0);
			} else if (low >= Q2) {
				output(1);

				synchronized (this) {
					low -= Q2;
					high -= Q2;
				}
			} else if (low >= Q1 && high <= Q3) {
				synchronized (log) {
					shifts++;
				}
				synchronized (this) {
					low -= Q1;
					high -= Q1;
				}
			} else {
				break;
			}

			synchronized (this) {
				low *= 2;
				high *= 2;
			}
		}
	}

	private void encodeEnd() throws IOException {
		synchronized (log) {
			shifts++;
		}

		output((low < Q1) ? 0 : 1);

		flushBitBuffer();   // flush bits remaining in buffer
	}

	/**
	 * 1: if x >= {@link #symCum}[1],<br/>
	 * {@link #N_CHAR}: if {@link #symCum}[N_CHAR] > x,<br/>
	 * i: such that {@link #symCum}[i - 1] > x >= {@link #symCum}[i]: otherwise
	 */
	private int binarySearchSym(long x) {// uint
		int i = 1;
		int j = N_CHAR;

		int k;
		while (i < j) {
			k = (i + j) >>> 1;// average / 2
			if (symCum[k] > x) {
				i = k + 1;
			} else {
				j = k;
			}
		}

		return i;
	}

	/**
	 * 0: if x >= {@link #positionCum}[1],<br/>
	 * {@link #N} - 1: if {@link #positionCum}[N] > x,<br/>
	 * i: such that {@link #positionCum}[i] > x >= {@link #positionCum}[i + 1]: otherwise
	 */
	private int binarySearchPos(long x) {// uint
		int i = 1;
		int j = N;

		int k;
		while (i < j) {
			k = (i + j) >>> 1;// average / 2
			if (positionCum[k] > x) {
				i = k + 1;
			} else {
				j = k;
			}
		}

		return i - 1;
	}

	private void startDecode() throws IOException {
		for (int b, i = 0; i < M + 2; i++) {
			b = getBit();
			synchronized (this) {
				value = value * 2 + b;
			}
		}
	}

	private int decodeChar() throws IOException {
		long range = high - low;// uint
		int sym = binarySearchSym(((value - low + 1) * symCum[0] - 1) / range);

		synchronized (this) {
			high = low + (range * symCum[sym - 1]) / symCum[0];
			low += (range * symCum[sym]) / symCum[0];
		}

		for (int b; ; ) {
			if (low >= Q2) {
				synchronized (this) {
					value -= Q2;
					low -= Q2;
					high -= Q2;
				}
			} else if (low >= Q1 && high <= Q3) {
				synchronized (this) {
					value -= Q1;
					low -= Q1;
					high -= Q1;
				}
			} else if (high > Q2) {
				break;
			}

			b = getBit();
			synchronized (this) {
				low *= 2;
				high *= 2;
				value = value * 2 + b;
			}
		}

		int ch = symToChar[sym];
		updateModel(sym);

		return ch;
	}

	private int decodePosition() throws IOException {
		long range = high - low;// uint
		int position = binarySearchPos(((value - low + 1) * positionCum[0] - 1) / range);

		synchronized (this) {
			high = low + (range * positionCum[position]) / positionCum[0];
			low += (range * positionCum[position + 1]) / positionCum[0];
		}

		for (int b; ; ) {
			if (low >= Q2) {
				synchronized (this) {
					value -= Q2;
					low -= Q2;
					high -= Q2;
				}
			} else if (low >= Q1 && high <= Q3) {
				synchronized (this) {
					value -= Q1;
					low -= Q1;
					high -= Q1;
				}
			} else if (high > Q2) {
				break;
			}

			b = getBit();
			synchronized (this) {
				low *= 2;
				high *= 2;
				value = value * 2 + b;
			}
		}

		return position;
	}

	//********** Encode and Decode **********//

	public void encode() throws IOException {
		synchronized (log) {
			textSize = inFile.available();
			// output size of text
			outFile.write(Bits.toBytes((int) textSize));
			//
			codeSize += 4;
			if (textSize == 0) {
				return;
			}
		}

		textSize = 0;
		startModel();
		initTree();

		int s = 0;
		int r = N - F;
		for (int i = s; i < r; i++) {
			textBuffer[i] = ' ';
		}

		int c, len;
		for (len = 0; len < F && (c = inFile.read()) != -1/*EOF*/; len++) {
			textBuffer[r + len] = c;
		}

		textSize = len;
		for (int i = 1; i <= F; i++) {
			insertNode(r - i);
		}
		insertNode(r);

		int i, lastMatchLength;
		do {
			if (matchLength > len) {
				matchLength = len;
			}
			if (matchLength <= THRESHOLD) {
				matchLength = 1;
				encodeChar(textBuffer[r]);
			} else {
				encodeChar(255 - THRESHOLD + matchLength);
				encodePosition(matchPosition - 1);
			}

			lastMatchLength = matchLength;
			for (i = 0; i < lastMatchLength && (c = inFile.read()) != -1/*EOF*/; i++) {
				deleteNode(s);

				textBuffer[s] = c;
				if (s < F - 1) {
					textBuffer[s + N] = c;
				}
				s = (s + 1) & (N - 1);
				r = (r + 1) & (N - 1);

				insertNode(r);
			}

			synchronized (this) {
				textSize += i;
			}
			if (textSize > printCount) {
				//System.out.print("%12ld\r", textSize);
				synchronized (this) {
					printCount += 1024;
				}
			}

			while (i++ < lastMatchLength) {
				deleteNode(s);
				s = (s + 1) & (N - 1);
				r = (r + 1) & (N - 1);

				if (--len != 0) {
					insertNode(r);
				}
			}
		} while (len > 0);

		encodeEnd();

		// DEBUG
		log.info("In: {} bytes, Out: {} bytes, Out/In: {}%", textSize, codeSize, (double) codeSize / textSize);
	}

	public void decode() throws IOException {
		// read size of text
		byte[] buffer = new byte[4];
		if (inFile.read(buffer) < 0) {
			throw new EOFException("Failed to read TextSize.");
		}
		textSize = Bits.toInt(buffer, 0);
		if (textSize == 0) {
			return;
		}

		startDecode();
		startModel();

		for (int i = 0; i < N - F; i++) {
			textBuffer[i] = ' ';
		}
		int r = N - F;

		long count;// uint
		for (count = 0; count < textSize; ) {
			int c = decodeChar();

			if (c < 256) {
				outFile.write(c);

				textBuffer[r++] = c;
				r &= (N - 1);
				count++;
			} else {
				int i = (r - decodePosition() - 1) & (N - 1);
				int j = c - 255 + THRESHOLD;

				for (int k = 0; k < j; k++) {
					c = textBuffer[(i + k) & (N - 1)];
					outFile.write(c);

					textBuffer[r++] = c;
					r &= (N - 1);
					count++;
				}
			}

			if (count > printCount) {
				//System.out.print("%12lu\r", count);
				synchronized (this) {
					printCount += 1024;
				}
			}
		}

		// DEBUG
		log.info("Decoded count: {}", count);
	}

	public void setInput(InputStream file) {
		if (null == file) {
			throw new NullArgumentException("file");
		}
		inFile = file;
	}

	public void setOutput(OutputStream file) {
		if (null == file) {
			throw new NullArgumentException("file");
		}
		outFile = file;
	}

	public byte[] encode(byte[] data, int offset, int length) throws IOException {
		if (null == data) {
			throw new NullArgumentException("data");
		}
		if (offset < 0) {
			throw new NullArgumentException("offset");
		}
		if (length < 0 || data.length < offset + length) {
			throw new IndexOutOfBoundsException(offset + " : " + length);
		}

		if (length == 0) {
			return new byte[0];
		}

		ByteArrayInputStream sr = null;
		ByteArrayOutputStream sw = null;
		try {
			setInput(sr = new ByteArrayInputStream(data, offset, length));
			setOutput(sw = new ByteArrayOutputStream());
			encode();
			return sw.toByteArray();
		} finally {
			if (null != sr) {
				sr.close();
			}
			if (null != sw) {
				sw.close();
			}
		}
	}

	public byte[] decode(byte[] data, int offset, int length) throws IOException {
		if (null == data) {
			throw new NullArgumentException("data");
		}
		if (offset < 0) {
			throw new NullArgumentException("offset");
		}
		if (length < 0 || data.length < offset + length) {
			throw new IndexOutOfBoundsException(offset + " : " + length);
		}

		if (length == 0) {
			return new byte[0];
		}

		ByteArrayInputStream sr = null;
		ByteArrayOutputStream sw = null;
		try {
			setInput(sr = new ByteArrayInputStream(data, offset, length));
			setOutput(sw = new ByteArrayOutputStream());
			decode();
			return sw.toByteArray();
		} finally {
			if (null != sr) {
				sr.close();
			}
			if (null != sw) {
				sw.close();
			}
		}
	}

}
