package server;

import util.ClosableThread;

import java.io.IOException;

public class StreamRetriever extends ClosableThread{

    private static final Runtime runtime = Runtime.getRuntime();
    private static final String brokenCmd[] = {
            "cmd /c",
            "\"C:\\Program Files (x86)\\Microsoft Visual Studio\\Shared\\Python36_64\\Scripts\\streamlink.exe\"",
//                        "--hls-start-offset 6:00",
            "-O",
//                        "https://www.twitch.tv/videos/413600002 worst |",
            "--retry-streams 300",
            "--twitch-disable-ads",
            "--twitch-disable-hosting",
            "twitch.tv/rawb worst |",
            "ffmpeg -i - -loglevel panic -vf fps=144 -vsync drop -vcodec mjpeg -crf 50 -an -f image2pipe -"
    };
    private static String command = buildCommand();

    private final Server server;
    private Process process;

    public StreamRetriever(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        while (server.isRunning()) {
            try {
                System.out.println("Attempting to connect to stream");
                attemptStreamConnection();
            } catch (InterruptedException e) {
            }
        }
    }

    private void attemptStreamConnection() throws InterruptedException {
        try {
            process = runtime.exec(command);
            new StreamVideoWatcher(server, process).start();
            new ErrorReader(process).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        process.waitFor();
    }

    private static String buildCommand() {
        if (command != null) return command;
        StringBuilder cmd = new StringBuilder();
        for (String s: brokenCmd) {
            cmd.append(s).append(" ");
        }
        return cmd.toString();
    }

    @Override
    public void close() {
        process.destroy();
    }
}
