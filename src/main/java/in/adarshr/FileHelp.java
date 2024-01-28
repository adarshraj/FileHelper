package in.adarshr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileHelp {
    Logger logger = LoggerFactory.getLogger(FileHelp.class);

    public static void main(String... arguments) {
        ArgumentParser argumentParser = new ArgumentParser(arguments);
        Path directory;
        List<Path> fileList = new ArrayList<>();
        if(argumentParser.getFolder() == null) {
            String currentDirectory = System.getProperty("user.dir");
            directory = Paths.get(currentDirectory);
        }else {
            directory = Paths.get("C:\\temp");
        }

        new FileHelp().begin(directory, fileList, argumentParser);
    }

    private void begin(Path directory, List<Path> fileList, ArgumentParser arguments) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    logger.info(path + " is a directory, ignoring");
                } else {
                    logger.info(path + " is a file, adding to the list");
                    fileList.add(path);
                }
            }
        } catch (IOException | DirectoryIteratorException x) {
            logger.error(x.toString());
        }

        logger.info("Folders to be created for");
        for (Path file : fileList) {
            logger.info(file.toString());
        }
        new FileHelp().createDirectory(fileList, arguments);
    }

    private void createDirectory(List<Path> fileList, ArgumentParser arguments){
        for (Path fileWithNoFolder : fileList) {
            try {
                //Just the fileWithNoFolder name with extension
                Path fileNameWithExtension = fileWithNoFolder.getFileName();

                if(fileNameWithExtension.endsWith(".jar")){
                    continue;
                }

                //Get filename without extension
                String fileNameWithoutExtension = fileNameWithExtension.toString().substring(0, fileNameWithExtension.toString().lastIndexOf("."));

                // Combine parent path with fileNameWithoutExtension. This is required to create folder.
                Path newPathWithoutExtension = fileWithNoFolder.getParent().resolve(fileNameWithoutExtension);

                logger.info("File name without extension is " + newPathWithoutExtension);

                if (!Files.exists(newPathWithoutExtension)) {
                    // Use the newPath to create directories
                    Files.createDirectories(newPathWithoutExtension);
                }else{
                    if(arguments.isForce()){
                        Files.createDirectories(newPathWithoutExtension);
                        logger.info("Force created folder: " + newPathWithoutExtension);
                    }
                    logger.info("Folder already exists: " + newPathWithoutExtension);
                }

                if(arguments.isMove()) {
                    // Define the target path
                    Path newlyCreatedFolder = newPathWithoutExtension.resolve(fileNameWithExtension);

                    // Move the fileWithNoFolder
                    Files.move(fileWithNoFolder, newlyCreatedFolder, StandardCopyOption.REPLACE_EXISTING);

                    logger.info("File moved to : " + newlyCreatedFolder);
                }
            } catch (IOException e) {
                logger.error(e.toString());
            }
        }
    }
}