package com.example.demo;

public class CommitStats {

	private int totalCommits;
	private int totalLinesAdded;

	public CommitStats(int totalCommits, int totalLinesAdded) {
        this.totalCommits = totalCommits;
        this.totalLinesAdded = totalLinesAdded;
    }

	public int getTotalCommits() {
		return totalCommits;
	}

	public void setTotalCommits(int totalCommits) {
		this.totalCommits = totalCommits;
	}

	public int getTotalLinesAdded() {
		return totalLinesAdded;
	}

	public void setTotalLinesAdded(int totalLinesAdded) {
		this.totalLinesAdded = totalLinesAdded;
	}

}
