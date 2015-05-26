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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entity represents an asynchronous request submitted to the WRDZ application.
 */
@Entity
@Table(name = "COM_ASYNC_REQUESTS", schema = "darceo")
public class AsyncRequest {

    /**
     * Default constructor.
     */
    public AsyncRequest() {
    }


    /**
     * Constructs asynchronous request entity based upon id, type and subtype of the request.
     * 
     * @param id
     *            id
     * @param type
     *            type
     * @param subtype
     *            subtype
     */
    public AsyncRequest(String id, String type, String subtype) {
        this.id = id;
        this.type = type;
        this.subtype = subtype;
        this.createdOn = new Date();
        this.inProgress = Boolean.TRUE;
    }


    /**
     * Asynchronous request identifier (primary key).
     */
    @Id
    @Column(name = "AR_ID", length = 63, unique = true, nullable = false)
    private String id;

    /**
     * Type of the request - name of the functionality.
     */
    @Column(name = "AR_TYPE", length = 63, nullable = false)
    private String type;

    /**
     * Subtype of the request - name of the method.
     */
    @Column(name = "AR_SUBTYPE", length = 63, nullable = false)
    private String subtype;

    /**
     * Date and time of asynchronous request's creation.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "AR_CREATED_ON", nullable = false)
    private Date createdOn;

    /**
     * Whether processing of the asynchronous request is in progress.
     */
    @Column(name = "AR_IN_PROGRESS", nullable = false)
    private Boolean inProgress;

    /**
     * Requested URL.
     */
    @Column(name = "AR_REQUESTED_URL", length = 4095, nullable = true)
    private String requestedUrl;

    /**
     * Result of this asynchronous request.
     */
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "request", cascade = { CascadeType.ALL })
    protected AsyncRequestResult result;


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getSubtype() {
        return subtype;
    }


    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }


    public Date getCreatedOn() {
        return createdOn;
    }


    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }


    public boolean isInProgress() {
        return inProgress;
    }


    public void setInProgress(Boolean inProgress) {
        this.inProgress = inProgress;
    }


    public String getRequestedUrl() {
        return requestedUrl;
    }


    public void setRequestedUrl(String requestedUrl) {
        this.requestedUrl = requestedUrl;
    }


    public AsyncRequestResult getResult() {
        return result;
    }


    public void setResult(AsyncRequestResult result) {
        this.result = result;
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
        AsyncRequest other = (AsyncRequest) obj;
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
        StringBuffer sb = new StringBuffer("AsyncRequest ");
        sb.append("[id = ").append(id);
        sb.append(", type = ").append(type);
        sb.append(", subtype = ").append(subtype);
        sb.append(", createdOn = ").append(createdOn);
        sb.append(", inProgress = ").append(inProgress);
        sb.append(", requestedUrl = ").append(requestedUrl);
        sb.append("]");
        return sb.toString();
    }

}
