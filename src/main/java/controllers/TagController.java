package controllers;

import api.CreateReceiptRequest;
import api.ReceiptResponse;
import dao.ReceiptDao;
import generated.tables.records.ReceiptsRecord;


import api.CreateTagRequest;
import dao.TagDao;
import generated.tables.records.TagsRecord;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static java.util.stream.Collectors.toList;

//@Path("/tags/{tag}")
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TagController {
    final TagDao tags;

    public TagController(TagDao tags) {
        this.tags = tags;
    }

        //@Path("/tags/{tag}")

    @PUT
    @Path("/tags/{tag}")
    //public void toggleTag(@Valid @NotNull CreateTagRequest tag, @PathParam("tag") String tagName) {
    public void toggleTag(@Valid int id, @PathParam("tag") String tagName) {
        // <your code here
        boolean isTagThere;
        System.out.println("in the put block");
        isTagThere = tags.checkForTag(tagName, id);
        if (isTagThere){
            System.out.println("if statement");

            tags.unToggleTag(tagName, id);
            //tag is already there, so untoggle it
        }
        else{
            //tag is not already there, so insert
            System.out.println("else statement");
            tags.insert(id, tagName);
        }
    }

//    @Path("/tags/{tag}")


    // @GET
    // @Path("/tags/{tag}")
    // public List<Object> getReceipts(@PathParam("tag") String tagName) {
    //     List<Object> receiptRecords = tags.getMatchingReceipts(tagName);
    //     return receiptRecords.stream().map(Object::new).collect(toList());
    // }

    @GET
    @Path("/tags/{tag}")
    public List<ReceiptResponse> getReceipts(@PathParam("tag") String tagName) {
        List<ReceiptsRecord> receiptRecords = tags.getMatchingReceipts(tagName);
        return receiptRecords.stream().map(ReceiptResponse::new).collect(toList());
    }
}
