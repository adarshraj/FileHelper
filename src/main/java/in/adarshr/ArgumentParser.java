package in.adarshr;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArgumentParser {
    Logger logger = LoggerFactory.getLogger(ArgumentParser.class);
    public ArgumentParser(String[] arguments) {
        parseArguments(arguments);
    }

    private boolean isForce;
    private boolean isMove;
    private String folder;
    private String file;
    private String extToIgnore;
    private String extToInclude;

    public void parseArguments(String[] args) {
        Options options = new Options();
        options.addOption("p", "path", true, "Path to folder");
        options.addOption("f", "force", false, "Force the operation");
        options.addOption("h", "help", false, "Help");
        options.addOption("m", "move", false, "Move the file instead of copying");
        options.addOption("ei", "extToInclude", true, "Extension to include");
        options.addOption("ex", "extToIgnore", true, "Extension to exclude");

        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = commandLineParser.parse(options, args);
            if (cmd.hasOption("p")) {
                setFolder(cmd.getOptionValue("p"));
            }
            if (cmd.hasOption("f")) {
                setForce(true);
            }
            if (cmd.hasOption("m")) {
                setMove(true);
            }
            if (cmd.hasOption("ex")) {
                setExtToIgnore(cmd.getOptionValue("ex"));
            }
            if (cmd.hasOption("ei")) {
                setExtToInclude(cmd.getOptionValue("ei"));
            }
            if (cmd.hasOption("h")) {
                createHelp(options);
            }
        } catch (ParseException e) {
            logger.error("Invalid option");
            createHelp(options);
            System.exit(0);
        }
    }

    public boolean isForce() {
        return isForce;
    }

    public void setForce(boolean force) {
        isForce = force;
    }

    public boolean isMove() {
        return isMove;
    }

    public void setMove(boolean move) {
        isMove = move;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void createHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("FileHelper", options);
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getExtToIgnore() {
        return extToIgnore;
    }

    public void setExtToIgnore(String extToIgnore) {
        this.extToIgnore = extToIgnore;
    }

    public String getExtToInclude() {
        return extToInclude;
    }

    public void setExtToInclude(String extToInclude) {
        this.extToInclude = extToInclude;
    }

}
