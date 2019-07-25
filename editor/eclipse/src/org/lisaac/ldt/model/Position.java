package org.lisaac.ldt.model;

public class Position {
	public int line;
	//public int column;
	public int offset;
	public int length;
	
	public Position(int line, int column, int offset) {
		super();
		this.line = line;
		//this.column = column;
		this.offset = offset;
		length = 0;
	}
	public Position(int line, int column, int offset, int len) {
		super();
		this.line = line;
		//this.column = column;
		this.offset = offset;
		length = len;
	}

	public int getLine() {
		return line;
	}
	//public int getColumn() {
		//return column;
	//}
	
	public boolean hasRange() {
		return length != 0;
	}
	
	public int getCharStart() {
		return offset-length;
	}
	public int getCharEnd() {
		return offset;
	}
	
	public int getStartOffset() {
		return offset;
	}
	
	public int length() {
		return length;
	}
	public void setLength(int l) {
		length = l;
	}
}
