package org.example.probes.helloworld;

import org.example.probes.helloworld.types.*;

import com.nimsoft.pf.common.pom.MvnPomVersion;
import com.nimsoft.nimbus.NimException;
import com.nimsoft.pf.common.log.Log;
import com.nimsoft.probe.framework.devkit.ProbeBase;
import com.nimsoft.probe.framework.devkit.interfaces.IProbeInventoryCollection;
import com.nimsoft.probe.framework.devkit.interfaces.IInventoryDataset;
import com.nimsoft.probe.framework.devkit.InventoryDataset;
import com.nimsoft.probe.framework.devkit.configuration.CtdPropertyDefinitionsList;
import com.nimsoft.probe.framework.devkit.inventory.Folder;
import com.nimsoft.probe.framework.devkit.configuration.ResourceConfig;
import com.nimsoft.probe.framework.devkit.inventory.typedefs.*;
import com.nimsoft.vm.cfg.IProbeResourceTypeInfo;

public class ProbeMain extends ProbeBase implements IProbeInventoryCollection {

    /*
     * Probe Name, Version, and Vendor are required when initializing the probe.
     */
    public final static  String PROBE_NAME = "hello_world";
    public static final  String PROBE_VERSION = MvnPomVersion.get("org.example", PROBE_NAME);
    public static final  String PROBE_VENDOR = "org.example";
    
    /**
     * Every probe is a stand alone Java program that must start itself up and
     * register itself with the bus. The Probe Framework provides all the logic
     * for doing this in the {@code ProbeBase} base class. So all we must do when
     * implementing a probe is create a simple {@code main()} method that invokes
     * the proper life cycle methods in {@code ProbeBase}
     *
     * @param args
     */
    public static void main(final String[] args) {
        try {
            ProbeBase.initLogging(args);
            ProbeMain probeProcess = new ProbeMain(args);
            Log.info("Probe " + PROBE_NAME + " startup"); 
            probeProcess.execute();
        } catch (final Exception e) {
            ProbeBase.reportProbeStartupError(e, PROBE_NAME);
        }
    }

    /**
     * You must implement a constructor that calls the super constructor 
     * and passes in the parameters shown below. You will call your 
     * constructor from the main() method and you may modify the method
     * signature to suit your needs. For example if you wanted to pass some
     * additional arguments. 
     * @throws NimException
     */
    public ProbeMain(String[] args) throws NimException {
        super(args, PROBE_NAME, PROBE_VERSION, PROBE_VENDOR);
        // Indicate this is a local probe
        setLocalMode(true);
    }

    /**
     * This is where you configure what you want to display in the probe configuration UI.
     * 
     * Note: Implementing this method is optional. If your probe does not require the ability to
     * specify configuration options in the Probe Configuration UI then you may skip implementing
     * this method.
     */
    @Override
    public void addDefaultProbeConfigurationToGraph() {
        // Add standard actions to add/delete/verify a profile
        ElementDef resDef = ElementDef.getElementDef("RESOURCE");
        resDef.addStandardAction(IProbeResourceTypeInfo.StandardActionType.DeleteProfileAction);
        resDef.addStandardAction(IProbeResourceTypeInfo.StandardActionType.VerifySelectionAction, "Verify Profile Configuration");
        resDef.addStandardAction(IProbeResourceTypeInfo.StandardActionType.AddProfileActionOnProbe, "Add Profile");
        
        // Set the properties that will be available when a new profile is created in the probe configuration UI
        CtdPropertyDefinitionsList profilePropDefs = CtdPropertyDefinitionsList.createCtdPropertyDefinitionsList("RESOURCE", getGraph());
        profilePropDefs.addStandardIdentifierProperty();
        profilePropDefs.addStandardAlarmMessageProperty();
        profilePropDefs.addStandardIntervalProperty();
        profilePropDefs.addStandardActiveProperty();

        // You must always invoke the super method
        super.addDefaultProbeConfigurationToGraph();
    }
 
