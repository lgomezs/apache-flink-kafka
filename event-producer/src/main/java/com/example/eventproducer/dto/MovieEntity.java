package com.example.eventproducer.dto;

import java.io.Serial;
import java.io.Serializable;

public class MovieEntity implements Serializable {

	@Serial
	private static final long serialVersionUID = 2401264032107342627L;

	private Long id;

	private String title;

	private int year;

	private String genre;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getYear() {
		return this.year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getGenre() {
		return this.genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

}
