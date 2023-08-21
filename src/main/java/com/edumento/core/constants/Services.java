package com.edumento.core.constants;

/** Created by ahmad on 5/24/17. */
public enum Services {
  API("API", "rk.api", "apiqueue"),
  QUESTION_BANK("QUESTION_BANK", "rk.qbank", "qbqueue"),
  CONTENT("CONTENT", "rk.content", "contentqueue"),
  BATCH("BATCH", "rk.batch", "batchqueue"),
  NOTIFICATIONS("NOTIFICATIONS", "rk.notification", "notificationqueue"),
  AUDITING("AUDITING", "rk.audit", "auditqueue"),
  INDEX("INDEX", "rk.index", "indexqueue"),
  CHAT("CHAT", "rk.chat", "chatqueue");

  private String service;
  private String routingKey;
  private String queue;

  Services(String service, String routingKey, String queue) {
    this.service = service;
    this.routingKey = routingKey;
    this.queue = queue;
  }

  public String getService() {
    return service;
  }

  public String getRoutingKey() {
    return routingKey;
  }

  public String getQueue() {
    return queue;
  }
}