    /**
     * Allows the user to test a profile configuration from the UI by using 
     * the pull down: Actions->verify.
     * </p>
     * All probe framework probes must implement this method.
     * </p>
     * The method should be implemented to validate the probe configuration. For example
     * if the configuration specifies remote system connectivity information. If unable 
     * to successfully verify the configuration then this method should throw an {@code Exception}
     * with a message about the nature of the problem.
     * </p>
     * Note: The tests performed here do not need to be limited to connectivity. You should verify
     * anything and everything related to your probe configuration.
     * 
     * Note: The methods {@code testResource()} and {@code getUpdatedInventory()} are both from the 
     * interface {@code IProbeInventoryCollection}. You will need to implement these methods if your 
     * probe implements {@code IProbeInventoryCollection}. Please see the JavaDoc on that interface 
     * for more details.
     * 
     * @param resource Configuration information.
     *
     * @return IInventoryDataset Optional. This is reserved for a future enhancement for an advanced probe 
     * to provide additional resource configuration information. However this is presently not used,
     * so the best practice is to return {@code null}
     *
     * @throws Exception if any errors are encountered during the testing of the configuration.
     */
    @Override
    public IInventoryDataset testResource(ResourceConfig res) throws NimException, InterruptedException {  
        Log.info("==== testResource: " + res.getName());
        
        /**
         * ***** Insert your test logic here *****
         * If your test is successful you need not do anything, simply 
         * allow this method to return null. If you need to report an error
         * then throw a NimException
         */
        
        // If we get to here then our tests were successful. Since we dont have 
        // any advanced information we wish to return we can simply return null
        return null;
    }
    
    /**
     * This is called by the framework on the inventory collection cycle. In this method 
     * we construct the inventory, and attach metrics. We always attach all metrics, and the
     * framework will determine which ones to publish based on how the probe is configured.
     * 
     * We return data to the framework in both the returned InventoryDataset, AND the passed
     * in ResourceConfig. Every inventory Element you create will be attached to the InventoryDataset,
     * those Elements must also be constructed in a hierarchy attached to ResourceConfig.
     * 
     * Note: The methods {@code testResource()} and {@code getUpdatedInventory()} are both from the 
     * interface {@code IProbeInventoryCollection}. You will need to implement these methods if your 
     * probe implements {@code IProbeInventoryCollection}. Please see the JavaDoc on that interface 
     * for more details.
     */
    @Override
    public IInventoryDataset getUpdatedInventory(ResourceConfig resourceConfig, IInventoryDataset previousDataset) throws NimException, InterruptedException {
        // A recommended best practice is to read configuration information
        // on each call to getUpdatedInventory(). This ensures configuration changes
        // take effect without the need for a full restart of the probe.
        // Also, please note that the configuration information is cached by the 
        // framework, so there is very low overhead here.
        int counter = resourceConfig.updateCounter;
        
        Log.info("==== Begin getUpdatedInventory: Pass-" + counter + "   " + resourceConfig.getName());
        
        // Create a new empty InventoryDataset
        InventoryDataset inventoryDataset = new InventoryDataset(resourceConfig);
        
        /**
         * ***** Insert your logic for populating the inventoryDataset here *****
         * The following few lines of code are provided simply as an example of 
         * how to create an inventory element, attach it to the ResourceConfig,
         * and set a Metric on it. 
         * When using this template to create a probe you should modify probe_schema.xml
         * to specify your inventory elements and metrics. 
         */
        Folder exampleFolder = Folder.addInstance(inventoryDataset, new EntityId(resourceConfig, "ExampleFolder"), "ExampleFolder", resourceConfig);
        ExampleGenericElement exampleElement = ExampleGenericElement.addInstance(inventoryDataset, new EntityId(exampleFolder, "ExampleElement"), "ExampleElement", exampleFolder);
        exampleElement.setMetric(ExampleGenericElement.ExampleMetric, 999);
        
        return inventoryDataset;
    }

}
