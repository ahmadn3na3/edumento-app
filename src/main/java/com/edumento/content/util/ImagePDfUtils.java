package com.edumento.content.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImagePDfUtils {
  private static final Logger log = LoggerFactory.getLogger(ImagePDfUtils.class);

  public static void imageToPdf(Path source, Path distination) throws IOException {
    Process process;
    String cmd = String.join(
        " ",
        "/usr/bin/convert",
        source.toString(),
        " -background white",
        distination.toString());
    log.debug("command -> {}", cmd);
    process = Runtime.getRuntime().exec(cmd);
    while (process.isAlive()) {
      continue;
    }
    log.debug("process exit value ==> {}", process.exitValue());
    if (process.exitValue() != 0) {
      BufferedReader bufferedInputStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      bufferedInputStream.lines().forEach(arg0 -> log.error("process error stream ==> {}", arg0));
    }
  }

  public static void extractThumbFromPdf(Path source, Path distination) throws IOException {
    Process process;
    String cmd = String.join(
        " ",
        "/usr/bin/convert",
        " -resize 150x150 ",
        " -background white",
        source.toString(),
        distination.toString());
    log.debug("command -> {}", cmd);
    process = Runtime.getRuntime().exec(cmd);
    while (process.isAlive()) {
      continue;
    }
    log.debug("process exit value ==> {}", process.exitValue());
    if (process.exitValue() != 0) {
      BufferedReader bufferedInputStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      bufferedInputStream.lines().forEach(arg0 -> log.error("process error stream ==> {}", arg0));
    }
  }
}
