package in.adarshr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class ArgumentParser {
    Logger logger = LoggerFactory.getLogger(ArgumentParser.class);
    public ArgumentParser(String[] arguments) {
        parseArguments(arguments);
    }

    private boolean isForce;
    private boolean isMove;
    private String folder;
    private String file;
    private String help;
    private String extToIgnore;

    public void parseArguments(String[] args) {
       // setExtToIgnore(".jar");
        if (args.length > 0) {
            String[] arguments = trimArguments(args);
            logger.info("Arguments are : " + Arrays.toString(arguments));
            int optionLength = arguments.length;

            Path path = Paths.get(args[0]);
            if (Files.isDirectory(path)) {
                setFolder(path.toString());
            } else if (Files.exists(path)) {
                setFile(path.toString());
                logger.info(args[0] + " is a file");
            } else {
                logger.info(args[0] + " does not exist");
            }

            for (int i = 1; i < optionLength; i++) {
                String arg = args[i].toLowerCase();
                switch (arg) {
                    case "-h" -> createHelp();
                    case "-f" -> setForce(true);
                    case "-m" -> setMove(true);
                    case "-ext" -> setExtToIgnore(null);
                }
            }
        } else {
            logger.info("No arguments provided");
        }

    }

    private String[] trimArguments(String[] args) {
        if (args == null) {
            return new String[0];
        }else{
            return Arrays.stream(args).map(String::trim).toArray(String[]::new);
        }
    }

    private void createHelp() {
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

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
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
}
