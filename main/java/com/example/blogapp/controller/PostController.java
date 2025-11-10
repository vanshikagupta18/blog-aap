package com.example.blogapp.controller;

import com.example.blogapp.entity.BlogPost;
import com.example.blogapp.entity.Comment;
import com.example.blogapp.repository.BlogPostRepository;
import com.example.blogapp.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, allowCredentials = "true")
@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private CommentRepository commentRepository;

    // ✅ Get all posts (with full image URLs)
    @GetMapping
    public List<BlogPost> getAllPosts() {
        List<BlogPost> posts = blogPostRepository.findAll();

        posts.forEach(post -> {
            if (post.getImageUrl() != null && !post.getImageUrl().isBlank()) {
                if (!post.getImageUrl().startsWith("http")) {
                    String fullUrl = ServletUriComponentsBuilder
                            .fromCurrentContextPath()
                            .path("/uploads/")
                            .path(post.getImageUrl())
                            .toUriString();
                    post.setImageUrl(fullUrl);
                }
            }
        });

        return posts;
    }

    // ✅ Get a single post by ID
    @GetMapping("/{id}")
    public BlogPost getPostById(@PathVariable Long id) {
        return blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + id));
    }

    // ✅ Create a new post
    @PostMapping
    public BlogPost createPost(@RequestBody BlogPost post) {
        if (post.getImageUrl() != null && post.getImageUrl().contains("/uploads/")) {
            String filename = post.getImageUrl().substring(post.getImageUrl().lastIndexOf("/") + 1);
            post.setImageUrl(filename);
        }
        return blogPostRepository.save(post);
    }

    // ✅ Update an existing post
    @PutMapping("/{id}")
    public BlogPost updatePost(@PathVariable Long id, @RequestBody BlogPost updatedPost) {
        return blogPostRepository.findById(id)
                .map(post -> {
                    post.setTitle(updatedPost.getTitle());
                    post.setContent(updatedPost.getContent());
                    post.setAuthor(updatedPost.getAuthor());

                    if (updatedPost.getImageUrl() != null && !updatedPost.getImageUrl().isBlank()) {
                        String filename = updatedPost.getImageUrl()
                                .substring(updatedPost.getImageUrl().lastIndexOf("/") + 1);
                        post.setImageUrl(filename);
                    }

                    return blogPostRepository.save(post);
                })
                .orElseThrow(() -> new RuntimeException("Post not found with id " + id));
    }

    // ✅ Delete a post
    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable Long id) {
        if (!blogPostRepository.existsById(id)) {
            throw new RuntimeException("Post not found with id " + id);
        }
        blogPostRepository.deleteById(id);
        return "Post deleted successfully!";
    }

    // -------------------------------
    // 🧩 COMMENT MANAGEMENT SECTION
    // -------------------------------

    // ✅ Get all comments for a post
    @GetMapping("/{postId}/comments")
    public List<Comment> getCommentsForPost(@PathVariable Long postId) {
        return commentRepository.findByPostId(postId);
    }

    // ✅ Add a new comment to a post
    @PostMapping("/{postId}/comments")
    public Comment addComment(@PathVariable Long postId, @RequestBody Comment comment) {
        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + postId));
        comment.setPost(post);
        return commentRepository.save(comment);
    }

    // ✅ Update a comment
    @PutMapping("/comments/{commentId}")
    public Comment updateComment(@PathVariable Long commentId, @RequestBody Comment updatedComment) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id " + commentId));

        comment.setAuthor(updatedComment.getAuthor());
        comment.setText(updatedComment.getText());
        comment.setTimestamp(updatedComment.getTimestamp());
        return commentRepository.save(comment);
    }

    // ✅ Delete a comment
    @DeleteMapping("/comments/{commentId}")
    public String deleteComment(@PathVariable Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException("Comment not found with id " + commentId);
        }
        commentRepository.deleteById(commentId);
        return "Comment deleted successfully!";
    }
}
