package net.polarizedions.jamesbot.database;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.polarizedions.jamesbot.config.DatabaseConfig;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

public class Database {
    MongoClient client;
    MongoDatabase database;
    public Database(DatabaseConfig config) {
        this.client = MongoClients.create(new ConnectionString(this.buildConnectionString(config)));
        this.database = this.client.getDatabase(config.database);
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        return this.database.getCollection(collectionName);
    }

    public void insert(String collectionName, Document document) {
        this.getCollection(collectionName).insertOne(document);
    }

    public Document findOne(String collectionName, Bson query) {
        return this.getCollection(collectionName).find(query).first();
    }

    @NotNull
    private String buildConnectionString(@NotNull DatabaseConfig config) {
        String connectionString = "mongodb://";
        if (! config.username.isEmpty()) {
            connectionString += config.username;

            if (! config.password.isEmpty()) {
                connectionString += ":" + config.password;
            }

            connectionString += "@";
        }

        return connectionString + config.host + ":" + config.port;
    }
}
