package com.example.demo.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CommitStats;
import com.example.demo.dto.GitDetailsRequest;
import com.example.demo.service.GitService;

@RestController
public class GitController {

	private final GitService gitService;

	public GitController(GitService gitService) {
		this.gitService = gitService;
	}

	@GetMapping("/git-stats")
	public CommitStats getGitStats(@RequestBody GitDetailsRequest gitDetailsRequest)
			throws IOException, ParseException, NoHeadException, GitAPIException {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date parsedStartDate = dateFormat.parse(gitDetailsRequest.getStartDate());
		Date parsedEndDate = dateFormat.parse(gitDetailsRequest.getEndDate());
		return gitService.fetchGitStats(gitDetailsRequest.getRepositoryUrl(), gitDetailsRequest.getUsername(),
				parsedStartDate, parsedEndDate, gitDetailsRequest.getPath(), gitDetailsRequest.getBranch());
	}

}
