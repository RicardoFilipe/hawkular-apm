/*
 * Copyright 2015-2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawkular.apm.server.api.model.zipkin;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A trace is a series of spans (often RPC calls) which form a latency tree.
 *
 * The root span is where trace_id = id and parent_id = Nil. The root span is
 * usually the longest interval in the trace, starting with a SERVER_RECV
 * annotation and ending with a SERVER_SEND.
 */
public class Span {

    private static final Logger log = Logger.getLogger(Span.class.getName());

    private String traceId;

    private String name;

    private String id;

    private String parentId;

    private List<Annotation> annotations = Collections.emptyList();

    private List<BinaryAnnotation> binaryAnnotations = Collections.emptyList();

    private Boolean debug;

    private Long timestamp;

    private Long duration;

    /**
     * @return the traceId
     */
    public String getTraceId() {
        return traceId;
    }

    /**
     * @param traceId the traceId to set
     */
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the parentId
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * @return the annotations
     */
    public List<Annotation> getAnnotations() {
        return annotations;
    }

    /**
     * @param annotations the annotations to set
     */
    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    /**
     * @return the binaryAnnotations
     */
    public List<BinaryAnnotation> getBinaryAnnotations() {
        return binaryAnnotations;
    }

    /**
     * @param binaryAnnotations the binaryAnnotations to set
     */
    public void setBinaryAnnotations(List<BinaryAnnotation> binaryAnnotations) {
        this.binaryAnnotations = binaryAnnotations;
    }

    /**
     * @return the debug
     */
    public Boolean getDebug() {
        return debug;
    }

    /**
     * @param debug the debug to set
     */
    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    /**
     * @return the timestamp
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the duration
     */
    public Long getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public boolean clientSpan() {
        return annotations.size() == 2
                && annotations.get(0).getValue().equals("cs")
                && annotations.get(1).getValue().equals("cr");
    }

    public boolean serverSpan() {
        return annotations.size() == 2
                && annotations.get(0).getValue().equals("sr")
                && annotations.get(1).getValue().equals("ss");
    }

    public BinaryAnnotation getBinaryAnnotation(String key) {
        for (int i=0; i < binaryAnnotations.size(); i++) {
            BinaryAnnotation ba = binaryAnnotations.get(i);
            if (ba.getKey().equals(key)) {
                return ba;
            }
        }
        return null;
    }

    public URL url() {
        BinaryAnnotation httpUrl = getBinaryAnnotation("http.url");
        if (httpUrl != null) {
            try {
                return new URL(httpUrl.getValue());
            } catch (MalformedURLException e) {
                log.log(Level.SEVERE, "Failed to decode URL", e);
            }
        }
        return null;
    }

    public String operation() {
        BinaryAnnotation httpUrl = getBinaryAnnotation("http.method");
        if (httpUrl != null) {
            return httpUrl.getValue();
        }
        return null;
    }

    public String componentType() {
        // TODO: Determine from standard binary annotations (e.g. sql means Database)
        return null;
    }

}
