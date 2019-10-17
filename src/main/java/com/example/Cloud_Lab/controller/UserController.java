package com.example.Cloud_Lab.controller;

import com.example.Cloud_Lab.dto.User;
import com.google.gson.Gson;
import com.microsoft.azure.cosmosdb.Document;
import com.microsoft.azure.cosmosdb.FeedOptions;
import com.microsoft.azure.cosmosdb.FeedResponse;
import com.microsoft.azure.cosmosdb.ResourceResponse;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rx.Observable;

import java.util.Iterator;

import static com.example.Cloud_Lab.controller.CreateDatabaseAndCollections.getCollectionString;
import static com.example.Cloud_Lab.controller.CreateDatabaseAndCollections.getDocumentClient;

@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/add")
    public ResponseEntity<String> addUser(@RequestParam("name") String name){
        String addedUser = "";
        User user = new User();
        try {
            AsyncDocumentClient client = getDocumentClient();
            String UsersCollection = getCollectionString("Users");

            user.setName(name);
            Observable<ResourceResponse<Document>> resp = client.createDocument(UsersCollection, user, null, false);
            String str =  resp.toBlocking().first().getResource().getId();
            user.setId(str);

            FeedOptions queryOptions = new FeedOptions();
            queryOptions.setEnableCrossPartitionQuery(true);
            queryOptions.setMaxDegreeOfParallelism(-1);

        } catch( Exception e) {
            e.printStackTrace();
        }
        String contentType = "application/json";

        Gson g = new Gson();
        addedUser = g.toJson(user);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(addedUser);
    }

    @GetMapping("/findUserById")
    public ResponseEntity<String> findUserById(@RequestParam("id") String id){
        String addedUser = "";
        try {
            AsyncDocumentClient client = getDocumentClient();
            String UsersCollection = getCollectionString("Users");

            FeedOptions queryOptions = new FeedOptions();
            queryOptions.setEnableCrossPartitionQuery(true);
            queryOptions.setMaxDegreeOfParallelism(-1);

            Iterator<FeedResponse<Document>> it = client.queryDocuments(
                    UsersCollection, "SELECT * FROM Users",
                    queryOptions).toBlocking().getIterator();

//            FeedResponse<Document> addedUser = client.queryDocuments(
//                    UsersCollection, "SELECT * FROM Users u WHERE u.id = " + str,
//                    queryOptions).toBlocking().single();


            System.out.println( "Result:");
            while( it.hasNext())
                for( Document d : it.next().getResults())
                    System.out.println( d.toJson());

            it = client.queryDocuments(
                    UsersCollection, "SELECT * FROM Users u WHERE u.id = '" + id + "'",
                    queryOptions).toBlocking().getIterator();

            System.out.println( "Result:");
            while( it.hasNext())
                for( Document d : it.next().getResults()) {
                    System.out.println( d.toJson());
                    addedUser = d.toJson();
                    Gson g = new Gson();
                    User u = g.fromJson(d.toJson(), User.class);
                    System.out.println( u.getId());
                }
        } catch( Exception e) {
            e.printStackTrace();
        }
        String contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(addedUser);
    }
}
