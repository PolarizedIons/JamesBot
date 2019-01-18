package net.polarizedions.jamesbot.database;

import com.mongodb.ConnectionString;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import net.polarizedions.jamesbot.config.DatabaseConfig;
import net.polarizedions.jamesbot.utils.Callback;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import static com.mongodb.client.model.Filters.eq;

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

    public void insert(String collectionName, Document document, Callback<Boolean> callback) {
        this.getCollection(collectionName).insertOne(document, (aVoid, throwable) -> {
            System.out.println("inserted!");

            callback.callback(throwable == null);
        });
    }

    public void findOne(String collectionName, Bson query, Callback<Document> callback) {
        this.getCollection(collectionName).find(query).first((document, throwable) -> {
            callback.callback(document);
        });

    }

    @NotNull
    private String buildConnectionString(DatabaseConfig config) {
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
