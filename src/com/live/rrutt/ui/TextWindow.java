package com.live.rrutt.ui;

/**
 * @author Rick Rutt
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TextWindow extends javax.swing.JFrame {

	private static final long serialVersionUID = -1212370239230155880L;
	
	public static final int windowX = 100;
	public static final int windowY = 25;
	public static final int windowWidth = 450;
	public static final int windowHeight = 330;

	private int cursorRow = 0;
	private int cursorCol = 0;

	public TextWindow() {
		initComponents();
		pack();
		setSize(windowWidth, windowHeight);
	}
	
	public void setDefaultBounds() {
		setBounds(
		  windowX, 
		  windowY, 
		  windowWidth,    
		  windowHeight);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {
		textScrollPane = new javax.swing.JScrollPane();
		textArea = new javax.swing.JTextArea();
		getContentPane().setLayout(new java.awt.GridBagLayout());
		java.awt.GridBagConstraints gridBagConstraints1;
		setTitle("Text Window");

		textScrollPane.setPreferredSize(new java.awt.Dimension(150, 200));
		textScrollPane.setFont(new java.awt.Font("Courier New", 0, 12));
		textScrollPane.setAutoscrolls(false);

		textArea.setColumns(40);
		textArea.setRows(16);
		textArea.setFont(new java.awt.Font("Courier New", java.awt.Font.BOLD,
				16));
		textScrollPane.setViewportView(textArea);

		gridBagConstraints1 = new java.awt.GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints1.weightx = 100.0;
		gridBagConstraints1.weighty = 95.0;
		getContentPane().add(textScrollPane, gridBagConstraints1);

	}

	public void clear() {
		textArea.setText("");
		setCursorRowCol(0, 0);
	}

	public void setCursorRowCol(int row, int col) {
		this.cursorRow = row;
		this.cursorCol = col;
	}

	public void newLine() {
		this.cursorRow++;
		this.cursorCol = 0;
	}

	public void writeText(String text) {
		String s = text;
		int sLength = s.length();

		int lineCount = textArea.getLineCount() - 1; // Allow for empty
														// pending entry line

		while (this.cursorRow >= lineCount) {
			textArea.append("\n");
			lineCount++;
		}

		int lineStart = 0;
		int lineEnd = 0;
		try {
			lineStart = textArea.getLineStartOffset(this.cursorRow);
			lineEnd = textArea.getLineEndOffset(this.cursorRow);
		} catch (javax.swing.text.BadLocationException e) {
			lineStart = 0;
			lineEnd = 0;
		}

		if (lineEnd > lineStart) {
			lineEnd--; // Move in front of line-feed
		}

		int lineLength = lineEnd - lineStart;
		int endCol = this.cursorCol + sLength;

		while (endCol >= lineLength) {
			textArea.insert(" ", lineEnd);
			lineEnd++;
			lineLength++;
		}

		int textStart = lineStart + this.cursorCol;
		int textEnd = textStart + sLength;
		textArea.replaceRange(s, textStart, textEnd);

		this.cursorCol = endCol;
	}

	// Variables declaration
	private javax.swing.JScrollPane textScrollPane;

	private javax.swing.JTextArea textArea;
	// End of variables declaration

}
