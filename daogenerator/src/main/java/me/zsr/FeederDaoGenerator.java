package me.zsr;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class FeederDaoGenerator {

    public static void main(String[] args) throws Exception {
        // note : +1 after upgrade schema
        Schema schema = new Schema(1, "me.zsr.feeder.dao");

        addFeed(schema);
        addRSSItem(schema);

        new DaoGenerator().generateAll(schema, "app/src/main/java");
    }

    private static void addFeed(Schema schema) {
        Entity music = schema.addEntity("Feed");
        music.setHasKeepSections(true);
        music.addStringProperty("title");
        music.addStringProperty("pub_date");
        music.addStringProperty("description");
        music.addStringProperty("content");
    }

    private static void addRSSItem(Schema schema) {
        Entity music = schema.addEntity("RSSItem");
        music.setHasKeepSections(true);
        music.addStringProperty("title");
        music.addStringProperty("link");
        music.addStringProperty("description");
        music.addStringProperty("state");
    }
}
