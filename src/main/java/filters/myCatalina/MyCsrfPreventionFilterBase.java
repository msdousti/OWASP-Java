/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 *
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package filters.myCatalina;


import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.Random;

public abstract class MyCsrfPreventionFilterBase extends MyFilterBase {

    private String randomClass = SecureRandom.class.getName();

    private Random randomSource;

    private int denyStatus = HttpServletResponse.SC_FORBIDDEN;

    /**
     * @return response status code that is used to reject denied request.
     */
    public int getDenyStatus() {
        return denyStatus;
    }

    /**
     * Set response status code that is used to reject denied request. If none
     * set, the default value of 403 will be used.
     *
     * @param denyStatus HTTP status code
     */
    public void setDenyStatus(int denyStatus) {
        this.denyStatus = denyStatus;
    }

    /**
     * Specify the class to use to generate the nonces. Must be in instance of
     * {@link Random}.
     *
     * @param randomClass The name of the class to use
     */
    public void setRandomClass(String randomClass) {
        this.randomClass = randomClass;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Set the parameters
        super.init(filterConfig);

        try {
            Class<?> clazz = Class.forName(randomClass);
            randomSource = (Random) clazz.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new ServletException("csrfPrevention.invalidRandomClass", e);
        }
    }

    @Override
    protected boolean isConfigProblemFatal() {
        return true;
    }

    /**
     * Generate a once time token (nonce) for authenticating subsequent
     * requests. The nonce generation is a simplified version of
     * ManagerBase.generateSessionId().
     *
     * @return the generated nonce
     */
    protected String generateNonce() {
        byte random[] = new byte[16];

        // Render the result as a String of hexadecimal digits
        StringBuilder buffer = new StringBuilder();

        randomSource.nextBytes(random);

        for (int j = 0; j < random.length; j++) {
            byte b1 = (byte) ((random[j] & 0xf0) >> 4);
            byte b2 = (byte) (random[j] & 0x0f);
            if (b1 < 10) {
                buffer.append((char) ('0' + b1));
            } else {
                buffer.append((char) ('A' + (b1 - 10)));
            }
            if (b2 < 10) {
                buffer.append((char) ('0' + b2));
            } else {
                buffer.append((char) ('A' + (b2 - 10)));
            }
        }

        return buffer.toString();
    }

    protected String getRequestedPath(HttpServletRequest request) {
        String path = request.getServletPath();
        if (request.getPathInfo() != null) {
            path = path + request.getPathInfo();
        }
        path = path.replace("//", "/");
        return path;
    }
}
