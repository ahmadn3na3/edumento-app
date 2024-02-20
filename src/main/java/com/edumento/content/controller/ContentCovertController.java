package com.edumento.content.controller;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edumento.content.services.ContentBackGroundService;
import com.edumento.core.constants.Code;
import com.edumento.core.constants.ContentType;
import com.edumento.core.exception.MintException;

@RestController()
@RequestMapping("/contentConvert")
public class ContentCovertController {
	@Autowired
	ContentBackGroundService contentBackGroundService;

	private final Map<ContentType, CompletableFuture<List<Path>>> futureMap = new ConcurrentHashMap<>();

	@GetMapping(path = "/{type}")

	public void contentConvert(@PathVariable(name = "type") ContentType contentType) {
		var pathCompletableFuture = futureMap.get(contentType);
		if (pathCompletableFuture != null && !pathCompletableFuture.isDone()) {
			throw new MintException(Code.INVALID, "Not Available");
		}
		futureMap.put(contentType, contentBackGroundService.convertType(contentType));
	}

	@GetMapping(path = "/{type}/status")
	public String checkStatus(@PathVariable(name = "type") ContentType contentType)
			throws ExecutionException, InterruptedException {
		var pathCompletableFuture = futureMap.get(contentType);
		if (pathCompletableFuture == null) {
			return "no running jobs for " + contentType.name();
		}
		if (pathCompletableFuture.isDone()) {
			var message = "done" + pathCompletableFuture.get().size();
			futureMap.remove(contentType);
			return message;
		}
		if (pathCompletableFuture.isCompletedExceptionally()) {
			futureMap.remove(contentType);
			return "error";
		}
		return "running";
	}
}
