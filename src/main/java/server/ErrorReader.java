package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ErrorReader extends Thread {

    private final Process process;
    private final InputStream stream;

    public ErrorReader(Process process) {
        this.process = process;
        this.stream = process.getErrorStream();
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))){
            readErrors(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readErrors(BufferedReader reader) {
        while (process.isAlive()) {
            try {
                System.err.println(reader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
