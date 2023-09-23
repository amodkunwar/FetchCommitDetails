package com.example.demo.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.springframework.stereotype.Service;

import com.example.demo.CommitStats;

@Service
public class GitService {

	public CommitStats fetchGitStats(String repositoryUrl, String username, String password, Date startDate,
			Date endDate, String path) throws IOException, GitAPIException {
		String projectName = repositoryUrl.substring(30);
		path = path + ":/gitTestingCheck/" + projectName;
		File folder = new File(path);
		Git git = null;
		CloneCommand cloneCommand = Git.cloneRepository().setBranch("master").setURI(repositoryUrl);
		if (folder.exists()) {
			git = Git.open(folder);
			PullCommand pull = git.pull();
			pull.call();
		} else {
			git = cloneCommand.setDirectory(folder)
					.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)).call();
		}
		Repository repository = git.getRepository();

		Iterable<RevCommit> commits = git.log().add(repository.resolve("HEAD")).call();

		int totalCommits = 0;
		int totalLinesAdded = 0;

		for (RevCommit commit : commits) {
			if (commit.getAuthorIdent().getWhen().after(startDate)
					&& commit.getAuthorIdent().getWhen().before(endDate)) {
				totalCommits++;

				if (commit.getParentCount() > 0) {
					List<DiffEntry> diffs = getDiffs(repository, commit);
					for (DiffEntry entry : diffs) {
						totalLinesAdded += countLinesAdded(repository, entry);
					}
				}
			}
		}
		return new CommitStats(totalCommits, totalLinesAdded);
	}

	private List<DiffEntry> getDiffs(Repository repository, RevCommit commit) throws IOException, GitAPIException {
		RevCommit parent = commit.getParent(0);
		if (parent == null) {
			// No parent, likely the initial commit
			return Collections.emptyList();
		}

		try (Git git = new Git(repository)) {
			return git.diff().setOldTree(prepareTreeParser(repository, parent.getId()))
					.setNewTree(prepareTreeParser(repository, commit.getId())).call();
		}
	}

	private CanonicalTreeParser prepareTreeParser(Repository repository, ObjectId objectId) throws IOException {
		try (RevWalk walk = new RevWalk(repository)) {
			RevCommit commit = walk.parseCommit(objectId);
			RevTree tree = walk.parseTree(commit.getTree().getId());
			CanonicalTreeParser treeParser = new CanonicalTreeParser();
			try (ObjectReader reader = repository.newObjectReader()) {
				treeParser.reset(reader, tree.getId());
			}
			return treeParser;
		}
	}

	private int countLinesAdded(Repository repository, DiffEntry entry) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (DiffFormatter formatter = new DiffFormatter(out)) {
			formatter.setRepository(repository);
			formatter.setDiffComparator(RawTextComparator.DEFAULT);
			formatter.format(entry);
		}
		String diffText = out.toString();
		// Parse the diff text to count lines added, e.g., by counting lines starting
		// with '+'
		// Implement your logic here based on the specific format of your diffs

		// Example:
		int linesAdded = 0;
		for (String line : diffText.split("\n")) {
			if (line.startsWith("+")) {
				linesAdded++;
			}
		}
		return linesAdded;
	}
}