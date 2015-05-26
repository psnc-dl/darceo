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
package pl.psnc.synat.fits.tech;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.metadata.tech.FileFormat;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.identity.ExternalIdentifier;
import edu.harvard.hul.ois.fits.identity.FitsIdentity;
import edu.harvard.hul.ois.fits.identity.FormatVersion;

/**
 * Builder of a file format based upon a part of output returned by FITS.
 * 
 */
public class FileFormatBuilder {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(FileFormatBuilder.class);

    /**
     * FITS output. A seed for the construction of the file format.
     */
    private final FitsOutput fitsOutput;


    /**
     * Constructs a file format builder.
     * 
     * @param fitsOutput
     *            seed
     */
    public FileFormatBuilder(FitsOutput fitsOutput) {
        this.fitsOutput = fitsOutput;
    }


    /**
     * Creates an object <code>FileFormat</code> based upon output returned by FITS.
     * 
     * @return file format
     */
    public FileFormat build() {
        List<FitsIdentity> identities = fitsOutput.getIdentities();
        if (identities == null || identities.size() == 0) {
            return null;
        }
        FileFormat fileFormat = new FileFormat();
        fileFormat.setFileType(new FileTypeBuilder(fitsOutput).build());
        fileFormat.setMimeType(identities.get(0).getMimetype());
        List<FormatVersion> formatVersions = identities.get(0).getFormatVersions();
        if (formatVersions != null && formatVersions.size() > 0) {
            Collections.sort(formatVersions, new FitsFormatVersionComparator());
            fileFormat.setFormatVersion(formatVersions.get(0).getValue());
        }
        fileFormat.setPuid(getPuidFromIdentity(identities.get(0)));
        String firstPuid = fileFormat.getPuid();
        Set<String> formatNames = new HashSet<String>();
        for (FitsIdentity identity : identities) {
            if (fileFormat.getPuid() == null && identity.getMimetype() != null
                    && identity.getMimetype().equals(fileFormat.getMimeType())) {
                fileFormat.setPuid(getPuidFromIdentity(identity));
            }
            if (identity.getMimetype() != null && identity.getMimetype().equals(fileFormat.getMimeType())) {
                formatNames.add(identity.getFormat());
            }
            if (firstPuid == null) {
                firstPuid = getPuidFromIdentity(identity);
            }
        }
        fileFormat.setNames(formatNames);
        if (fileFormat.getPuid() == null) {
            fileFormat.setPuid(firstPuid);
        }
        return fileFormat;
    }


    /**
     * Gets PUID from FITS identity section.
     * 
     * @param identity
     *            FITS identity section
     * @return PUID or null
     */
    private String getPuidFromIdentity(FitsIdentity identity) {
        List<ExternalIdentifier> externalIdentifiers = identity.getExternalIdentifiers();
        if (externalIdentifiers != null && externalIdentifiers.size() > 0) {
            Collections.sort(externalIdentifiers, new FitsExternalIdentifierComparator());
            ExternalIdentifier externalIdentifier = externalIdentifiers.get(0);
            if (externalIdentifier.getName().equals("puid")) {
                return externalIdentifier.getValue();
            }
        }
        return null;
    }


    /**
     * Descending comparator of format versions returned by FITS. They are compared as numbers if they are numbers,
     * otherwise as strings.
     * 
     */
    private class FitsFormatVersionComparator implements Comparator<FormatVersion> {

        @Override
        public int compare(FormatVersion fv1, FormatVersion fv2) {
            try {
                double ver1 = Double.parseDouble(fv1.getValue());
                double ver2 = Double.parseDouble(fv2.getValue());
                if (ver1 == ver2) {
                    return 0;
                }
                if (ver1 > ver2) {
                    return -1;
                } else {
                    return 1;
                }
            } catch (NumberFormatException e) {
                logger.debug("Version is not a number.", e);
            }
            return fv2.getValue().compareToIgnoreCase(fv1.getValue());
        }

    }


    /**
     * Descending comparator of external identifiers returned by FITS. They are compared as string but only the PUID
     * types are taken into account, i.e. they precedes other types.
     * 
     */
    private class FitsExternalIdentifierComparator implements Comparator<ExternalIdentifier> {

        @Override
        public int compare(ExternalIdentifier ei1, ExternalIdentifier ei2) {
            if (ei1.getName().equals("puid") && !ei2.getName().equals("puid")) {
                return -1;
            }
            if (!ei1.getName().equals("puid") && ei2.getName().equals("puid")) {
                return 1;
            }
            if (ei1.getName().equals("puid") && ei2.getName().equals("puid")) {
                if (ei1.getValue().startsWith("fmt/") && ei2.getValue().startsWith("x-fmt/")) {
                    return -1;
                }
                if (ei1.getValue().startsWith("x-fmt/") && ei2.getValue().startsWith("fmt/")) {
                    return 1;
                }
                int fmt1 = 0;
                int fmt2 = 0;
                if (ei1.getValue().startsWith("fmt/") && ei2.getValue().startsWith("fmt/")) {
                    fmt1 = Integer.parseInt(ei1.getValue().substring(4));
                    fmt2 = Integer.parseInt(ei2.getValue().substring(4));
                }
                if (ei1.getValue().startsWith("x-fmt/") && ei2.getValue().startsWith("x-fmt/")) {
                    fmt1 = Integer.parseInt(ei1.getValue().substring(6));
                    fmt2 = Integer.parseInt(ei2.getValue().substring(6));
                }
                if (fmt1 != 0 || fmt2 != 0) {
                    if (fmt1 == fmt2) {
                        return 0;
                    }
                    if (fmt1 > fmt2) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
            return ei2.getValue().compareToIgnoreCase(ei1.getValue());
        }

    }

}
