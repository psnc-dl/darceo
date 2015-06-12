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

import java.io.File;
import java.util.List;

import pl.psnc.synat.wrdz.zmd.exception.ArchiverException;

/**
 * Specifies the interface of compressed archive builder.
 */
public interface ArchiveBuilder {

    /**
     * Adds new resource (file or directory) to the archive.
     * 
     * @param added
     *            file or directory added to the archive
     * @return updated instance of {@link ArchiveBuilder}
     */
    ArchiveBuilder addToArchive(File added);


    /**
     * Adds new resources (files or directories) to the archive.
     * 
     * @param added
     *            list of files or directories added to the archive
     * @return updated instance of {@link ArchiveBuilder}
     */
    ArchiveBuilder addToArchive(List<File> added);


    /**
     * Builds new archive using provided information.
     * 
     * @return file handle to the newly created compressed archive.
     * @throws ArchiverException
     *             if any problems during archivization arise.
     */
    File buildArchive()
            throws ArchiverException;
}
