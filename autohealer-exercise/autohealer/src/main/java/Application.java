/*
 *  MIT License
 *
 *  Copyright (c) 2019 Michael Pogrebinsky - Distributed Systems & Cloud Computing with Java
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class Application {
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        /*if (args.length != 2) {
            System.out.println("Expecting parameters <number of workers> <path to worker jar file>");
            System.exit(1);
        }*/

//        int numberOfWorkers = Integer.parseInt(args[0]);
//        String pathToWorkerProgram = args[1];
        int numberOfWorkers = 10;
        String pathToWorkerProgram = "/Users/ashok/Documents/Else/Projects_leer/Udemy Distributed Systems And Cloud Computing/distributedSystemsAndCloudComputingInJava/autohealer-exercise/flakyworker/target/flaky.worker-1.0-SNAPSHOT-jar-with-dependencies.jar";

        Autohealer autohealer = new Autohealer(numberOfWorkers, pathToWorkerProgram);
        autohealer.connectToZookeeper();
        autohealer.startWatchingWorkers();
        autohealer.run();
        autohealer.close();
    }
}
