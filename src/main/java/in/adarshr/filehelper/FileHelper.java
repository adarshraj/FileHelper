package in.adarshr.filehelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * FileHelp class
 */
public class FileHelper {
    static Logger logger = LoggerFactory.getLogger(FileHelper.class);

    /**
     * Main method
     * @param arguments arguments
     */
    public static void main(String... arguments) {
        ArgumentParser argumentParser = new ArgumentParser(arguments);
        Path directory;
        List<Path> fileList = new ArrayList<>();
        logger.info("------------- " + LocalDateTime.now() + " Start "+ " -------------");
        if(argumentParser.getFolder() == null) {
            String currentDirectory = System.getProperty("user.dir");
            directory = Paths.get(currentDirectory);
        }else {
            directory = Path.of(argumentParser.getFolder());
        }
        new FileHelper().begin(directory, fileList, argumentParser);
        logger.info("------------- " + LocalDateTime.now() + " End "+ " -------------");
    }

    /**
     * Begin the process
     * @param directory directory to be processed
     * @param fileList list of files
     * @param arguments argumentParser
     */
    private void begin(Path directory, List<Path> fileList, ArgumentParser arguments) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            if(checkForOperations(arguments)) {
                logger.info("Operation to be performed: " + arguments.operationToPerform());
                for (Path path : stream) {
                    if (Files.isDirectory(path)) {
                        logger.info(path + " is a directory, ignoring");
                    } else {
                        String fileExtension = getFileExtension(path);
                        if ((arguments.getExtToIgnore() != null && arguments.getExtToIgnore().contains(fileExtension))
                                || checkForThisFile(path)) {
                            logger.info(path + " has ignored extension, skipping");
                        } else {
                            if (arguments.getExtToInclude() != null && arguments.getExtToInclude().contains(fileExtension)) {
                                logger.info(path + " file adding to the list");
                                fileList.add(path);
                            } else {
                                logger.info(path + " is a file, adding to the list");
                                fileList.add(path);
                            }
                        }
                    }
                }
            }else{
                logger.info("No operation to be performed");
            }
        } catch (IOException | DirectoryIteratorException x) {
            logger.error(x.toString());
        }

        if(!fileList.isEmpty()) {
            if(arguments.isCreateFolder()) {
                logger.info("Folders to be created for");
                for (Path file : fileList) {
                    logger.info(file.toString());
                }
                new FileHelper().createDirectory(fileList, arguments);
            } else if (arguments.getRename()) {
                logger.info("Files to be renamed");
                for (Path file : fileList) {
                    renameFile(file);
                }
            }else{
                logger.info("Nothing to do");
            }
        }else{
            logger.info("Nothing to do");
        }
    }

    /**
     * Check for operations
     * @param arguments argumentParser
     * @return true if any operation is to be performed
     */
    private boolean checkForOperations(ArgumentParser arguments) {
        return arguments.isCreateFolder() || arguments.getRename();
    }

    /**
     * Get the file extension
     * @param path  path of the file
     * @return  file extension
     */
    private String getFileExtension(Path path) {
        String fileNameWithExtension = path.getFileName().toString();
        return fileNameWithExtension.substring(fileNameWithExtension.lastIndexOf(".") + 1);
    }

    /**
     * Check for the file extension
     * @param path  path of the file
     * @return  true if the file is to be ignored
     */
    private boolean checkForThisFile(Path path) {
        return path.toString().endsWith(".jar")|| path.toString().contains("config");
    }

    /**
     * Create a new directory and move the file to the newly created directory
     * @param fileList list of files
     * @param arguments argumentParser
     */
    private void createDirectory(List<Path> fileList, ArgumentParser arguments){
        for (Path fileWithNoFolder : fileList) {
            try {
                //Just the fileWithNoFolder name with extension
                Path fileNameWithExtension = fileWithNoFolder.getFileName();

                //Rename the file if required
                if(arguments.getRename()){
                    fileNameWithExtension = renameFile(fileWithNoFolder);
                }

                //Get filename without extension
                String fileNameWithoutExtension = fileNameWithExtension.toString().substring(0, fileNameWithExtension.toString().lastIndexOf("."));

                // Combine parent path with fileNameWithoutExtension. This is required to create folder.
                Path newPathWithoutExtension = fileWithNoFolder.getParent().resolve(fileNameWithoutExtension);

                //Create a new folder only if it's not existing
                if (!Files.exists(newPathWithoutExtension)) {
                    // Use the newPath to create directories
                    Files.createDirectories(newPathWithoutExtension);
                    logger.info("New folder created: " + newPathWithoutExtension);
                }else{
                    // If the folder already exists, create only if the flag is present
                    if(arguments.isForce()){
                        Files.createDirectories(newPathWithoutExtension);
                        logger.info("Force created folder: " + newPathWithoutExtension);
                    }
                    logger.info("Folder already exists: " + newPathWithoutExtension);
                }

                //Move the file to the newly created folder
                //TODO: The logic not working with folder creation. Need to fix it.
                //TODO: The logic needs to be separated from the createDirectory method
                if(arguments.isMove()) {
                    // Define the target path
                    Path newlyCreatedFolder = newPathWithoutExtension.resolve(fileNameWithExtension);

                    // Move the fileWithNoFolder
                    Files.move(fileWithNoFolder, newlyCreatedFolder, StandardCopyOption.REPLACE_EXISTING);

                    logger.info("File moved to : " + newlyCreatedFolder);
                }
            } catch (IOException e) {
                logger.error("Exception while file handling: " + e);
            }
        }
    }

    /**
     * Rename the file
     * @param fileName file to be renamed
     * @return renamedFile
     */
    private Path renameFile(Path fileName) {
        Path fileNameWithExtension = fileName.getFileName();
        String fileNameWithoutExtension = fileNameWithExtension.toString().substring(0, fileNameWithExtension.toString().lastIndexOf("."));
        String fileExtension = fileNameWithExtension.toString().substring(fileNameWithExtension.toString().lastIndexOf("."));
        Path parent = fileName.getParent();

        FileRenamer fileRenamer = new FileRenamer();
        String renamed = fileRenamer.rename(fileNameWithoutExtension);
        Path renamedFile = parent.resolve(renamed + fileExtension);
        try {
            // Use Files.move to rename the source file to destination
            Files.move(fileName, renamedFile);
            logger.info("File renamed: " + renamed + fileExtension);
        } catch (IOException e) {
            logger.error("Failed to rename file: " + e);
        }
        return renamedFile;
    }
}