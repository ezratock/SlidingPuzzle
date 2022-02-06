import java.util.Scanner;

public class WordWrap {
    public static String wrap(String rawText, int maxWidth, boolean splitWords) {
        Scanner iterator = new Scanner(rawText);
        String returnString = "";

        if (splitWords) {
            for (int i = 0; i < ((rawText.length() - 1) / maxWidth) + 1; i++) {
                returnString += (i == 0 ? "" : "\n") + rawText.substring(i*maxWidth, Math.min((i+1) * maxWidth - 1, rawText.length()));
            }
        } else {
            String currentToken;
            int currentTokenLength;
            int lineLength = 0;
            int index = 0;
            rawText = rawText + "\n"; // Weird formatting thing that I apparently gotta do

            while (iterator.hasNext()) {

                //Finds the next existing paragraph break and prints \n\n if its at where the program is working right now.
                int nextPB = rawText.indexOf("\n", index);
                if (nextPB > 0 && rawText.substring(index, nextPB).replaceAll("\\s+", "").equals("")) {
                    if (rawText.charAt(index + 1) == '\n') {
                        returnString += "\n";
                    }
                    returnString += "\n";
                    lineLength = 0;
                }

                //adds a space to the beginning of the current token unless its the first word of a line
                currentToken = lineLength > 0 ? (" " + iterator.next()) : iterator.next();
                currentTokenLength = currentToken.length();

                //if the current token fits on the current line, add it to the line.  Otherwise, print it on a new line and reset.
                if (currentTokenLength + lineLength <= maxWidth) {
                    returnString += currentToken;
                    lineLength += currentTokenLength;
                } else {
                    returnString += "\n" + currentToken.replaceAll("\\s+", "");
                    lineLength = currentTokenLength;
                }

                //updating index to the current index of the scanner
                while (Character.isWhitespace(rawText.charAt(index))) {
                    index++;
                }
                while (!Character.isWhitespace(rawText.charAt(index))) {
                    index++;
                }
            }
        }

        return returnString;
    }
}
