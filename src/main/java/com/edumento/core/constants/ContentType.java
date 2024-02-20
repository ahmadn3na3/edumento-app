package com.edumento.core.constants;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** Created by ahmad on 7/3/16. */
public enum ContentType {
	AUDIO("mp3", "wav"), VIDEO("mp4", "3gp", "mkv", "avi"), TEXT("pdf"), IMAGE("jpg", "png", "bmp", "tiff"),
	INTERACTIVE("zip"), WORKSHEET("pdf"), URL, WORD("doc", "docx", "odt", "fodt"),
	SPREAD_SHEETS("xls", "xlsx", "ods", "fods"), PRESENTATION("ppt", "pptx", "odp", "fodp"), H5P("h5p"), OTHER;

	private final Set<String> extentions = new HashSet<>();

	ContentType(String... extenions) {
		if (extenions != null) {
			Collections.addAll(getExtentions(), extenions);
		}
	}

	public Set<String> getExtentions() {
		return extentions;
	}
}
