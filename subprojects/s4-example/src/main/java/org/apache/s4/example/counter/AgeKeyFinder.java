/*
 * Copyright (c) 2011 Yahoo! Inc. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *          http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License. See accompanying LICENSE file. 
 */
package org.apache.s4.example.counter;

import java.util.ArrayList;
import java.util.List;

import org.apache.s4.base.KeyFinder;

public class AgeKeyFinder implements KeyFinder<UserEvent> {

    public List<String> get(UserEvent event) {

        List<String> results = new ArrayList<String>();

        /* Retrieve the age and add it to the list. */
        results.add(Integer.toString(event.getAge()));

        return results;
    }
}
