package in.adarshr.filehelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * FileRenamer class
 * Rename the file based on the rules defined in renameRules.properties file
 */
public class FileRenamer {
    //SLF4J Logger
    private static final Logger LOG = LoggerFactory.getLogger(FileRenamer.class);

    final private Properties renameRules;

    public FileRenamer() {
        this.renameRules = load();
    }

    /**
     * Load the renameRules.properties file
     * @return Properties
     */
    private Properties load() {
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream("config/renameRules.properties"));
            return prop;
        } catch (Exception e) {
            LOG.error("Error loading renameRules.properties file", e);
            return null;
        }
    }

    /**
     * Rename the file based on the rules defined in renameRules.properties file
     * @param fileName file name
     * @return String
     */
    public String rename(String fileName) {
        if (renameRules == null) {
            LOG.error("renameRules.properties file not found");
            return fileName;
        }
        String targetWords = renameRules.getProperty("targetWords");
        String replacementWords = renameRules.getProperty("replacementWords");

        //remove [ ] from the targetWords and replacementWords
        targetWords = targetWords.replaceAll("[\\[\\]]", "");
        replacementWords = replacementWords.replaceAll("[\\[\\]]", "");

        //target word is a list separated by comma, so we need to split it
        String[] targetWordsArray = targetWords.split(",");
        String[] replacementWordsArray = replacementWords.split(",");

        if (targetWordsArray.length != replacementWordsArray.length) {
            LOG.error("targetWords and replacementWords count mismatch");
            return fileName;
        }

        //Iterate through the targetWordsArray and replace each word with corresponding replacement word
        for (int i = 0; i < targetWordsArray.length; i++) {
            fileName = fileName.replaceAll(Pattern.quote(targetWordsArray[i]), replacementWordsArray[i]);
        }

        String suffix = renameRules.getProperty("suffix");
        fileName = fileName + suffix;

        String prefix = renameRules.getProperty("prefix");
        fileName = prefix + fileName;

        String changeCase = renameRules.getProperty("changeCase");
        switch (changeCase) {
            case "upper" -> fileName = fileName.toUpperCase();
            case "lower" -> fileName = fileName.toLowerCase();
            case "capitalize" -> fileName = Arrays.stream(fileName.split("\\s"))
                        .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                        .collect(Collectors.joining(" "));

        }

        String formatYear = renameRules.getProperty("formatYear");
        if (formatYear.equalsIgnoreCase("true")) {
            String dateFormat = renameRules.getProperty("yearFormat");
            String yearBetween = renameRules.getProperty("yearBetween");
            //Identify if the file name contains a year and is between the yearsBetween
            if (fileName.matches(".*\\d{4}.*")) {
                //Extract the year from the file name
                String year = fileName.replaceAll(".*?(\\d{4}).*", "$1");
                //Check if the year is between the yearsBetween
                if (Integer.parseInt(year) >= Integer.parseInt(yearBetween.split("-")[0]) && Integer.parseInt(year) <= Integer.parseInt(yearBetween.split("-")[1])) {
                    // Attempt to parse year
                    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy");
                    java.time.temporal.TemporalAccessor temporalAccessor = formatter.parse(year);

                    if (temporalAccessor.isSupported(java.time.temporal.ChronoField.DAY_OF_YEAR)) {
                        //Format the year
                        fileName = fileName.replaceAll("\\d{4}", java.time.LocalDate.from(temporalAccessor).format(java.time.format.DateTimeFormatter.ofPattern(dateFormat)));
                    }
                }
            }
        }

        String trimSpaces = renameRules.getProperty("trimSpaces");
        if (trimSpaces.equalsIgnoreCase("true")) {
            fileName = fileName.trim();
        }

        String appendDate = renameRules.getProperty("appendDate");
        if (appendDate.equalsIgnoreCase("true")) {
            String dateFormat = renameRules.getProperty("dateFormat");
            fileName = fileName + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern(dateFormat));
        }
        return fileName;
    }

}