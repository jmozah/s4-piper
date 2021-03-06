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

import java.util.concurrent.TimeUnit;

import org.apache.s4.base.Event;
import org.apache.s4.core.App;
import org.apache.s4.core.Receiver;
import org.apache.s4.core.Sender;
import org.apache.s4.core.Stream;

import com.google.inject.Guice;
import com.google.inject.Injector;

/*
 * This is a sample application to test a new S4 API. 
 * See README file for details.
 * 
 * */

final public class MyApp extends App {

    final private int interval = 1;
    private GenerateUserEventPE generateUserEventPE;

    /*
     * 
     * 
     * The application graph itself is created in this Class. However, developers may provide tools for creating apps
     * which will generate the objects.
     * 
     * IMPORTANT: we create a graph of PE prototypes. The prototype is a class instance that is used as a prototype from
     * which all PE instance will be created. The prototype itself is not used as an instance. (Except when the PE is of
     * type Singleton PE). To create a data structure for each PE instance you must do it in the method
     * ProcessingElement.onCreate().
     */

    /*
     * Build the application graph using POJOs. Don't like it? Write a nice tool.
     * 
     * @see io.s4.App#init()
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onInit() {

        /* PE that prints counts to console. */
        PrintPE printPE = createPE(PrintPE.class);

        Stream<CountEvent> userCountStream = createStream(CountEvent.class);
        userCountStream.setName("User Count Stream");
        userCountStream.setKey(new CountKeyFinder());
        userCountStream.setPE(printPE);

        Stream<CountEvent> genderCountStream = createStream(CountEvent.class);
        genderCountStream.setName("Gender Count Stream");
        genderCountStream.setKey(new CountKeyFinder());
        genderCountStream.setPE(printPE);

        Stream<CountEvent> ageCountStream = createStream(CountEvent.class);
        ageCountStream.setName("Age Count Stream");
        ageCountStream.setKey(new CountKeyFinder());
        ageCountStream.setPE(printPE);

        /* PEs that count events by user, gender, and age. */
        CounterPE userCountPE = createPE(CounterPE.class);
        userCountPE.setTrigger(Event.class, interval, 10l, TimeUnit.MILLISECONDS);
        userCountPE.setCountStream(userCountStream);

        CounterPE genderCountPE = createPE(CounterPE.class);
        genderCountPE.setTrigger(Event.class, interval, 10l, TimeUnit.MILLISECONDS);
        genderCountPE.setCountStream(genderCountStream);

        CounterPE ageCountPE = createPE(CounterPE.class);
        ageCountPE.setTrigger(Event.class, interval, 10l, TimeUnit.MILLISECONDS);
        ageCountPE.setCountStream(ageCountStream);

        /* Streams that output user events keyed on user, gender, and age. */
        Stream<UserEvent> userStream = createStream(UserEvent.class);
        userStream.setName("User Stream");
        userStream.setKey(new UserIDKeyFinder());
        userStream.setPE(userCountPE);

        Stream<UserEvent> genderStream = createStream(UserEvent.class);
        genderStream.setName("Gender Stream");
        /* It is possible to specify a field name of a primitive type as a string instead of using a KeyFinder object. */
        // genderStream.setKey(new GenderKeyFinder());
        genderStream.setKey("gender");
        genderStream.setPE(genderCountPE);

        Stream<UserEvent> ageStream = createStream(UserEvent.class);
        ageStream.setName("Age Stream");
        ageStream.setKey(new AgeKeyFinder());
        ageStream.setPE(ageCountPE);

        generateUserEventPE = createPE(GenerateUserEventPE.class);
        generateUserEventPE.setStreams(userStream, genderStream, ageStream);
        generateUserEventPE.setSingleton(true);
        generateUserEventPE.setTimerInterval(1, TimeUnit.MILLISECONDS);

    }

    /*
     * Create and send 200 dummy events of type UserEvent.
     * 
     * @see io.s4.App#start()
     */
    @Override
    protected void onStart() {

    }

    @Override
    protected void onClose() {
        System.out.println("Bye.");

    }

    public static void main(String[] args) {

        Injector injector = Guice.createInjector(new Module());
        MyApp myApp = injector.getInstance(MyApp.class);
        Sender sender = injector.getInstance(Sender.class);
        Receiver receiver = injector.getInstance(Receiver.class);
        myApp.setCommLayer(sender, receiver);
        myApp.init();
        myApp.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        myApp.close();
        receiver.close();
    }
}
