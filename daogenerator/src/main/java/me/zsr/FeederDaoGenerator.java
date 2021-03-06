package me.zsr;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class FeederDaoGenerator {

    public static void main(String[] args) throws Exception {
        // note : +1 after upgrade schema
        Schema schema = new Schema(20, "me.zsr.feeder.dao");

        addFeed(schema);

        new DaoGenerator().generateAll(schema, "app/src/main/java");
    }

    private static void addFeed(Schema schema) {
        Entity feedAccount = schema.addEntity("FeedAccount");
        feedAccount.addIdProperty().autoincrement();
        feedAccount.addStringProperty("name");
        feedAccount.addStringProperty("reserved");

        Entity feedSource = schema.addEntity("FeedSource");
        feedSource.setHasKeepSections(true);
        feedSource.addIdProperty().autoincrement();
        feedSource.addStringProperty("title").notNull();
        feedSource.addStringProperty("url");
        feedSource.addDateProperty("date");
        feedSource.addStringProperty("link");
        feedSource.addStringProperty("favicon");
        feedSource.addStringProperty("description");
        feedSource.addStringProperty("reserved");
        Property feedAccountId = feedSource.addLongProperty("feedAccountId").notNull().getProperty();
        feedSource.addToOne(feedAccount, feedAccountId);
        ToMany accountToSource = feedAccount.addToMany(feedSource, feedAccountId);
        accountToSource.setName("feedSources");


        Entity feedItem = schema.addEntity("FeedItem");
        feedItem.setHasKeepSections(true);
        feedItem.addIdProperty().autoincrement();
        feedItem.addStringProperty("title").notNull();
        feedItem.addStringProperty("link");
        feedItem.addStringProperty("description");
        feedItem.addBooleanProperty("read");
        feedItem.addBooleanProperty("trash");
        feedItem.addStringProperty("content");
        feedItem.addDateProperty("lastShownDate");
        feedItem.addStringProperty("reserved");
        Property feedItemDate = feedItem.addDateProperty("date").getProperty();
        Property feedSourceId = feedItem.addLongProperty("feedSourceId").notNull().getProperty();
        feedItem.addToOne(feedSource, feedSourceId);
        ToMany sourceToItem = feedSource.addToMany(feedItem, feedSourceId);
        sourceToItem.setName("feedItems");
        sourceToItem.orderDesc(feedItemDate);
    }
}
