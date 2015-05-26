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
package pl.psnc.synat.wrdz.common.entity.async;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entity represents a result of an asynchronous request submitted to the WRDZ application.
 */
@Entity
@Table(name = "COM_ASYNC_REQUEST_RESULTS", schema = "darceo")
public class AsyncRequestResult {

    /**
     * Default constructor.
     */
    public AsyncRequestResult() {
    }


    /**
     * Constructs result of asynchronous request entity based upon id and request.
     * 
     * @param id
     *            id
     * @param request
     *            request
     */
    public AsyncRequestResult(String id, AsyncRequest request) {
        this.id = id;
        this.request = request;
        this.completedOn = new Date();
        this.code = AsyncRequestResultConsts.HTTP_CODE_INTERNAL_SERVER_ERROR;
    }


    /**
     * Result identifier (primary key).
     */
    @Id
    @Column(name = "ARR_ID", length = 63, unique = true, nullable = false)
    private String id;

    /**
     * Asynchronous request for which this result is.
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinColumn(name = "ARR_AR_ID", unique = true, nullable = false)
    private AsyncRequest request;

    /**
     * Date and time of asynchronous request's completion.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ARR_COMPLETED_ON", nullable = false)
    private Date completedOn;

    /**
     * Http code of the result.
     */
    @Column(name = "ARR_HTTP_CODE", nullable = false)
    private Integer code;

    /**
     * Content type of the result.
     */
    @Column(name = "ARR_CONTENT_TYPE", nullable = true)
    private String contentType;

    /**
     * Filename of the content of the result, if this content in not inline.
     */
    @Column(name = "ARR_RESULT_FILENAME", nullable = true)
    private String filename;


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public AsyncRequest getRequest() {
        return request;
    }


    public void setRequest(AsyncRequest request) {
        this.request = request;
    }


    public Date getCompletedOn() {
        return completedOn;
    }


    public void setCompletedOn(Date completedOn) {
        this.completedOn = completedOn;
    }


    public Integer getCode() {
        return code;
    }


    public void setCode(Integer code) {
        this.code = code;
    }


    public String getContentType() {
        return contentType;
    }


    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    /**
     * Returns info whether result has any content.
     * 
     * @return whether result has any content
     */
    public boolean hasContent() {
        return contentType != null;
    }


    public String getFilename() {
        return filename;
    }


    public void setFilename(String filename) {
        this.filename = filename;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int res = 1;
        res = prime * res + ((id == null) ? 0 : id.hashCode());
        return res;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AsyncRequestResult other = (AsyncRequestResult) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("AsyncRequestResult ");
        sb.append("[id = ").append(id);
        sb.append(", request = ").append(request);
        sb.append(", completedOn = ").append(completedOn);
        sb.append(", code = ").append(code);
        sb.append(", contentType = ").append(contentType);
        sb.append(", filename = ").append(filename);
        sb.append("]");
        return sb.toString();
    }

}
