/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package com.util;

public class SerialNumber {
	private SerialNo firstNo;
	private SerialNo serialNo;

	public SerialNumber(int iMax)
  {
    int max = (iMax > 0) ? iMax : 1;
    long base = (long)Math.pow(10.0D, Integer.toString(max).length());
    SerialNo firstNo = new SerialNo(1, Long.toString(base + 1L).substring(1));
    SerialNo preNo = firstNo;
    for (int i = 2; i <= max; ++i) {
      SerialNo serialNo = new SerialNo(i, Long.toString(base + i).substring(1));
      preNo.setNext(serialNo);
      preNo = serialNo;
    }
    preNo.setNext(firstNo);
    this.firstNo = firstNo;
    this.serialNo = firstNo;
  }

	public synchronized void reset() {
		this.serialNo = this.firstNo;
	}

	public synchronized String nextString() {
		String sn = this.serialNo.getString();
		this.serialNo = this.serialNo.getNext();
		return sn;
	}

	public synchronized int next() {
		int sn = this.serialNo.getInt();
		this.serialNo = this.serialNo.getNext();
		return sn;
	}

	private class SerialNo {
		private SerialNo next;
		private int serialNo;
		private String serialNoString;

		private SerialNo(int paramInt, String paramString) {
			this.serialNo = paramInt;
			this.serialNoString = serialNoString;
		}

		private int getInt() {
			return this.serialNo;
		}

		private String getString() {
			return this.serialNoString;
		}

		private void setNext(SerialNo next) {
			this.next = next;
		}

		private SerialNo getNext() {
			return this.next;
		}

		public String toString() {
			return "SerialNo [serialNo=" + this.serialNo + ", serialNoString=" + this.serialNoString + "]";
		}
	}
}