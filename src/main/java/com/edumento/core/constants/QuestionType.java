package com.edumento.core.constants;

/** Created by ayman on 09/06/16. */
public enum QuestionType {
	TRUE_FALSE(QuestionCategory.OBJECTIVE), ESSAY(QuestionCategory.SUBJECTIVE),
	MULTIPLE_CHOICES(QuestionCategory.OBJECTIVE), SEQUENCE(QuestionCategory.OBJECTIVE),
	COMPLETE(QuestionCategory.OBJECTIVE), MATCHING(QuestionCategory.OBJECTIVE),
	SINGLE_LINE(QuestionCategory.SUBJECTIVE), SINGLE_CHOICE(QuestionCategory.OBJECTIVE),
	IMAGE_CHOICE(QuestionCategory.OBJECTIVE);

	private final QuestionCategory category;

	QuestionType(QuestionCategory qtype) {
		category = qtype;
	}

	public static boolean contains(String test) {

		for (QuestionType questionType : QuestionType.values()) {
			if (questionType.name().equals(test)) {
				return true;
			}
		}
		return false;
	}

	public QuestionCategory getCategory() {
		return category;
	}

	private enum QuestionCategory {
		OBJECTIVE, SUBJECTIVE
	}
}
