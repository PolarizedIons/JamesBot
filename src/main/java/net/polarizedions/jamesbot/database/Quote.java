package net.polarizedions.jamesbot.database;

import org.bson.types.ObjectId;

import java.util.Date;

public class Quote {
    public ObjectId _id;
    public String nick;
    public int quoteNum;
    public String message;
    public Date date;

    public Quote() {

    }

    public Quote(String nick, int quoteNum, String message, Date date) {
        this.nick = nick;
        this.quoteNum = quoteNum;
        this.message = message;
        this.date = date;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getQuoteNum() {
        return quoteNum;
    }

    public void setQuoteNum(int quoteNum) {
        this.quoteNum = quoteNum;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
