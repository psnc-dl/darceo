/**
 * Copyright 2015 Poznań Supercomputing and Networking Center
 *
 * Licensed under the GNU General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.gnu.org/licenses/gpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.psnc.synat.wrdz.zmd.object.helpers.archivers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import pl.psnc.synat.wrdz.zmd.exception.ArchiverException;

/**
 * Zip archive builder.
 */
public class ZipArchiveBuilder implements ArchiveBuilder {

    /**
     * Buffer length.
     */
    private static final int BUFFER_LENGTH = 2048;

    /**
     * List of files to be archived.
     */
    private final List<File> targets;

    /**
     * Destination file.
     */
    private final File destination;


    /**
     * Creates new instance of this class.
     * 
     * @param destination
     *            destination file.
     */
    public ZipArchiveBuilder(File destination) {
        targets = new ArrayList<File>();
        this.destination = destination;
    }


    /**
     * Creates new instance of this class.
     * 
     * @param target
     *            target file.
     * @param destination
     *            destination file.
     */
    public ZipArchiveBuilder(File target, File destination) {
        targets = new ArrayList<File>();
        targets.add(target);
        this.destination = destination;
    }


    /**
     * Creates new instance of this class.
     * 
     * @param targets
     *            target files.
     * @param destination
     *            destination file.
     */
    public ZipArchiveBuilder(List<File> targets, File destination) {
        this.targets = new ArrayList<File>(targets);
        this.destination = destination;
    }


    @Override
    public ZipArchiveBuilder addToArchive(File added) {
        targets.add(added);
        return this;
    }


    @Override
    public ZipArchiveBuilder addToArchive(List<File> added) {
        targets.addAll(added);
        return this;
    }


    @Override
    public File buildArchive()
            throws ArchiverException {
        try {
            ZipOutputStream outFile = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(destination)));
            byte[] dataBuffer = new byte[BUFFER_LENGTH];
            for (File file : targets) {
                String trunk = file.getParent();
                if (!trunk.endsWith(File.separator)) {
                    trunk += File.separator;
                }
                System.out.println("TRUNK: " + trunk);
                if (file.isDirectory()) {
                    addDirectory(file, outFile, dataBuffer, trunk);
                } else {
                    addFile(file, outFile, dataBuffer, trunk);
                }
            }
            cleanUp(outFile);
            return destination;
        } catch (FileNotFoundException e) {
            throw new ArchiverException("Cannot write to the specified destination.", e);
        } catch (Exception e) {
            throw new ArchiverException("Unspecified archiver error occured.", e);
        }

    }


    /**
     * Adds directory and all it's contents to the archive.
     * 
     * @param target
     *            target file.
     * @param outFile
     *            resulting archive file.
     * @param dataBuffer
     *            buffer for data reading.
     * @param trunk
     *            common directory trunk to be cut off from the paths inside the archives.
     * @throws FileNotFoundException
     *             if target file was not found.
     * @throws IOException
     *             should any IO error occur.
     */
    public void addDirectory(File target, ZipOutputStream outFile, byte[] dataBuffer, String trunk)
            throws FileNotFoundException, IOException {
        String[] files = target.list();
        for (String file : files) {
            File toBeZipped = new File(target, file);
            if (toBeZipped.isDirectory()) {
                addDirectory(toBeZipped, outFile, dataBuffer, trunk);
            } else {
                addFile(toBeZipped, outFile, dataBuffer, trunk);
            }
        }
    }


    /**
     * Adds file to the archive.
     * 
     * @param target
     *            target file.
     * @param outFile
     *            resulting archive file.
     * @param dataBuffer
     *            buffer for data reading.
     * @param trunk
     *            common directory trunk to be cut off from the paths inside the archives.
     * @throws FileNotFoundException
     *             if target file was not found.
     * @throws IOException
     *             should any IO error occur.
     */
    private void addFile(File target, ZipOutputStream outFile, byte[] dataBuffer, String trunk)
            throws FileNotFoundException, IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(target), BUFFER_LENGTH);
        ZipEntry entry = new ZipEntry(trunk != null ? target.getPath().substring(trunk.length()) : target.getPath());
        System.out.println(target.getPath().substring(trunk.length()));
        outFile.putNextEntry(entry);
        int count;
        while ((count = in.read(dataBuffer, 0, BUFFER_LENGTH)) != -1) {
            outFile.write(dataBuffer, 0, count);
        }
        cleanUp(in);
    }


    /**
     * Closes input stream.
     * 
     * @param in
     *            input stream to be closed.
     * @throws IOException
     *             should any IO errors occur.
     */
    private void cleanUp(InputStream in)
            throws IOException {
        in.close();
    }


    /**
     * Flushes and closes output stream.
     * 
     * @param out
     *            output stream to be flushed and closed.
     * @throws IOException
     *             should any IO errors occur.
     */
    private void cleanUp(OutputStream out)
            throws IOException {
        out.flush();
        out.close();
    }
}
