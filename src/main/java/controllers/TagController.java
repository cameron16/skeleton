package controllers;

import api.CreateReceiptRequest;
import api.ReceiptResponse;
import dao.ReceiptDao;
import generated.tables.records.ReceiptsRecord;

import dao.TagDao;
import generated.tables.records.TagsRecord;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TagController {
    final TagDao tags;

    public TagController(TagDao tags) {
        this.tags = tags;
    }

    @PUT
    @Path("/tags/{tag}")
    public void toggleTag(@Valid int id, @PathParam("tag") String tagName) {
        boolean isTagThere;
        isTagThere = tags.checkForTag(tagName, id);
        if (isTagThere){
            //tag is already there, so untoggle it
            tags.unToggleTag(tagName, id);
        }
        else{
            //tag is not already there, so insert
            tags.insert(id, tagName);
        }
    }

    @GET
    @Path("/tags/{tag}")
    public List<ReceiptResponse> getReceipts(@PathParam("tag") String tagName) {
        List<ReceiptsRecord> receiptRecords = tags.getMatchingReceipts(tagName);
        //return receiptRecords.stream().map(ReceiptResponse::new).collect(toList());
        List<TagsRecord> tagsRecords = tags.getAllTags();
        return receiptRecords.stream().map((receipt) -> new ReceiptResponse(receipt, tagsRecords.stream().filter((tag) -> tag.getId() == receipt.getId()).map((tag) -> tag.getTag()).collect(toList()))).collect(toList());
      }
  } 

//     }
// }
