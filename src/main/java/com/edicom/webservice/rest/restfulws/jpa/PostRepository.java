package com.edicom.webservice.rest.restfulws.jpa;

import com.edicom.webservice.rest.restfulws.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {

}
