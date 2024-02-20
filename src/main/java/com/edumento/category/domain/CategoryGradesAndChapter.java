package com.edumento.category.domain;

import java.util.LinkedList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.edumento.category.model.Chapter;
import com.edumento.category.model.Grade;
import com.edumento.core.domain.AbstractEntity;

@Document(collection = "CategoryGradesAndChapter")
public class CategoryGradesAndChapter extends AbstractEntity {
	@Id
	private String id;

	@Field
	@Indexed(name = "categoryId")
	private Long categoryId;

	private LinkedList<Grade> grades = new LinkedList<>();
	private LinkedList<Chapter> chapters = new LinkedList<>();
	private Long userId;

	public CategoryGradesAndChapter() {
	}

	public CategoryGradesAndChapter(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LinkedList<Grade> getGrades() {
		return grades;
	}

	public void setGrades(LinkedList<Grade> grades) {
		this.grades = grades;
	}

	public LinkedList<Chapter> getChapters() {
		return chapters;
	}

	public void setChapters(LinkedList<Chapter> chapters) {
		this.chapters = chapters;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
