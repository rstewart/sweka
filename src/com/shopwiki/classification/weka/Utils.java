package com.shopwiki.classification.weka;

import java.io.Closeable;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author rstewart
 */
public class Utils {

	private static final ThreadLocal<NumberFormat> numberFormat = new ThreadLocal<NumberFormat>() {
		@Override
		protected NumberFormat initialValue() {
			return new DecimalFormat();
		}
	};

	/** Copied from com.shopwiki.text.Pretty */
	public static final String comma(long n) {
		return numberFormat.get().format(n);
	}

	/** Copied from com.shopwiki.text.Pretty */
	public static final String comma(double n) {
		return numberFormat.get().format(n);
	}

	/** Copied from com.shopwiki.io.IOUtil */
    public static void close(Closeable closeable) throws IOException {
        if (closeable != null) {
            closeable.close();
        }
    }
}
