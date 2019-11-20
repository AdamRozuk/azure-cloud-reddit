package com.example.Cloud_Lab.controller;

import com.example.Cloud_Lab.dto.User;
import com.google.gson.Gson;
import com.microsoft.azure.cosmosdb.Document;
import com.microsoft.azure.cosmosdb.FeedOptions;
import com.microsoft.azure.cosmosdb.FeedResponse;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.StringJoiner;

import static com.example.Cloud_Lab.controller.CreateDatabaseAndCollections.getCollectionString;
import static com.example.Cloud_Lab.controller.CreateDatabaseAndCollections.getDocumentClient;

@RestController
@RequestMapping("/page")
public class PageController {

    @GetMapping("/initialPage")
    public ResponseEntity<String> initialPage(){
        String addedCommunity = "";
        StringBuilder builder = new StringBuilder();
        //List<String> communities= new ArrayList<>();
        try {
            AsyncDocumentClient client = getDocumentClient();
            String UsersCollection = getCollectionString("Post");

            FeedOptions queryOptions = new FeedOptions();
            queryOptions.setEnableCrossPartitionQuery(true);
            queryOptions.setMaxDegreeOfParallelism(-1);

            Iterator<FeedResponse<Document>> it = client.queryDocuments(
                    UsersCollection, "SELECT TOP 2 * FROM Post ORDER BY Post.numberOfLikes desc",
                    queryOptions).toBlocking().getIterator();

            System.out.println( "Result:");
            builder.append("{ \"posts\": [");
            StringJoiner mystring = new StringJoiner(",");
            while( it.hasNext()){
                for( Document d : it.next().getResults()){
                    mystring.add(d.toJson());
                }
            }

            builder.append(mystring);
            builder.append("]}");

            System.out.println( "Result:");
            while( it.hasNext())
                for( Document d : it.next().getResults()) {
                    System.out.println( d.toJson());
                    //communities.add(d.toJson());
                    addedCommunity = d.toJson();
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
                .body(builder.toString());
    }

    @GetMapping("/thread")
    public ResponseEntity<String> threadPage(@RequestParam String id){
        String addedCommunity = "";
        StringBuilder builder = new StringBuilder();
        //List<String> communities= new ArrayList<>();
        try {
            AsyncDocumentClient client = getDocumentClient();
            String UsersCollection = getCollectionString("Post");

            FeedOptions queryOptions = new FeedOptions();
            queryOptions.setEnableCrossPartitionQuery(true);
            queryOptions.setMaxDegreeOfParallelism(-1);

            Iterator<FeedResponse<Document>> it = client.queryDocuments(
                    UsersCollection, "SELECT * FROM Post p WHERE p.linkToParentPost = '" + id +"'",
                    queryOptions).toBlocking().getIterator();

            System.out.println( "Result:");
            builder.append("{ \"posts\": [");
            StringJoiner mystring = new StringJoiner(",");
            while( it.hasNext()){
                for( Document d : it.next().getResults()){
                    mystring.add(d.toJson());
                }
            }

            builder.append(mystring);
            builder.append("]}");

            System.out.println( "Result:");
            while( it.hasNext())
                for( Document d : it.next().getResults()) {
                    System.out.println( d.toJson());
                    //communities.add(d.toJson());
                    addedCommunity = d.toJson();
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
                .body(builder.toString());
    }
}
