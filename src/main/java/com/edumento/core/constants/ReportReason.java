package com.edumento.core.constants;

/** Created by ahmad on 5/26/16. */
public enum ReportReason {
  UNWANTED_MESSAGES("mint.report.reason.unwanted.message", ReportType.USER),
  UNWANTED_CONTENT("mint.report.reason.unwanted.content", ReportType.USER),
  ABUSIVE_MESSAGES("mint.report.reason.abusive.message", ReportType.USER),
  // ABUSIVE_CONTENT("mint.report.reason.abusive.content"),
  IMPROPER_CONTENT("mint.report.reason.improper.content", ReportType.SPACE),
  IMPROPER_LANGUAGE("mint.report.reason.improper.language", ReportType.USER),
  ANNOYING_DISCUSSION("mint.report.reason.annoying.discussion", ReportType.SPACE),
  ANNOYING_SPACES("mint.report.reason.annoying.spaces", ReportType.USER),
  //  UNWANTED_SPACE("mint.report.reason.unwanted.space", ReportType.SPACE),
  SPACE_HACKED("mint.report.reason.hacked", ReportType.SPACE),
  IDENTITY("mint.report.reason.identity", ReportType.USER);
  //  OTHER("mint.report.reason.other", ReportType.SPACE);

  private String message;
  private ReportType type;

  ReportReason(String message, ReportType type) {
    this.message = message;
    this.type = type;
  }

  public String getMessage() {
    return message;
  }

  public ReportType getType() {
    return type;
  }
}
