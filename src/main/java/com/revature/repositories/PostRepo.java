package com.revature.repositories;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.revature.models.Post;
import com.revature.models.Profile;

@Repository
public interface PostRepo extends JpaRepository<Post, Integer> {
    Page<Post> findAll(Pageable pageable);
    Page<Post> getPostsByCreator(Pageable pageable, int profileID );
    List<Post> findTop3ByCreator(Profile profile, Sort sort);

    //Avoid this repository if possible - it is very temperamental. It likes ice-cream and long walks on the beach.
    // It also likes to use proper terminology as per the note in the post model.
    List<Post> findAllByGroupGroupId(int groupId);
    Post getPostByPsid(Integer psid);
    List<Post> findAllByCreator(Profile profile);
    Page<Post> findAllByGroupIsNull(Pageable pageable);
    List<Post> findAllByGroupIsNull();

}


