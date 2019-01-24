package net.polarizedions.jamesbot.reponders;

import net.polarizedions.jamesbot.core.Bot;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponderDice implements IResponder {
    private static final Pattern DICE_PATTERN = Pattern.compile("^([0-9]+)?d([0-9]+)$", Pattern.CASE_INSENSITIVE);
    private static final Random RANDOM = new Random();
    private static final int MAX_NUMBER_SMALL_ROLL = 50;

    @Override
    public boolean run(MessageEvent msg) {
        String message = msg.getMessage();
        String prefix = Bot.instance.getBotConfig().commandPrefix;

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
                e.printStackTrace();/* NOOP */
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

    private long roll(int size, int number) {
        if (number > MAX_NUMBER_SMALL_ROLL) {
            return big_roll(size, number);
        }
        
        long total = 0;

        for (int i = 0; i < number; i++) {
            total += RANDOM.nextInt(size) + 1;
        }

        return total;
    }
    
    private long big_roll(long size, long number) {
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
        return Math.sqrt((sides * size - 1) * number / 12d);
    }
    
    private boolean isHugeRoll(int size, int number) {
        return Integer.MAX_LONG / number < size;
    }
}
