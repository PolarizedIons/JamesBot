package net.polarizedions.jamesbot.database;

import com.mongodb.client.MongoCollection;
import net.polarizedions.jamesbot.config.ConfigurationLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ImportJanebot {
    public static void main(String[] args) throws IOException, ParseException {
        System.out.println("Connecting to database");
        ConfigurationLoader loader = new ConfigurationLoader();
        loader.load();
        Database database = new Database(loader.getBotConfig().databaseConfig);

        System.out.println("Importing from ./import ...");
        HashMap<String, String> buttcoinAbuse = new HashMap<>();
        HashMap<String, String> buttcoinActive = new HashMap<>();
        HashMap<String, String> buttcoinGifted = new HashMap<>();
        HashMap<String, String> buttcoinReveived = new HashMap<>();
        HashMap<String, String> buttcoinMined = new HashMap<>();

        HashMap<String, String> quoteDates = new HashMap<>();
        HashMap<String, String> quoteMessages = new HashMap<>();

        loadFile(buttcoinAbuse, "buttcoin:stats:abuse.txt");
        loadFile(buttcoinActive, "buttcoin:stats:active.txt");
        loadFile(buttcoinGifted, "buttcoin:stats:gifted.txt");
        loadFile(buttcoinReveived, "buttcoin:stats:received.txt");
        loadFile(buttcoinMined, "buttcoin:stats:mined.txt");


        loadFile(quoteDates, "quote:dates.txt");
        loadFile(quoteMessages, "quote:messages.txt");

        importButtcoinAccount(database, buttcoinAbuse, buttcoinActive, buttcoinGifted, buttcoinMined, buttcoinReveived);
        importQuotes(database, quoteDates, quoteMessages);
    }

    private static void importQuotes(Database database, HashMap<String, String> quoteDates, HashMap<String, String> quoteMessages) throws ParseException {
        MongoCollection<Quote> coll = database.getQuoteCollection();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Map.Entry<String, String> quoteEntry : quoteMessages.entrySet()) {
            String nickStr[] = quoteEntry.getKey().split("#");
            String nick = nickStr[0];
            int count = Integer.parseInt(nickStr[1]);
            String message = quoteEntry.getValue();
            String dateStr = quoteDates.getOrDefault(nick, "");
            Date date = dateStr.isEmpty() ? null : dateFormat.parse(dateStr);

            if (message.isEmpty()) {
                continue;
            }

            Quote quote = new Quote();
            quote.setNick(nick);
            quote.setMessage(message);
            quote.setDate(date);
            quote.setQuoteNum(count);

            coll.insertOne(quote);
        }
    }

    private static void importButtcoinAccount(Database database, HashMap<String, String> buttcoinAbuse, HashMap<String, String> buttcoinActive, HashMap<String, String> buttcoinGifted, HashMap<String, String> buttcoinMined, HashMap<String, String> buttcoinReveived) {
        MongoCollection<ButtcoinAccount> coll = database.getButtcoinCollection();
        for (Map.Entry<String, String> minedEntry : buttcoinMined.entrySet()) {
            String nick = minedEntry.getKey();
            int mined = Integer.parseInt(minedEntry.getValue());
            int abuse = Integer.parseInt(buttcoinAbuse.getOrDefault(nick, "0"));
            boolean active = buttcoinActive.getOrDefault(nick, "0").equals("1");
            int gifted = Integer.parseInt(buttcoinGifted.getOrDefault(nick, "0"));
            int received = Integer.parseInt(buttcoinReveived.getOrDefault(nick, "0"));


            ButtcoinAccount account = new ButtcoinAccount();
            account.setName(nick);
            account.setMined(mined);
            account.setBruteforced(abuse);
            account.setActive(active);
            account.setGifted(gifted);
            account.setGiven(received);
            account.setBalance(mined - gifted + received);

            coll.insertOne(account);
        }
    }

    private static void loadFile(HashMap<String, String> map, String filename) throws FileNotFoundException {
        Scanner sc = new Scanner(new File("./import/" + filename));
        while (sc.hasNextLine()) {
            String line[] = sc.nextLine().split("=");
            map.put(line[0].trim(), line[1].trim().substring(1, line[1].length() - 2).trim());
        }
    }
}
