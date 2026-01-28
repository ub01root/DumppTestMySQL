package org.happydev.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.sql.*;

public class DumpService {

    public static void run(TestDumper plugin) throws Exception {

        // Config
        String host = plugin.getConfig().getString("database.host");
        int port = plugin.getConfig().getInt("database.port");
        String database = plugin.getConfig().getString("database.name");
        String user = plugin.getConfig().getString("database.user");
        String password = plugin.getConfig().getString("database.password");
        int limit = plugin.getConfig().getInt("dump.max-rows-per-table");

        // JDBC URL
        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useSSL=false&allowPublicKeyRetrieval=true";

        // Output file
        File outFile = new File(plugin.getDataFolder(),
                plugin.getConfig().getString("dump.file"));

        plugin.getDataFolder().mkdirs();

        JsonObject root = new JsonObject();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {

            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");

                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(
                        "SELECT * FROM `" + tableName + "` LIMIT " + limit
                );

                ResultSetMetaData rsMeta = rs.getMetaData();
                int columnCount = rsMeta.getColumnCount();

                JsonArray rows = new JsonArray();

                while (rs.next()) {
                    JsonObject row = new JsonObject();
                    for (int i = 1; i <= columnCount; i++) {
                        Object value = rs.getObject(i);
                        row.addProperty(
                                rsMeta.getColumnName(i),
                                value == null ? null : value.toString()
                        );
                    }
                    rows.add(row);
                }

                root.add(tableName, rows);
            }
        }

        // Write JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(outFile)) {
            gson.toJson(root, writer);
        }

        plugin.getLogger().info("Dump generado correctamente en: " + outFile.getAbsolutePath());
    }
}
