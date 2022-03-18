package com.revature.models;

import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.*;

import com.revature.utilites.SecurityUtil;

import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "post")
@Data
@AllArgsConstructor
public class Post {

	@Id
	@Column(name = "post_id", unique = true, nullable = false)
	private int psid;

	@ManyToOne
	@JoinColumn(name = "profile_id", nullable = false)
	private Profile creator; //*<- to note: please use the proper name of the column to create new
	// methods in the repo. The repo will flip out on you if you don't.

	@Column(name = "body")
	private String body;

	@Column(name = "image_url", columnDefinition = "TEXT")
	private String imgURL;

	@Column(name = "date_posted", nullable = false)
	private Timestamp datePosted;

	@CollectionTable(name = "likes", joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "post_id"))
	@Column(name = "profile_id")
	@ElementCollection()
	private Set<Integer> likes = new LinkedHashSet<>();

	//new bookmarks table to identify bookmarks by post_id (post that was bookmarked) and the
	// profile_id (user/profile that bookmarked the post) ~ Modeled after likes table.

	@CollectionTable(name = "bookmarks", joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "post_id"))
	@Column(name = "profile_id")
	@ElementCollection()
	private Set<Integer> bookmarks = new LinkedHashSet<>(); //Don't forget to swing by the Post DTO if you update the model and declare this.

	@ManyToOne
	private Group group;

	public Post() {
		super();
		this.psid = SecurityUtil.getId();

	}

	public Post(Profile creator, String body, String imgURL, Timestamp datePosted, Group group) {
		this();
		this.creator = creator;
		this.body = body;
		this.imgURL = imgURL;
		this.datePosted = datePosted;
		this.group = group;
	}

	public Post(int psid, Profile creator, String body, String imgURL, Timestamp datePosted, Set<Integer> likes, Group group) {
		this.psid = psid;
		this.creator = creator;
		this.body = body;
		this.imgURL = imgURL;
		this.datePosted = datePosted;
		this.likes = likes;
		this.group = group;
	}

	public Post(int psid, Profile creator, String body, String imgURL, Timestamp dateposted, Group group) {
		this.psid = psid;
		this.creator = creator;
		this.body = body;
		this.imgURL = imgURL;
		this.datePosted = dateposted;
		this.group = group;
	}

}