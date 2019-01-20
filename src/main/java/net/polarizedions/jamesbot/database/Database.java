package net.polarizedions.jamesbot.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.polarizedions.jamesbot.config.DatabaseConfig;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Database {
    MongoClient client;
    MongoDatabase database;
    public Database(DatabaseConfig config) {
        this.client = MongoClients.create(new ConnectionString(this.buildConnectionString(config)));
        this.database = this.client.getDatabase(config.database);

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        database = database.withCodecRegistry(pojoCodecRegistry);
    }

    public MongoCollection<ButtcoinAccount> getButtcoinCollection() {
        return this.database.getCollection("buttcoins", ButtcoinAccount.class);
    }

    public MongoCollection<Quote> getQuoteCollection() {
        return this.database.getCollection("quotes", Quote.class);
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
