/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eg.edu.alexu.ehr.util.io.buffered;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Mina R. Waheeb
 */
public class BufferedFileWriter {
    private String path;
    private BufferedWriter writer;

    public BufferedFileWriter(String path) throws IOException {
        this.path = path;
        File file = new File(path);
        if (!file.exists())
            file.createNewFile();
        writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
    }

    public void write(String str) throws IOException {
        writer.write(str);
    }

    public void writeln(String str) throws IOException {
        write(str + String.format("%n"));
    }

    public void write() throws IOException {
        write("");
    }

    public void writeln() throws IOException {
        writeln("");
    }

    public void close() throws IOException {
        writer.close();
    }
}
