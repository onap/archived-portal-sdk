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
package org.openecomp.portalsdk.core.web.support;

public class FeedbackMessage {

  private String  message;
  private int     messageType;
  private boolean keyed;

  public static final int MESSAGE_TYPE_ERROR   = 10;
  public static final int MESSAGE_TYPE_WARNING = 20;
  public static final int MESSAGE_TYPE_INFO    = 30;
  public static final int MESSAGE_TYPE_SUCCESS = 40;

  public static final String DEFAULT_MESSAGE_SUCCESS = "Update successful.";
  public static final String DEFAULT_MESSAGE_ERROR   = "An error occurred while processing the request: ";

  public static final String DEFAULT_MESSAGE_SYSTEM_ADMINISTRATOR = "If the problem persists, please contact your Administrator.";

  public FeedbackMessage() {
  }

  public FeedbackMessage(String message) {
    this(message, MESSAGE_TYPE_ERROR);
  }

  public FeedbackMessage(String message, int messageType) {
    this(message, messageType, false);
  }

  public FeedbackMessage(String message, int messageType, boolean keyed) {
    this.message     = message;
    this.messageType = messageType;
    this.keyed       = keyed;
  }

  public String getMessage() {
      return message;
  }

  public int getMessageType() {
      return messageType;
  }

    public boolean isKeyed() {
        return keyed;
    }

    public void setMessage(String message) {
      this.message = message;
  }

  public void setMessageType(int messageType) {
      this.messageType = messageType;
  }

    public void setKeyed(boolean keyed) {
        this.keyed = keyed;
    }


}
