package com.mirrorlabs.filebrowser;

import java.util.HashMap;
import java.util.Map;

import android.webkit.MimeTypeMap;

public class MimeTypes {

	private static Map<String, String> mMimeTypes = new HashMap<String, String>();

	public void put(String type, String extension) {
		// Convert extensions to lower case letters for easier comparison
		extension = extension.toLowerCase();
		mMimeTypes.put(type, extension);
	}

	public static String getMimeType(String filename) {
		String extension = getExtension(filename);
		// Let's check the official map first. Webkit has a nice extension-to-MIME
		// map.
		// Be sure to remove the first character from the extension, which is the
		// "." character.
		if (extension.length() > 0) {
			String webkitMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));
			if (webkitMimeType != null)
				return webkitMimeType;// Found one. Let's take it!
		}
		// Convert extensions to lower case letters for easier comparison
		extension = extension.toLowerCase();
		String mimetype = mMimeTypes.get(extension);
		return mimetype == null ? "*/*" : mimetype;
	}

	public static String getExtension(String uri) {
		if (uri == null)
			return null;
		int dot = uri.lastIndexOf(".");
		return dot >= 0 ? uri.substring(dot) : "";
	}

}
