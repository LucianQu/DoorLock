package com.blg.rtu.vo2xml;

import com.blg.rtu3.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class Help {

	public String out(MainActivity act) throws Exception {
		Vo2Xml vo = new Vo2Xml();
		String xml = vo.toXml();

		return xml;
	}

	public void in(MainActivity act, File f) throws Exception {
		InputStream is = null ;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			is = new FileInputStream(f);
			byte[] b = new byte[1024];
			int n;
			while ((n = is.read(b)) != -1) {
				out.write(b, 0, n);
			}// end while
		} catch (Exception e) {
			throw e ;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
				}// end try
			}// end if
		}// end try

		byte[] buffer = out.toByteArray();

		Vo2Xml vo = new Vo2Xml().toObject(buffer) ;

	}
}
