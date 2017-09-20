package controllers;

import api.CreateReceiptRequest;
import api.ReceiptResponse;
import dao.ReceiptDao;
import generated.tables.records.ReceiptsRecord;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import dao.TagDao;
import generated.tables.records.TagsRecord;


import static java.util.stream.Collectors.toList;

@Path("/receipts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReceiptController {
    final ReceiptDao receipts;
    final TagDao tags;

    //public ReceiptController(ReceiptDao receipts) {
    public ReceiptController(ReceiptDao receipts, TagDao tags) {
        this.receipts = receipts;
        this.tags = tags;
    }

    @POST
    public int createReceipt(@Valid @NotNull CreateReceiptRequest receipt) {
        return receipts.insert(receipt.merchant, receipt.amount);
    }

    @GET
    public List<ReceiptResponse> getReceipts() {
        List<ReceiptsRecord> receiptRecords = receipts.getAllReceipts();
        //return receiptRecords.stream().map(ReceiptResponse::new).collect(toList());
        List<TagsRecord> tagsRecords = tags.getAllTags();
        return receiptRecords.stream().map((receipt) -> new ReceiptResponse(receipt, tagsRecords.stream().filter((tag) -> tag.getId() == receipt.getId()).map((tag) -> tag.getTag()).collect(toList()))).collect(toList());
      }
  }

//     }



// }
