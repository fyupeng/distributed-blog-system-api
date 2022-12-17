package cn.fyupeng.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FetchVideoCover {

    private String ffmpegEXE;

    public FetchVideoCover(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public void getCover(String videoInputPath, String videoOutputPath) throws Exception {
        //ffmpeg.exe -ss 00:00:01 -y -i D:\45.mp4 -vframes 1 new.jpg
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);

        command.add("-ss");
        command.add("00:00:01");

        command.add("-y");
        command.add("-i");
        command.add(videoInputPath);

        command.add("-vframes");
        command.add("1");

        command.add(videoOutputPath);

        for(String c : command){
            System.out.println(c);
        }

        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        FetchVideoCover.CloseStream(process);

    }

    private static void CloseStream(Process process) throws IOException {
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while( (line = br.readLine())  != null ){

        }

        if(br != null){
            br.close();
        }
        if(inputStreamReader != null){
            inputStreamReader.close();
        }
        if(errorStream != null){
            errorStream.close();
        }
    }

    public static void main(String[] args) {
        FetchVideoCover ffmpeg = new FetchVideoCover("D:\\study\\software\\ffmpeg\\bin\\ffmpeg.exe");
        try {
            ffmpeg.getCover("D:\\45.mp4" , "D:\\一帧图片.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
