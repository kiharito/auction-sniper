package test.endtoend.auctionsniper;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matcher;

import java.io.File;
import java.io.IOException;
import java.util.logging.LogManager;

import static org.hamcrest.MatcherAssert.assertThat;

public class AuctionLogDriver {
    public static String LOG_FILE_NAME = "auction-sniper.log";
    private final File logFIle = new File(LOG_FILE_NAME);

    public void hasEntry(Matcher<String> matcher) throws IOException {
        assertThat(FileUtils.readFileToString(logFIle, "UTF-8"), matcher);
    }

    public void clearLog() {
        logFIle.delete();
        LogManager.getLogManager().reset();
    }
}
