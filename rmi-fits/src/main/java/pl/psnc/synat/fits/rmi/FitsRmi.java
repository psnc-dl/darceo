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
package pl.psnc.synat.fits.rmi;

import java.io.PrintStream;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import pl.psnc.synat.fits.logging.LoggingOutputStream;
import pl.psnc.synat.fits.logging.MessageFormatter;
import pl.psnc.synat.fits.logging.StdOutErrLevel;
import pl.psnc.synat.fits.tech.FitsTechMetadataExtractorService;
import pl.psnc.synat.wrdz.common.metadata.tech.rmi.FitsTechMetadataExtractor;
import pl.psnc.synat.wrdz.common.metadata.tech.rmi.FitsTechMetadataExtractorConsts;
import edu.harvard.hul.ois.fits.exceptions.FitsException;

/**
 * The main class. Starts or stops the FITS service in the RMI registry.
 * 
 */
public class FitsRmi {

    /**
     * Standard output
     */
    private static PrintStream stdout;

    /**
     * Standard error
     */
    private static PrintStream stderr;

    /**
     * RMI registry.
     */
    private static Registry registry;

    /**
     * FITS service;
     */
    private static FitsTechMetadataExtractor fitsService;

    /**
     * FITS stopping service;
     */
    private static FitsRmiStopperService stopService;


    /**
     * The entry point.
     * 
     * @param args
     *            argument list (unnecessary)
     */
    public static void main(String[] args) {
        stdout = System.out;
        stderr = System.err;

        if (args.length >= 1 && !(args[0].equals("start") || args[0].equals("stop"))) {
            stdout.println("Invalid CLI options. Use: start or stop");
            System.exit(0);
        }

        redirectStandardOutput();

        if (args.length == 0) {
            startAsService(); // for windows service
        } else if (args[0].equals("start")) {
            start();
        } else {
            stop();
        }
    }


    private static void redirectStandardOutput() {
        LogManager logManager = LogManager.getLogManager();
        logManager.reset();

        Handler fileHandler = null;
        try {
            fileHandler = new FileHandler("logs/stdout.log", 10000000, 3, true);
        } catch (Exception e) {
            stdout.println("Error creating a log file for the standard output.");
            stdout.println(e.toString());
            System.exit(1);
        }
        fileHandler.setFormatter(new MessageFormatter());
        Logger.getLogger("").addHandler(fileHandler);

        Logger logger;
        LoggingOutputStream los;

        logger = Logger.getLogger("stdout");
        los = new LoggingOutputStream(logger, StdOutErrLevel.STDOUT);
        System.setOut(new PrintStream(los, true));

        logger = Logger.getLogger("stderr");
        los = new LoggingOutputStream(logger, StdOutErrLevel.STDERR);
        System.setErr(new PrintStream(los, true));
    }


    private static void start() {
        System.setProperty("sun.rmi.log.debug", "true");
        System.setProperty("sun.rmi.server.exceptionTrace", "true");
        try {
            registry = LocateRegistry.createRegistry(FitsTechMetadataExtractorConsts.RMI_REGISTRY_PORT);
        } catch (RemoteException e) {
            stdout.println("Error creating RMI registry on the port: "
                    + FitsTechMetadataExtractorConsts.RMI_REGISTRY_PORT + ".");
            if (e instanceof java.rmi.server.ExportException) {
                stdout.println("It seems that the FITS service is already running.");
            } else {
                stdout.println(e.toString());
            }
            System.exit(1);
        }

        try {
            stopService = new FitsRmiStopperService();
            FitsRmiStopper stub = (FitsRmiStopper) UnicastRemoteObject.exportObject(stopService,
                FitsRmiStopperConsts.FITS_STOP_RMI_SERVICE_PORT);
            registry.rebind(FitsRmiStopperConsts.FITS_STOP_RMI_SERVICE_NAME, stub);
        } catch (AccessException e) {
            stdout.println("Error accessing the RMI registry");
            stdout.println(e.toString());
            System.exit(1);
        } catch (RemoteException e) {
            stdout.println("Error binding the service to the RMI registry");
            stdout.println(e.toString());
            System.exit(1);
        } catch (Exception e) {
            stdout.println("Unexpected error");
            stdout.println(e.toString());
            System.exit(1);
        }

        try {
            fitsService = new FitsTechMetadataExtractorService();
        } catch (FitsException e) {
            stdout.println("Error creating an instance of the service class");
            stdout.println(e.toString());
            System.exit(1);
        }
        try {
            FitsTechMetadataExtractor stub = (FitsTechMetadataExtractor) UnicastRemoteObject.exportObject(fitsService,
                FitsTechMetadataExtractorConsts.FITS_RMI_SERVICE_PORT);
            registry.rebind(FitsTechMetadataExtractorConsts.FITS_RMI_SERVICE_NAME, stub);
            stdout.println("FITS service registered and bound under the name: "
                    + FitsTechMetadataExtractorConsts.FITS_RMI_SERVICE_NAME);
        } catch (AccessException e) {
            stdout.println("Error accessing the RMI registry");
            stdout.println(e.toString());
            System.exit(1);
        } catch (RemoteException e) {
            stdout.println("Error binding the service to the RMI registry");
            stdout.println(e.toString());
            System.exit(1);
        } catch (Exception e) {
            stdout.println("Unexpected error");
            stdout.println(e.toString());
            System.exit(1);
        }
    }


    private static void stop() {
        try {
            registry = LocateRegistry.getRegistry("localhost", FitsTechMetadataExtractorConsts.RMI_REGISTRY_PORT);
        } catch (RemoteException e) {
            stdout.println("Error getting the RMI registry on the port: "
                    + FitsTechMetadataExtractorConsts.RMI_REGISTRY_PORT + ".");
            stdout.println(e.toString());
            System.exit(1);
        }
        FitsRmiStopper stopper = null;
        try {
            stopper = (FitsRmiStopper) registry.lookup(FitsRmiStopperConsts.FITS_STOP_RMI_SERVICE_NAME);
        } catch (AccessException e) {
            stdout.println("Error accessing the RMI registry.");
            stdout.println(e.toString());
            System.exit(1);
        } catch (RemoteException e) {
            stdout.println("Error getting the service from the RMI registry.");
            if (e instanceof java.rmi.ConnectException) {
                stdout.println("It seems that the FITS service is not running.");
            } else {
                stdout.println(e.toString());
            }
            System.exit(1);
        } catch (Exception e) {
            stdout.println("Unexpected error.");
            stdout.println(e.toString());
            System.exit(1);
        }
        try {
            stopper.stop(registry);
        } catch (RemoteException e) {
            stdout.println("Error calling the stopping service.");
            stdout.println(e.toString());
            System.exit(1);
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        stdout.println("The FITS service stopped");
    }


    private static void startAsService() {
        FitsRmi.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                FitsRmi.stop();
            }

        });

    }
}
