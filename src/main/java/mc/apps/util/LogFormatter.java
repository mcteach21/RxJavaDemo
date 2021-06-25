package mc.apps.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class LogFormatter extends Formatter {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    DateFormat df = new SimpleDateFormat("dd-Mm HH:mm"); //yyyy-MM-dd'T'

    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        sb.append(ANSI_YELLOW);
        sb.append(df.format(new Date(record.getMillis())))
                .append(ANSI_RED)
                .append(" "+record.getSourceClassName())
                .append(ANSI_YELLOW)
                .append(" ["+record.getLevel().getLocalizedName()+"] : ")
                .append(ANSI_WHITE)
                .append(formatMessage(record)+"\n");

        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
                // ignore
            }
        }

        return sb.toString();
    }
}