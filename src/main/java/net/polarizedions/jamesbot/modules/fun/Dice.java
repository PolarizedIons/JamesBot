package net.polarizedions.jamesbot.modules.fun;

import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.modules.Module;
import net.polarizedions.jamesbot.responders.IResponder;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dice extends Module implements IResponder {
    private static final Pattern DICE_PATTERN = Pattern.compile("^([0-9]+)?d([0-9]+)$", Pattern.CASE_INSENSITIVE);
    private static final int MAX_NUMBER_SMALL_ROLL = 50;
    static final Random RANDOM = new Random();

    public Dice(Bot bot) {
        super(bot);
    }

    @Override
    public boolean run(MessageEvent msg) {
        String message = msg.getMessage();
        String prefix = this.bot.getBotConfig().commandPrefix;

        if (!message.startsWith(prefix)) {
            return false;
        }

        message = message.substring(prefix.length());
        Matcher matcher = DICE_PATTERN.matcher(message);
        if (matcher.matches()) {
            int number = 1;

            try {
                number = Integer.parseInt(matcher.group(1));
            }
            catch (Exception e) {
                /* NOOP */
            }
            int size = Integer.parseInt(matcher.group(2));

            if (size <= 0 || number <= 0 || isHugeRoll(size, number)) {
                msg.respondWith("And what do you expect to happen???");
            }
            else {
                msg.respondWith("Rolled " + number + " d" + size + " dice and got " + roll(size, number) + ".");
            }

            return true;
        }

        return false;
    }

    long roll(int size, int number) {
        if (number > MAX_NUMBER_SMALL_ROLL) {
            return big_roll(size, number);
        }
        
        long total = 0;

        for (int i = 0; i < number; i++) {
            total += RANDOM.nextInt(size) + 1;
        }

        return total;
    }
    
    long big_roll(long size, long number) {
        long roll = (long) (RANDOM.nextGaussian() * standardDeviation(size, number) + mean(size, number));
        
        if (roll < number) {
            return number;
        }
        else if (roll > size * number) {
            return size * number;
        }
        
        return roll;
    }
    
    private double mean(long size, long number) {
        return (size + 1) * number / 2d;
    }
    
    private double standardDeviation(long size, long number) {
        return Math.sqrt((size * size - 1) * number / 12d);
    }
    
    private boolean isHugeRoll(int size, int number) {
        return Long.MAX_VALUE / number < size;
    }

    @Override
    public String getModuleName() {
        return "dice";
    }
}
