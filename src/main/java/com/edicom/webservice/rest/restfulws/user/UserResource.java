package com.edicom.webservice.rest.restfulws.user;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.edicom.webservice.rest.restfulws.jpa.PostRepository;
import com.edicom.webservice.rest.restfulws.jpa.UserRepository;
import com.edicom.webservice.rest.restfulws.post.Post;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

// Our Rest Controller
@RestController
public class UserResource {

    private UserRepository userRepository;
    private PostRepository postRepository;

    //Init
    public UserResource(UserRepository userRepository, PostRepository postRepository){
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    // Method for Users

    //Gets all users
    @GetMapping("/users")
    public MappingJacksonValue retrieveAllUsers(){
        //First we find all users and create a MappingJacksonValue
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(userRepository.findAll());
        //We do not want to return user password or posts, so we only return id, user_name and birth_date
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id","user_name","birth_date");
        FilterProvider filters = new SimpleFilterProvider().addFilter("userFilter", filter);
        mappingJacksonValue.setFilters(filters);
        return mappingJacksonValue;
    }

    //Get a specific user
    @GetMapping("/users/{id}")
    public MappingJacksonValue retrieveUser(@PathVariable int id){
        //We find a user
        Optional<User> user = userRepository.findById(id);

        //If optional is empty, UserNotFoundException
        if (user.isEmpty()){
            throw new UserNotFoundException("User not found - id:"+id);
        }

        //Get the user from the optional
        EntityModel<User> entityModel = EntityModel.of(user.get());

        //Get the link to localhost:8080/users
        WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        entityModel.add(link.withRel("all-users"));

        //Again filter the user
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(entityModel);
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id","user_name","birth_date");
        FilterProvider filters = new SimpleFilterProvider().addFilter("userFilter", filter);
        mappingJacksonValue.setFilters(filters);
        return mappingJacksonValue;
    }

    //Create a user
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user){
        //We save the user and get its id
        User savedUser = userRepository.save(user);
        //Get the uri to the resource
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId()).toUri();
        //Return 201 Status Code with location=uriToResource
        return ResponseEntity.created(location).build();
    }

    //Delete a user
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable int id){
        //Delete user
        userRepository.deleteById(id);
    }

    // Methods for Post

    //Get all posts for specified user
    @GetMapping("/users/{id}/posts")
    public List<Post> retrievePostsForUser(@PathVariable int id){
        //Get the user
        Optional<User> user = userRepository.findById(id);

        //If optional is empty, UserNotFoundException
        if (user.isEmpty()){
            throw new UserNotFoundException("User not found - id:"+id);
        }

        //We can get all posts from retrieved user
        return user.get().getPosts();
    }

    //Create a new post for specified user
    @PostMapping("/users/{id}/posts")
    public ResponseEntity<Post> createPostForUser(@PathVariable int id, @Valid @RequestBody Post post){
        //Get the user
        Optional<User> user = userRepository.findById(id);

        //If optional is empty, UserNotFoundException
        if (user.isEmpty()){
            throw new UserNotFoundException("User not found - id:"+id);
        }

        //Set the user to Post
        post.setUser(user.get());

        //We save the post
        Post savedPost = postRepository.save(post);

        //Get resource uri
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedPost.getId()).toUri();

        //Return 201 Status + uri in location
        return ResponseEntity.created(location).build();
    }
}
