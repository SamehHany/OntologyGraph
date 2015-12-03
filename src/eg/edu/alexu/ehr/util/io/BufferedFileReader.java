/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eg.edu.alexu.ehr.util.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author sameh
 */
public class BufferedFileReader {
    private String path;
    private BufferedReader reader;
    
    public BufferedFileReader(String path) throws FileNotFoundException {
        this.path = path;
        reader = new BufferedReader(new FileReader(path));
    }
    
    public BufferedFileReader(File file) throws FileNotFoundException {
        this.path = file.getPath();
        reader = new BufferedReader(new FileReader(file));
    }
    
    public String readLine() throws IOException {
        return reader.readLine();
    }
    
    public String read() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        StringBuilder strBuffer = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            strBuffer.append(line + String.format("%n"));
        }
        return strBuffer.toString();
    }
    
    public void close() throws IOException {
        reader.close();
    }
}
