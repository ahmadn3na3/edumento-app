package com.edumento.assessment.model.leaderboard;

/** Created by A.Alsayed on 16/01/2019. */
public class LeaderboardModel {

	private String username;
	private int userlevel;
	private Float globalScorePoints = 0.0f;

	public LeaderboardModel(String username, int userLevel, Float globalScorePoints) {
		this.username = username;
		userlevel = userLevel;
		this.globalScorePoints = globalScorePoints;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getUserlevel() {
		return userlevel;
	}

	public void setUserlevel(int userlevel) {
		this.userlevel = userlevel;
	}

	public Float getGlobalScorePoints() {
		return globalScorePoints;
	}

	public void setGlobalScorePoints(Float globalScorePoints) {
		this.globalScorePoints = globalScorePoints;
	}
}
