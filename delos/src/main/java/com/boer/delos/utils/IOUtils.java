package com.boer.delos.utils;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {
	/** 关闭流 */
	public static boolean close(Closeable io) {
		if (io != null) {
			try {
				io.close();
			} catch (IOException e) {
				L.d("IOUtils "+e.toString());
			}
		}
		return true;
	}
}