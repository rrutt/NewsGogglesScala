package com.live.rrutt.newsgoggles;

// Reference: http://cognitiveentity.wordpress.com/2012/09/13/java-7u6-string-performance-regression/

public class StringDecorator implements CharSequence {

	private final String contents;
	private final int offset;
	private final int length;

	public StringDecorator(String contents) {
		this.contents = contents;
		this.offset = 0;
		this.length = contents.length();
	}

	private StringDecorator(String contents, int offset, int length) {
		this.contents = contents;
		this.offset = offset;
		this.length = length;
	}

	public int length() {
		return length;
	}

	public char charAt(int index) {
		return contents.charAt(index + offset);
	}

	public CharSequence subSequence(int start, int end) {
		return new StringDecorator(contents, offset + start, end - start);
	}

	public String toString() {
		return contents.substring(offset, offset + length);
	}

}