package com.revature.dto;

import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;

import com.revature.models.Post;
import com.revature.utilites.SecurityUtil;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * The class is a data transfer object for the Post model
 *
 * @author John Boyle
 * @batch: 211129-Enterprise
 *
 */

//I was a DAO model not a DTO model Cohort. If you're in the same boat and reading this - smile and act like you go this.
// You don't need to get it, you just have to work with it. It's kind of like an extension to the Model.
@Data
@AllArgsConstructor
public class PostDTO {

	private int psid;

	private ProfileDTO creator;

	private String body;

	private String imgURL;

	private Timestamp datePosted;

	private Set<Integer> likes = new LinkedHashSet<>();

	private Set<Integer> bookmarks = new LinkedHashSet<>(); //Bookmark set declared in DTO.
	// Look Below. Add to Post toPost() and add to or create a constructor.

	private GroupDTO group;

	public PostDTO() {
		super();
		psid = SecurityUtil.getId();
	}

	public PostDTO(Post post) {
		if (post != null) {
			psid = post.getPsid();
			creator = post.getCreator() != null ? new ProfileDTO(post.getCreator()) : null;
			body = post.getBody();
			imgURL = post.getImgURL();
			datePosted = post.getDatePosted();
			likes = post.getLikes();
			group = post.getGroup() != null ? new GroupDTO(post.getGroup()) : null;
		}
	}

	public Post toPost() {
		return new Post(psid, (creator != null ? creator.toProfile() : null), body, imgURL, datePosted, likes, bookmarks,
				(group != null ? group.toGroup() : null));
	}

	public PostDTO(ProfileDTO creator, String body, String imgURL, Timestamp datePosted, GroupDTO group) {
		this();
		this.creator = creator;
		this.body = body;
		this.imgURL = imgURL;
		this.datePosted = datePosted;
		this.group = group;
	}

	public PostDTO(ProfileDTO creator, String body, String imgURL, Timestamp datePosted, Set<Integer> likes,
				   GroupDTO group) {
		this();
		this.creator = creator;
		this.body = body;
		this.imgURL = imgURL;
		this.datePosted = datePosted;
		this.likes = likes;
		this.group = group;
	}

	public PostDTO(ProfileDTO creator, String body, String imgURL, Timestamp datePosted, Set<Integer> likes, Set<Integer> bookmarks,
				   GroupDTO group) {
		this();
		this.creator = creator;
		this.body = body;
		this.imgURL = imgURL;
		this.datePosted = datePosted;
		this.likes = likes;
		this.bookmarks = bookmarks;
		this.group = group;
	}

}