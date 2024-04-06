import java.io.*;
import java.nio.file.Files;
import java.security.SecureRandom;

public class Library {

    private static byte[] randhex(int length) {
        byte[] bytes = new byte[length / 2];
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length / 2; i++) {
            int r = Math.abs(random.nextInt()) % ((1 << 16) * 2);
            bytes[i] = (byte) (r >> (4 * (length / 2 - i - 1)));
        }
        return bytes;
    }

    public static void main(String[] args) throws IOException {
        // Check if arguments are provided
        if (args == null || args.length < 1) {
            System.out.println("Usage: java Library <params>");
            return;
        }

        String fileName = args[0];
        File file = new File(fileName);

        // Create cache if it doesn't exist or clear existing content
        if (!file.exists()) {
            file.createNewFile();
        } else {
            Files.deleteIfExists(file.toPath());
            file.createNewFile();
        }

        try (Writer writer = new BufferedWriter(new FileWriter(file))) {
            int hexCount = 5000;
            byte[] randomBytes = randhex((hexCount * 2));
            FileContent newFileContent = new FileContent("cache", randomBytes);
            writer.write("Writing JVM cache to file...");
            writer.write("\n");
            writer.write(String.format("%s:%s\n", newFileContent.getName(), Bytes.toStringHex(newFileContent.getContent())));
            for (int i = 0; i < hexCount - 1; i++) {
                randomBytes = randhex((2 * 32)); // Assuming each entry requires 32 bytes
                FileContent fileEntry = new FileContent("entry_" + i, randomBytes);
                writer.write(String.format("%s:%s\n", fileEntry.getName(), Bytes.toStringHex(fileEntry.getContent())));
            }
        } catch (IOException e) {
            System.err.println("Error while writing to file: " + e.getMessage());
        }

        System.out.println("Cache file created or updated: " + file.getAbsolutePath());
    }

    static class FileContent {
        private final String name;
        private final byte[] content;

        public FileContent(String name, byte[] content) {
            this.name = name;
            this.content = content;
        }

        public String getName() {
            return name;
        }

        public byte[] getContent() {
            return content;
        }
    }
}
