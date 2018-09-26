package br.com.cnabbradesco.utils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ResourceManager {
	public static boolean extract(String resource, String pathToExtract) throws Exception {
		String[] aux = resource.split("/");
		String resourceName = aux[aux.length - 1];
		InputStream is = ResourceManager.class.getResourceAsStream(resource);
		FileOutputStream fos = new FileOutputStream(pathToExtract + "/" + resourceName);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		int len = 0;
		byte[] buffer = new byte[1024];
		while ((len = is.read(buffer)) > 0) {
			bos.write(buffer, 0, len);
			bos.flush();
		}
		is.close();
		fos.close();
		bos.close();
		return true;
	}
}
