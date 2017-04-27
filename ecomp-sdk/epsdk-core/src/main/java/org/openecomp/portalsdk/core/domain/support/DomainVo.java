/*-
 * ================================================================================
 * eCOMP Portal SDK
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package org.openecomp.portalsdk.core.domain.support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.openecomp.portalsdk.core.domain.FusionVo;


/* Super class from which all data objects descend */
public class DomainVo extends FusionVo implements Serializable, Cloneable, Comparable {

    protected Long id;
    protected Date created;
    protected Date modified;
    protected Long createdId;
    protected Long modifiedId;
    protected Long rowNum;

    protected Serializable auditUserId;

    Set auditTrail = null;

    public DomainVo() {}


    public void setId(Long i) {
        id = i;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public void setCreatedId(Long createdId) {
        this.createdId = createdId;
    }

    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
    }

    public void setAuditUserId(Serializable auditUserId) {
        this.auditUserId = auditUserId;
    }

    public void setRowNum(Long rowNum) {
        this.rowNum = rowNum;
    }

    public void setAuditTrail(Set auditTrail) {
        this.auditTrail = auditTrail;
    }

    public Long getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public Date getModified() {
        return modified;
    }

    public Long getCreatedId() {
        return createdId;
    }

    public Long getModifiedId() {
        return modifiedId;
    }

    public Serializable getAuditUserId() {
        return auditUserId;
    }

    public Long getRowNum() {
        return rowNum;
    }

    public Set getAuditTrail() {
        return auditTrail;
    }


    public Object clone() throws CloneNotSupportedException {
      return super.clone();
    }


    public Object copy() {
      return copy(false);
    }


    public Object copy(boolean isIdNull) {
       // let's create a "copy" of the object using serialization
       ByteArrayOutputStream baos = null;
       ByteArrayInputStream  bais = null;
       ObjectOutputStream     oos = null;
       ObjectInputStream      ois = null;

       DomainVo newVo = null;

       try {

         baos = new ByteArrayOutputStream();
         oos  = new ObjectOutputStream(baos);
         oos.writeObject(this);

         bais  = new ByteArrayInputStream(baos.toByteArray());
         ois   =  new ObjectInputStream(bais);
         newVo = (DomainVo)ois.readObject();

         if (isIdNull) {
           newVo.setId(null);
         }

       }
       catch (Exception e) {
         e.printStackTrace();
       }

       return newVo;
     }



    public int compareTo(Object obj){
      Long c1 = getId();
      Long c2 = ((DomainVo)obj).getId();

      return (c1 == null || c2 == null) ? 1 : c1.compareTo(c2);
    }


}
