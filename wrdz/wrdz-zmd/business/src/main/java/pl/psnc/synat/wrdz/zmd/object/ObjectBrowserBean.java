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
package pl.psnc.synat.wrdz.zmd.object;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.lang.StringUtils;

import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.common.dao.QueryModifier;
import pl.psnc.synat.wrdz.common.dao.QuerySorter;
import pl.psnc.synat.wrdz.zmd.dao.object.DigitalObjectDao;
import pl.psnc.synat.wrdz.zmd.dao.object.DigitalObjectFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.DigitalObjectSorterBuilder;
import pl.psnc.synat.wrdz.zmd.dao.object.content.ContentVersionDao;
import pl.psnc.synat.wrdz.zmd.dao.object.content.ContentVersionFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.content.ContentVersionSorterBuilder;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.FileExtractedMetadataDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.FileExtractedMetadataFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.FileProvidedMetadataDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.FileProvidedMetadataFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.ObjectProvidedMetadataDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.ObjectProvidedMetadataFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.operation.OperationDao;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileExtractedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileProvidedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectProvidedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Operation;

/**
 * Bean providing additional functionalities supporting {@link ObjectManagerBean}.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ObjectBrowserBean implements ObjectBrowser {

    /**
     * Content version DAO.
     */
    @EJB
    private ContentVersionDao contentVersionDao;

    /**
     * Digital object DAO.
     */
    @EJB
    private DigitalObjectDao digitalObjectDao;

    /**
     * Data file DAO.
     */
    @EJB
    private DataFileDao dataFileDao;

    /**
     * Data file DAO.
     */
    @EJB
    private OperationDao operationDao;

    /**
     * Provided metadata files DAO.
     */
    @EJB
    private FileProvidedMetadataDao providedFileMetadataDAO;

    /**
     * Extracted metadata files DAO.
     */
    @EJB
    private FileExtractedMetadataDao extractedFileMetadataDAO;

    /**
     * Provided object metadata DAO.
     */
    @EJB
    private ObjectProvidedMetadataDao providedObjectMetadataDAO;


    @Override
    public DigitalObject getDigitalObject(String identifier)
            throws ObjectNotFoundException {
        DigitalObject digitalObject = digitalObjectDao.getDigitalObject(identifier);
        if (digitalObject == null) {
            throw new ObjectNotFoundException("Object with identifier " + identifier + " does not exist");
        }
        return digitalObject;
    }


    @Override
    public String getEntityContent(Long identifier)
            throws ObjectNotFoundException {
        Operation op = operationDao.getOperationForObject(identifier);
        if (op == null) {
            throw new ObjectNotFoundException("Object with identifier " + identifier + " does not exist");
        }
        String metsContent = op.getContents();

        return metsContent;
    }


    @Override
    @SuppressWarnings("unchecked")
    public List<DigitalObject> getDigitalObjects(int pageOffset, int pageSize, String identifier, String name) {
        QueryModifier<DigitalObjectFilterFactory, DigitalObjectSorterBuilder, DigitalObject> modifier = digitalObjectDao
                .createQueryModifier();
        DigitalObjectFilterFactory factory = modifier.getQueryFilterFactory();

        QueryFilter<DigitalObject> filter = factory.byCurrentVersionState(true);
        if (StringUtils.isNotBlank(identifier)) {
            filter = factory.and(filter, factory.byIdentifier("%" + identifier + "%"));
        }
        if (StringUtils.isNotBlank(name)) {
            filter = factory.and(filter, factory.byName("%" + name + "%"));
        }

        QuerySorter<DigitalObject> sorter = modifier.getQuerySorterBuilder().byId(false).buildSorter();

        return digitalObjectDao.findPaginatedBy(filter, sorter, pageSize, pageOffset);
    }


    @Override
    @SuppressWarnings("unchecked")
    public long countDigitalObjects(String identifier, String name) {
        DigitalObjectFilterFactory factory = digitalObjectDao.createQueryModifier().getQueryFilterFactory();

        QueryFilter<DigitalObject> filter = factory.byCurrentVersionState(true);
        if (StringUtils.isNotBlank(identifier)) {
            filter = factory.and(filter, factory.byIdentifier("%" + identifier + "%"));
        }
        if (StringUtils.isNotBlank(name)) {
            filter = factory.and(filter, factory.byName("%" + name + "%"));
        }
        return digitalObjectDao.countBy(filter);
    }


    @Override
    @SuppressWarnings("unchecked")
    public List<DigitalObject> getDigitalObjects(Collection<Long> ids, int pageOffset, int pageSize, String identifier,
            String name) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        QueryModifier<DigitalObjectFilterFactory, DigitalObjectSorterBuilder, DigitalObject> modifier = digitalObjectDao
                .createQueryModifier();
        DigitalObjectFilterFactory factory = modifier.getQueryFilterFactory();

        QueryFilter<DigitalObject> filter = factory.byIds(ids);
        if (StringUtils.isNotBlank(identifier)) {
            filter = factory.and(filter, factory.byIdentifier("%" + identifier + "%"));
        }
        if (StringUtils.isNotBlank(name)) {
            filter = factory.and(filter, factory.byName("%" + name + "%"));
        }
        QuerySorter<DigitalObject> sorter = modifier.getQuerySorterBuilder().byId(false).buildSorter();

        return digitalObjectDao.findPaginatedBy(filter, sorter, pageSize, pageOffset);
    }


    @Override
    @SuppressWarnings("unchecked")
    public long countDigitalObjects(Collection<Long> ids, String identifier, String name) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        DigitalObjectFilterFactory factory = digitalObjectDao.createQueryModifier().getQueryFilterFactory();

        QueryFilter<DigitalObject> filter = factory.byIds(ids);
        if (StringUtils.isNotBlank(identifier)) {
            filter = factory.and(filter, factory.byIdentifier("%" + identifier + "%"));
        }
        if (StringUtils.isNotBlank(name)) {
            filter = factory.and(filter, factory.byName("%" + name + "%"));
        }
        return digitalObjectDao.countBy(filter);
    }


    @Override
    public ContentVersion getObjectsVersion(String identifier, Integer versionNo)
            throws ObjectNotFoundException {
        DigitalObject digitalObject = getDigitalObject(identifier);
        if (digitalObject.getCurrentVersion() != null) {
            Integer currentVersionNo = digitalObject.getCurrentVersion().getVersion();
            if (currentVersionNo.equals(versionNo) || versionNo == null) {
                return digitalObject.getCurrentVersion();
            }
            if (currentVersionNo > versionNo) {
                ContentVersion contentVersion = contentVersionDao.getContentVersion(digitalObject.getId(), versionNo);
                if (contentVersion != null) {
                    return contentVersion;
                }
            }
        }
        throw new ObjectNotFoundException("Object with identifier " + identifier + " and version " + versionNo
                + " does not exist");
    }


    @Override
    public List<ContentVersion> getContentVersions(String identifier, boolean ascending)
            throws ObjectNotFoundException {
        DigitalObject object = getDigitalObject(identifier);
        QueryModifier<ContentVersionFilterFactory, ContentVersionSorterBuilder, ContentVersion> queryModifier = contentVersionDao
                .createQueryModifier();
        ContentVersionFilterFactory queryFilterFactory = queryModifier.getQueryFilterFactory();
        return contentVersionDao.findBy(queryFilterFactory.byObjectId(object.getId()), queryModifier
                .getQuerySorterBuilder().byVersion(ascending).buildSorter());
    }


    @Override
    public DataFile getDataFileFromVersion(String path, ContentVersion version)
            throws FileNotFoundException {
        DataFile dataFile = dataFileDao.getDataFileFromVersion(path, version);
        if (dataFile == null) {
            throw new FileNotFoundException("Data file " + path + " in version " + version.getVersion()
                    + " not found in the repository.");
        }
        return dataFile;
    }


    @Override
    public List<DataFile> getDataFiles(ContentVersion version, List<String> files)
            throws FileNotFoundException {
        List<DataFile> results = new LinkedList<DataFile>();

        Iterator<String> it = files.iterator();
        while (it.hasNext()) {
            String path = (String) it.next();
            DataFile dataFile = dataFileDao.getDataFileFromPathAndVersion(path, version);
            if (dataFile != null) {
                results.add(dataFile);
            }
        }
        if (results != null && results.size() == files.size()) {
            return results;
        } else {
            throw new FileNotFoundException("Unable to find specified files!");
        }
    }


    @Override
    public MetadataFile getMetadataFileFromVersion(String path, ContentVersion version)
            throws FileNotFoundException {
        int vid = version.getVersion();
        //try find file in provided metadata
        FileProvidedMetadataFilterFactory queryFilterFactory = providedFileMetadataDAO.createQueryModifier()
                .getQueryFilterFactory();
        MetadataFile result = null;
        QueryFilter<FileProvidedMetadata> providedFileMetadataFilter = queryFilterFactory.byMetadataFileName(path);
        queryFilterFactory.and(providedFileMetadataFilter, queryFilterFactory.byIncludedInVersion(vid));
        result = providedFileMetadataDAO.findFirstResultBy(providedFileMetadataFilter);
        if (result != null) {
            return result;
        }

        //try find file in extracted metadata
        FileExtractedMetadataFilterFactory queryExtractedFilterFactory = extractedFileMetadataDAO.createQueryModifier()
                .getQueryFilterFactory();
        QueryFilter<FileExtractedMetadata> extractedFileMetadataFilter = queryExtractedFilterFactory
                .byMetadataFileName(path);
        queryExtractedFilterFactory.and(extractedFileMetadataFilter,
            queryExtractedFilterFactory.byIncludedInVersion(vid));
        result = extractedFileMetadataDAO.findFirstResultBy(extractedFileMetadataFilter);
        if (result != null) {
            return result;
        }

        //try find file in object provided metadata
        ObjectProvidedMetadataFilterFactory queryObjectProvidedFilterFactory = providedObjectMetadataDAO
                .createQueryModifier().getQueryFilterFactory();
        QueryFilter<ObjectProvidedMetadata> providedObjectMetadataFilter = queryObjectProvidedFilterFactory
                .byMetadataFileName(path);
        queryObjectProvidedFilterFactory.and(providedObjectMetadataFilter,
            queryObjectProvidedFilterFactory.byIncludedInVersion(vid));
        result = providedObjectMetadataDAO.findFirstResultBy(providedObjectMetadataFilter);
        return result;
    }
}
