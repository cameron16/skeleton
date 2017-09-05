package dao;

import api.ReceiptResponse;

import generated.tables.records.ReceiptsRecord;
import generated.tables.records.TagsRecord;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static generated.Tables.TAGS;
import static generated.Tables.RECEIPTS;


public class TagDao {
    DSLContext dsl;

    public TagDao(Configuration jooqConfig) {
        this.dsl = DSL.using(jooqConfig);
    }

    public void insert(int id, String tag) {
        TagsRecord tagsRecord = dsl
                .insertInto(TAGS, TAGS.ID, TAGS.TAG)
                .values(id, tag)
                .returning(TAGS.ID)
                .fetchOne();
    }

    public boolean checkForTag(String givenTag, int givenId){
        //check if id has tag
        //return true if id does have the tag
        return dsl.select().from(TAGS).where(TAGS.TAG.eq(givenTag).and(TAGS.ID.eq(givenId))).fetch().isNotEmpty();
    }

    public void unToggleTag(String givenTag, int givenId){
        dsl.delete(TAGS).where(TAGS.TAG.eq(givenTag).and(TAGS.ID.eq(givenId))).execute();
    }

    public List<ReceiptsRecord> getMatchingReceipts(String givenTag) {
        return dsl.select(RECEIPTS.ID, RECEIPTS.AMOUNT, RECEIPTS.MERCHANT).from(RECEIPTS).join(TAGS).on(RECEIPTS.ID.eq(TAGS.ID)).where(TAGS.TAG.eq(givenTag)).fetchInto(ReceiptsRecord.class);

    }

}
