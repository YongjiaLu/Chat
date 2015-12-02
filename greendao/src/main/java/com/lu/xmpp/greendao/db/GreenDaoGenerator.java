package com.lu.xmpp.greendao.db;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class GreenDaoGenerator {
    private static final int DB_VERSION = 1;

    private static final String Default_Bean_Path = "com.lu.xmpp.bean";

    private static final String Default_Dao_Path = "com.lu.xmpp.dao";

    private static final String Main_Project_SRC_Dir = "app/src/main/java-gen";

    public static void main(String[] args) throws Exception {

        Schema schema = new Schema(DB_VERSION, Default_Bean_Path);

        schema.setDefaultJavaPackageDao(Default_Dao_Path);

        Entity entity = schema.addEntity("ChatLog");

        //where the message from
        entity.addStringProperty("from").notNull();
        //where the message to
        entity.addStringProperty("to").notNull();
        //when
        entity.addDateProperty("time");
        //isRead
        entity.addBooleanProperty("isRead");
        //message body
        entity.addStringProperty("body");

        new DaoGenerator().generateAll(schema, Main_Project_SRC_Dir);

    }
}
