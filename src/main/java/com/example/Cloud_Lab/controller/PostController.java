package com.example.Cloud_Lab.controller;

import com.example.Cloud_Lab.dto.Community;
import com.example.Cloud_Lab.dto.Post;
import com.example.Cloud_Lab.dto.User;
import com.google.gson.Gson;
import com.microsoft.azure.cosmosdb.Document;
import com.microsoft.azure.cosmosdb.FeedOptions;
import com.microsoft.azure.cosmosdb.FeedResponse;
import com.microsoft.azure.cosmosdb.ResourceResponse;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rx.Observable;

import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import static com.example.Cloud_Lab.controller.CreateDatabaseAndCollections.getCollectionString;
import static com.example.Cloud_Lab.controller.CreateDatabaseAndCollections.getDocumentClient;

@RestController
@RequestMapping("/post")
public class PostController {

    @PostMapping("/add")
    public ResponseEntity<String> addPost(@RequestParam("title") String title,@RequestParam("communityId") String communityId,
                                          @RequestParam("creatorNickname") String creatorNickname,@RequestParam("message") String message,
                                          @RequestParam("linkToImage") String linkToImage,
                                          @RequestParam("linkToParentPost") String linkToParentPost){
        String addedPost = "";
        Post post = new Post();
        try {
            AsyncDocumentClient client = getDocumentClient();
            String UsersCollection = getCollectionString("Post");

            post.setTitle(title);
            post.setCommunityId(communityId);
            post.setCreatorNickname(creatorNickname);
            post.setMessage(message);
            post.setLinkToImage(linkToImage);
            post.setLinkToParentPost(linkToParentPost);
            post.setNumberOfLikes(new Random().nextInt());
            post.setTimeOfCreation(new Date());
            //post.setFamilyId(new Random().toString());
            Observable<ResourceResponse<Document>> resp = client.createDocument(UsersCollection, post, null, false);
            String str =  resp.toBlocking().first().getResource().getId();
            post.setId(str + "0000");

            FeedOptions queryOptions = new FeedOptions();
            queryOptions.setEnableCrossPartitionQuery(true);
            queryOptions.setMaxDegreeOfParallelism(-1);

        } catch( Exception e) {
            e.printStackTrace();
        }
        String contentType = "application/json";

        Gson g = new Gson();
        addedPost = g.toJson(post);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(addedPost);
    }

    @GetMapping("/findById")
    public ResponseEntity<String> findUserById(@RequestParam("id") String id){
        String addedPost = "";
        try {
            AsyncDocumentClient post = getDocumentClient();
            String UsersCollection = getCollectionString("Post");

            FeedOptions queryOptions = new FeedOptions();
            queryOptions.setEnableCrossPartitionQuery(true);
            queryOptions.setMaxDegreeOfParallelism(-1);

            Iterator<FeedResponse<Document>> it = post.queryDocuments(
                    UsersCollection, "SELECT * FROM Post",
                    queryOptions).toBlocking().getIterator();

//            FeedResponse<Document> addedUser = client.queryDocuments(
//                    UsersCollection, "SELECT * FROM Users u WHERE u.id = " + str,
//                    queryOptions).toBlocking().single();


            System.out.println( "Result:");
            while( it.hasNext())
                for( Document d : it.next().getResults())
                    System.out.println( d.toJson());

            it = post.queryDocuments(
                    UsersCollection, "SELECT * FROM Post u WHERE u.id = '" + id + "'",
                    queryOptions).toBlocking().getIterator();

            System.out.println( "Result:");
            while( it.hasNext())
                for( Document d : it.next().getResults()) {
                    System.out.println( d.toJson());
                    addedPost = d.toJson();
                    Gson g = new Gson();
                    User u = g.fromJson(d.toJson(), User.class);
                    System.out.println( u.getId());
                }
        } catch( Exception e) {
            e.printStackTrace();
        }
        String contentType = "application/json";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(addedPost);
    }

}
