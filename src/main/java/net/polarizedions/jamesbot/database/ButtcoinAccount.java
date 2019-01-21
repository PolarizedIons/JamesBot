package net.polarizedions.jamesbot.database;

import org.bson.types.ObjectId;

public class ButtcoinAccount {
    public ObjectId _id;
    public String name;
    public boolean active;
    public int balance;
    public int mined;
    public int bruteforced;
    public int gifted;
    public int given;

    public ButtcoinAccount() {

    }

    public ButtcoinAccount(String name) {
        this(name, false, 0, 0, 0, 0, 0);
    }

    public ButtcoinAccount(String name, boolean active, int balance, int mined, int bruteforced, int gifted, int given) {
        this.name = name;
        this.active = active;
        this.balance = balance;
        this.mined = mined;
        this.bruteforced = bruteforced;
        this.gifted = gifted;
        this.given = given;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getMined() {
        return mined;
    }

    public void setMined(int mined) {
        this.mined = mined;
    }

    public int getBruteforced() {
        return bruteforced;
    }

    public void setBruteforced(int bruteforced) {
        this.bruteforced = bruteforced;
    }

    public int getGifted() {
        return gifted;
    }

    public void setGifted(int gifted) {
        this.gifted = gifted;
    }

    public int getGiven() {
        return given;
    }

    public void setGiven(int given) {
        this.given = given;
    }
}
