package in.adarshr.filehelper;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ArgumentParser class
 * Process the input arguments
 */
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
    private boolean createFolder;
    private boolean rename;

    /**
     * Parse the input arguments
     * @param args arguments
     */
    public void parseArguments(String[] args) {
        Options options = getOptions();

        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = commandLineParser.parse(options, args);
            if (cmd.hasOption("c")) {
                setCreateFolder(true);
            }
            if (cmd.hasOption("p")) {
                setFolder(cmd.getOptionValue("p"));
            }
            if (cmd.hasOption("f")) {
                setForce(true);
            }
            if (cmd.hasOption("m")) {
                setMove(true);
            }
            if (cmd.hasOption("r")) {
                setRename(true);
            }
            if (cmd.hasOption("ex")) {
                setExtToIgnore(cmd.getOptionValue("ex"));
            }
            if (cmd.hasOption("ei")) {
                setExtToInclude(cmd.getOptionValue("ei"));
            }
            if (cmd.hasOption("h")) {
                createHelp(options);
                System.exit(0);
            }
        } catch (ParseException e) {
            logger.error("Invalid option");
            createHelp(options);
            System.exit(0);
        }
    }

    /**
     * Get the options
     * @return Options
     */
    private static Options getOptions() {
        Options options = new Options();
        options.addOption("c", "create", false, "Create folder");
        options.addOption("p", "path", true, "Path to folder");
        options.addOption("f", "force", false, "Force the operation");
        options.addOption("h", "help", false, "Help");
        options.addOption("m", "move", false, "Move the file instead of copying");
        options.addOption("r", "rename", false, "Rename the file");
        options.addOption("ei", "extToInclude", true, "Extension to include");
        options.addOption("ex", "extToIgnore", true, "Extension to exclude");
        return options;
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
  
    public boolean isCreateFolder() {
        return createFolder;
    }

    public void setCreateFolder(boolean createFolder) {
        this.createFolder = createFolder;
    }
    public boolean getRename() {
        return rename;
    }
    public void setRename(boolean rename) {
        this.rename = rename;
    }

    /**
     * Operation to be performed
     * @return String
     */
    public String operationToPerform() {
        StringBuilder stringBuilder = new StringBuilder();
        if (isCreateFolder()) {
            stringBuilder.append("Create folder(s)").append("\n");
            if(getRename()){
                stringBuilder.append("with files renamed").append("\n");
            }
            if(isMove()){
                stringBuilder.append("and move").append("\n");
            }
        } else if (getRename()) {
            stringBuilder.append("Rename").append("\n");
        }
        return stringBuilder.toString();
    }
}
