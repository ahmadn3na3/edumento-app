package com.edumento.assessment.model.leaderboard;

public class UserSpaceRankingModel {

	private String spaceName;
	private String spaceImage;
	private int spaceRanking;
	private int totalUsersInSpace;
	
	public UserSpaceRankingModel(String spaceName, String spaceImage, int spaceRanking, int totalUsersInSpace) {
		this.spaceName = spaceName;
		this.spaceImage = spaceImage;
		this.spaceRanking = spaceRanking;
		this.totalUsersInSpace = totalUsersInSpace;
	}
	
	public String getSpaceName() {
		return spaceName;
	}
	public void setSpaceName(String spaceName) {
		this.spaceName = spaceName;
	}
	public int getSpaceRanking() {
		return spaceRanking;
	}
	public void setSpaceRanking(int spaceRanking) {
		this.spaceRanking = spaceRanking;
	}

	public int getTotalUsersInSpace() {
		return totalUsersInSpace;
	}

	public void setTotalUsersInSpace(int totalUsersInSpace) {
		this.totalUsersInSpace = totalUsersInSpace;
	}

	public String getSpaceImage() {
		return spaceImage;
	}

	public void setSpaceImage(String spaceImage) {
		this.spaceImage = spaceImage;
	}
	
	
}
