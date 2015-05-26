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

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.psnc.synat.wrdz.common.BaseTestSuite;
import pl.psnc.synat.wrdz.zmd.ZmdBaseTest;
import pl.psnc.synat.wrdz.zmd.entity.types.ObjectType;
import pl.psnc.synat.wrdz.zmd.input.InputFile;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectCreationRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectFetchingRequest;

public class ObjectManagerBeanTest extends ZmdBaseTest {

    /** Bean to test. */
    private ObjectManager objectManager;


    @Before
    public void setUp()
            throws Exception {
        objectManager = (ObjectManager) BaseTestSuite.getContext().lookup("java:global/wrdz/classes/ObjectManagerBean");
    }


    @Test(expected = ObjectNotFoundException.class)
    public void shouldGetObjectNotFoundException()
            throws Exception {
        ObjectFetchingRequest request = new ObjectFetchingRequest("oai:darceo.psnc.pl:0", 1, true, true);
        objectManager.getObject(request);
    }


    @Test
    public void shouldCreateObjectProperly()
            throws ObjectCreationException {
        URL url = getClass().getResource("/physarch.png");
        File file = new File(url.getPath());
        URI source = file.toURI();
        InputFile inputFile = new InputFile(source);
        Set<InputFile> inputFiles = new HashSet<InputFile>();
        inputFiles.add(inputFile);
        Map<String, URI> objectMetadata = new HashMap<String, URI>();
        ObjectCreationRequest request = new ObjectCreationRequest(inputFiles, objectMetadata, ObjectType.MASTER);
        String objectId = objectManager.createObject(request);
        Assert.assertEquals("oai:zmd.wrdz.synat.psnc.pl:1000", objectId);
    }

}
