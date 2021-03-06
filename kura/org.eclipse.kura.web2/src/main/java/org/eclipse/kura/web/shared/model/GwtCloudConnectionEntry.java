/*******************************************************************************
 * Copyright (c) 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech
 *******************************************************************************/
package org.eclipse.kura.web.shared.model;

import java.io.Serializable;

import org.eclipse.kura.web.client.util.KuraBaseModel;

public class GwtCloudConnectionEntry extends KuraBaseModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 3373858744219238675L;

    public GwtCloudConnectionEntry() {
    }

    public boolean isConnected() {
        return get("connectionStatus");
    }

    public void setConnectionStatus(boolean isConnected) {
        set("connectionStatus", isConnected);
    }

    public String getCloudFactoryPid() {
        return get("cloudFactoryPid");
    }

    public void setCloudFactoryPid(String cloudFactoryPid) {
        set("cloudFactoryPid", cloudFactoryPid);
    }

    public String getCloudServicePid() {
        return get("cloudServicePid");
    }

    public void setCloudServicePid(String cloudServicePid) {
        set("cloudServicePid", cloudServicePid);
    }
}
