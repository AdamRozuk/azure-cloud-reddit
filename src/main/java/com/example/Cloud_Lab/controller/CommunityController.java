package com.example.Cloud_Lab.controller;

import com.example.Cloud_Lab.dto.Community;
import com.example.Cloud_Lab.dto.User;
import com.google.gson.Gson;
import com.microsoft.azure.cosmosdb.Document;
import com.microsoft.azure.cosmosdb.FeedOptions;
import com.microsoft.azure.cosmosdb.ResourceResponse;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rx.Observable;

import static com.example.Cloud_Lab.controller.CreateDatabaseAndCollections.getCollectionString;
import static com.example.Cloud_Lab.controller.CreateDatabaseAndCollections.getDocumentClient;

@RestController
@RequestMapping("/community")
public class CommunityController {

    @PostMapping("/add")
    public ResponseEntity<String> addUser(@RequestParam("name") String name){
        String addedCommunity = "";
        Community community = new Community();
        try {
            AsyncDocumentClient client = getDocumentClient();
            String UsersCollection = getCollectionString("Community");

            community.setName(name);
            Observable<ResourceResponse<Document>> resp = client.createDocument(UsersCollection, community, null, false);
            String str =  resp.toBlocking().first().getResource().getId();
            community.setId(str);

            FeedOptions queryOptions = new FeedOptions();
            queryOptions.setEnableCrossPartitionQuery(true);
            queryOptions.setMaxDegreeOfParallelism(-1);

        } catch( Exception e) {
            e.printStackTrace();
        }
        String contentType = "application/json";

        Gson g = new Gson();
        addedCommunity = g.toJson(community);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(addedCommunity);
    }
}
