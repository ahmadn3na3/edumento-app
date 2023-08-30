package com.edumento.user.services;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edumento.core.configuration.MintProperties;
import com.edumento.core.model.ResponseModel;

/** Created by ahmad on 5/29/16. */
@Service
public class UploadService {
  private final Logger log = LoggerFactory.getLogger(UploadService.class);

  private final MintProperties mintProperties;

  @Autowired
  public UploadService(MintProperties mintProperties) {
    this.mintProperties = mintProperties;
  }

  public ResponseModel getDefaultImagesAndThumbnails() {
    Path imagePath = Paths.get(mintProperties.getUpload().getImg().getPath(), "default", "image");
    Path thumbnailPath =
        Paths.get(mintProperties.getUpload().getImg().getPath(), "default", "thumbnail");

    Map<String, Map<String, String>> defaults = new HashMap<>();
    defaults.put("images", new HashMap<>());
    defaults.put("thumbnails", new HashMap<>());
    Arrays.stream(imagePath.toFile().list())
        .forEach(
            s ->
                defaults
                    .get("images")
                    .put(
                        s.replace(".png", "").replace("_", " "),
                        String.format("%s/img/default/image/%s", mintProperties.getUrl(), s)));
    Arrays.stream(thumbnailPath.toFile().list())
        .forEach(
            s ->
                defaults
                    .get("thumbnails")
                    .put(
                        s.replace("_thum.png", "").replace("_", " "),
                        String.format("%s/img/default/thumbnail/%s", mintProperties.getUrl(), s)));
    return ResponseModel.done(defaults);
  }
}
