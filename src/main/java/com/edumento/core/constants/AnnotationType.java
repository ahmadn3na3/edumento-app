package com.edumento.core.constants;

/** Created by ayman on 03/08/16. */
public enum AnnotationType {
	VOICE_NOTE("VOICE_NOTE", new ContentType[] { ContentType.IMAGE, ContentType.TEXT }),
	TEXT_NOTE("TEXT_NOTE",
			new ContentType[] { ContentType.IMAGE, ContentType.TEXT, ContentType.AUDIO, ContentType.VIDEO }),
	UNDERLINE("UNDERLINE", new ContentType[] { ContentType.IMAGE, ContentType.TEXT }),
	HIGHLIGHT("HIGHLIGHT", new ContentType[] { ContentType.IMAGE, ContentType.TEXT }),
	TEXT("TEXT", new ContentType[] { ContentType.IMAGE, ContentType.TEXT }),
	RECTANGLE("RECTANGLE", new ContentType[] { ContentType.IMAGE, ContentType.TEXT }),
	ELLIPSE("ELLIPSE", new ContentType[] { ContentType.IMAGE, ContentType.TEXT }),
	CIRCLE("CIRCLE", new ContentType[] { ContentType.IMAGE, ContentType.TEXT }),
	BOOKMARK("BOOKMARK", new ContentType[] { ContentType.TEXT }),
	FREEHAND("FREEHAND", new ContentType[] { ContentType.IMAGE, ContentType.TEXT });

	String name;
	ContentType[] allowedOn;

	AnnotationType(String name, ContentType[] allowedOn) {
		this.name = name;
		this.allowedOn = allowedOn;
	}

	public String getName() {
		return name;
	}

	public ContentType[] getAllowedOn() {
		return allowedOn;
	}
}
