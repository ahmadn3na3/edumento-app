package com.edumento.content.domain;

public class Chunk {
	private Integer chunkIndex = 0;
	private Long chunkStart;
	private Long chunkEnd;

	public Chunk() {
	}

	public Chunk(int chunkIndex) {
		this.chunkIndex = chunkIndex;
	}

	public Chunk(Integer chunkIndex, Long chunkStart, Long chunkEnd) {
		this.chunkIndex = chunkIndex;
		this.chunkStart = chunkStart;
		this.chunkEnd = chunkEnd;
	}

	public Integer getChunkIndex() {
		return chunkIndex;
	}

	public void setChunkIndex(Integer chunkIndex) {
		this.chunkIndex = chunkIndex;
	}

	public Long getChunkStart() {
		return chunkStart;
	}

	public void setChunkStart(Long chunkStart) {
		this.chunkStart = chunkStart;
	}

	public Long getChunkEnd() {
		return chunkEnd;
	}

	public void setChunkEnd(Long chunkEnd) {
		this.chunkEnd = chunkEnd;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		var chunk = (Chunk) o;

		return chunkIndex.equals(chunk.chunkIndex);
	}

	@Override
	public int hashCode() {
		return chunkIndex.hashCode();
	}
}
