package com.edumento.user.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.edumento.user.services.ModuleService;

@RestController("/home")
public class HomeController {

	@Autowired
	ModuleService moduleService;

	@GetMapping(path = "/", produces = {MediaType.APPLICATION_JSON_VALUE})
	public String index() {
		return "Example Response";
	}
}
