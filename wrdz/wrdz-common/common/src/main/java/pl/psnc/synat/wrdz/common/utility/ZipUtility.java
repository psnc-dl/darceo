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
package pl.psnc.synat.wrdz.common.utility;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

/**
 * Contains method for handling ZIP files.
 */
public final class ZipUtility {

    /**
     * Private constructor.
     */
    private ZipUtility() {
    }


    /**
     * Packs specified files into destination file.
     * 
     * @param srcs
     *            files to pack
     * @param dest
     *            destination zip file
     * @throws IOException
     *             in case when something goes wrong in method
     */
    public static void zip(List<File> srcs, File dest)
            throws IOException {
        ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(dest));
        try {
            for (File file : srcs) {
                FileInputStream inputStream = new FileInputStream(file);
                try {
                    outputStream.putNextEntry(new ZipEntry(file.getName()));
                    IOUtils.copy(inputStream, outputStream);
                } finally {
                    inputStream.close();
                    outputStream.closeEntry();
                }

            }
        } finally {
            outputStream.flush();
            outputStream.close();
        }
    }


    /**
     * Unpacks specified file into destination directory.
     * 
     * @param src
     *            zip source file
     * @param dest
     *            destination folder
     * @throws IOException
     *             in case when something goes wrong in method
     */
    public static void unzip(File src, File dest)
            throws IOException {
        if (!dest.exists()) {
            throw new FileNotFoundException("Destination folder not found!");
        }

        ZipFile zipFile = new ZipFile(src);

        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (!entry.isDirectory()) {
                File file = new File(dest, entry.getName());
                File parentFile = file.getParentFile();
                if (parentFile != null) {
                    parentFile.mkdirs();
                }
                InputStream inputStream = zipFile.getInputStream(entry);
                FileOutputStream outputStream = new FileOutputStream(file);

                try {
                    IOUtils.copy(inputStream, outputStream);
                } finally {
                    inputStream.close();
                    outputStream.close();
                }
            }
        }
    }


    /**
     * Unpacks specified input stream into destination directory.
     * 
     * @param src
     *            zip source file
     * @param dest
     *            destination folder
     * @throws IOException
     *             in case when something goes wrong in method
     */
    public static void unzip(InputStream src, File dest)
            throws IOException {
        if (!dest.exists()) {
            throw new FileNotFoundException("Destination folder not found!");
        }
        byte[] buffer = new byte[1024];
        ZipInputStream zipInput = new ZipInputStream(src);
        ZipEntry entry = null;
        while ((entry = zipInput.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
                File file = new File(dest, entry.getName());
                File parentFile = file.getParentFile();
                if (parentFile != null) {
                    parentFile.mkdirs();
                }
                BufferedOutputStream bos = null;
                try {
                    bos = new BufferedOutputStream(new FileOutputStream(file), 1024);
                    int len;
                    while ((len = zipInput.read(buffer)) != -1) {
                        bos.write(buffer, 0, len);
                    }
                } finally {
                    bos.flush();
                    bos.close();
                }
            }
            zipInput.closeEntry();
        }
    }

}
