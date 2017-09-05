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
        System.out.println("it is inserting on put");
        System.out.println("this is id: "+ Integer.toString(id));
        System.out.println("this is tag: " + tag);
        System.out.println(TAGS);

        TagsRecord tagsRecord = dsl
                .insertInto(TAGS, TAGS.ID, TAGS.TAG)
                .values(id, tag)
                .returning(TAGS.ID)
                .fetchOne();

        System.out.println(TAGS);
        System.out.println(tagsRecord);

    }

    public boolean checkForTag(String givenTag, int givenId){
        //check if id has tag
        //return true if id does have the tag
        return dsl.select().from(TAGS).where(TAGS.TAG.eq(givenTag).and(TAGS.ID.eq(givenId))).fetch().isNotEmpty();
    }

    public void unToggleTag(String givenTag, int givenId){
        System.out.println("untoggling");
        //dsl.delete(TAGS).where(TAGS.TAG.eq(givenTag).and(TAGS.ID.eq(givenId)));
        dsl.delete(TAGS).where(TAGS.TAG.eq(givenTag).and(TAGS.ID.eq(givenId))).execute();
    }


    //public List<ReceiptsRecord> getMatchingReceipts(String givenTag) {
    public List<ReceiptsRecord> getMatchingReceipts(String givenTag) {

        // org.jooq.Result <org.jooq.Record3<java.lang.Integer,java.lang.String,java.math.BigDecimal>> temp = dsl.select(RECEIPTS.ID, RECEIPTS.MERCHANT, RECEIPTS.AMOUNT).from(TAGS).join(RECEIPTS).on(TAGS.ID.eq(RECEIPTS.ID)).where(TAGS.TAG.eq(currentTag)).fetch();
        // return dsl.selectFrom((org.jooq.Table)temp).fetch();
        

        // List m = dsl.select.join.(TAGS).on(TAGS.ID.eq(RECEIPTS.ID)).where(TAGS.TAG.eq(givenTag)).fetchInto();

        // return dsl.selectFrom(RECEIPTS).fetch();


       // return dsl.selectFrom(RECEIPTS).join(TAGS).on(TAGS.ID.eq(RECEIPTS.ID)).where(TAGS.TAG.eq(givenTag)).fetchInto();
        return dsl.select(RECEIPTS.ID, RECEIPTS.AMOUNT, RECEIPTS.MERCHANT).from(RECEIPTS).join(TAGS).on(RECEIPTS.ID.eq(TAGS.ID)).where(TAGS.TAG.eq(givenTag)).fetchInto(ReceiptsRecord.class);

        //dsl.select..join..where.fetchInto(RecordClass.class)


        //return dsl.selectFrom(RECEIPTS.ID, RECEIPTS.MERCHANT,RECEIPTS.AMOUNT).from(TAGS).join(RECEIPTS).on(TAGS.ID.eq(RECEIPTS.ID)).where(TAGS.TAG.eq(givenTag)).fetch();

    }






}
