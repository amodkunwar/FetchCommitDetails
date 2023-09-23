package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.GitService;

@RestController
public class GitController {

	private final GitService gitService;

	public GitController(GitService gitService) {
		this.gitService = gitService;
	}

	@GetMapping("/git-stats")
	public CommitStats getGitStats(@RequestParam String repositoryUrl, @RequestParam String username,
			@RequestParam String password, @RequestParam String startDate, @RequestParam String endDate,
			@RequestParam String path) throws IOException, ParseException, NoHeadException, GitAPIException {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date parsedStartDate = dateFormat.parse(startDate);
		Date parsedEndDate = dateFormat.parse(endDate);
		return gitService.fetchGitStats(repositoryUrl, username, password, parsedStartDate, parsedEndDate, path);
	}

}
