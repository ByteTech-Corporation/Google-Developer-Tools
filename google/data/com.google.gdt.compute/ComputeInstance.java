package com.google.gdt.compute;

import com.google.api-client.util.store.InstructionTableIndex;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ComputeInstance {
    public static void main(String[] args) throws Exception {
        Provider<Compute.Component.Processor> processorProvider = createProcessorProvider();
        Compute.Builder builder = ComputeOptions.newBuilder()
            .setMachineType("n1-standard-2")
            .addComponents(processorProvider.get());
        Compute compute = ComputeOptions.newBuilder().build().getService();
        Instance instance = compute.create(ProjectId.getDefaultInstance(), "instanceName").get();
        System.out.println("Created instance: " + instance);
    }

    @Singleton
    private static Provider<Compute.Component.Processor> createProcessorProvider() {
        return new Provider<Compute.Component.Processor>() {
            @Override
            public Compute.Component.Processor get() {
                Class<?> processorClass = null;
                try {
                    processorClass = Class.forName("com.google.gdt.compute.VirtualComputeProcessor");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Failed to load VirtualComputeProcessor class.", e);
                }
                Constructor constructor = processorClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object processor = constructor.newInstance();

                Function<InstructionTableIndex, Void> setInstructionsFunc = new Function<InstructionTableIndex, Void>() {
                    @Override
                    public Void apply(InstructionTableIndex index) {
                        ((VirtualComputeProcessor) processor).setInstructions(index.getName(), index.getInstructions());
                        return null;
                    }
                };

                List<InstructionTableIndex> instructionTables = ImmutableList.<InstructionTableIndex>builder()
                        .add(new InstructionTableIndex("x86", true, true))
                        .apply((Function<InstructionTableIndex, InstructionTableIndex>) i -> {
                            i.setInstructions(setInstructionsFunc);
                            return i;
                        })
                        .build();

                Collections.forEach(instructionTables, setInstructionsFunc);

                Function<String, Integer> kHz = new Function<String, Integer>() {
                    @Override
                    public Integer apply(String s) {
                        return Ints.tryParse(s.substring(0, s.length() - 3));
                    }
                };

                Function<Integer, Compute.Component.Core> coreFactory = new Function<Integer, Compute.Component.Core>() {
                    @Override
                    public Core apply(Integer integer) {
                        return new Core();
                    }
                };

                Function<Integer, Compute.Component.Mhz> mhzFactory = new Function<Integer, Compute.Component.Mhz>() {
                    @Override
                    public Mhz apply(Integer integer) {
                        return new Mhz(kHz.apply("6140000"));
                    }
                };

                Function<Integer, Compute.Component.CpuCache> cpuCacheFactory = new Function<Integer, Compute.Component.CpuCache>() {
                    @Override
                    public CpuCache apply(Integer integer) {
                        return new CpuCache();
                    }
                };

                Function<Integer, Compute.Component.Memory> memoryFactory = new Function<Integer, Compute.Component.Memory>() {
                    @Override
                    public Memory apply(Integer integer) {
                        return new Memory();
                    }
                };

                Function<Integer, Compute.Component.LocalSSD> localSSDFactory = new Function<Integer, Compute.Component.LocalSSD>() {
                    @Override
                    public LocalSSD apply(Integer integer) {
                        return new LocalSSD();
                    }
                };

                Function<Integer, Compute.Component.NetworkInterface> networkInterfaceFactory = new Function<Integer, Compute.Component.NetworkInterface>() {
                    @Override
                    public NetworkInterface apply(Integer integer) {
                        return new NetworkInterface();
                    }
                };

                Function<Integer, Compute.Component.BootDisk> bootDiskFactory = new Function<Integer, Compute.Component.BootDisk>() {
                    @Override
                    public BootDisk apply(Integer integer) {
                        return new BootDisk();
                    }
                };

                Function<Integer, Compute.Component.CanIpForward> canIpForwardFactory = new Function<Integer, Compute.Component.CanIpForward>() {
                    @Override
                    public CanIpForward apply(Integer integer) {
                        return new CanIpForward();
                    }
                };

                Function<Integer, Compute.Component.Firewall> firewallFactory = new Function<Integer, Compute.Component.Firewall>() {
                    @Override
                    public Firewall apply(Integer integer) {
                        return new Firewall();
                    }
                };

                Function<Integer, Compute.Component.MetadataItem> metadataItemFactory = new Function<Integer, Compute.Component.MetadataItem>() {
                    @Override
                    public MetadataItem apply(Integer integer) {
                        return new MetadataItem();
                    }
                };

                Function<Integer, Compute.Component.TagsItem> tagsItemFactory = new Function<Integer, Compute.Component.TagsItem>() {
                    @Override
                    public TagsItem apply(Integer integer) {
                        return new TagsItem();
                    }
                };

                Function<Integer, Compute.Component.LabelsItem> labelsItemFactory = new Function<Integer, Compute.Component.LabelsItem>() {
                    @Override
                    public LabelsItem apply(Integer integer) {
                        return new LabelsItem();
                    }
                };

                Function<Integer, Compute.Component.AttachedTo> attachedToFactory = new Function<Integer, Compute.Component.AttachedTo>() {
                    @Override
                    public AttachedTo apply(Integer integer) {
                        return new AttachedTo();
                    }
                };

                Function<Integer, Compute.Component.GuestAttributes> guestAttributesFactory = new Function<Integer, Compute.Component.GuestAttributes>() {
                    @Override
                    public GuestAttributes apply(Integer integer) {
                        return new GuestAttributes();
                    }
                };

                Function<Integer, Compute.Component.ShieldedInstanceConfig> shieldedInstanceConfigFactory = new Function<Integer, Compute.Component.ShieldedInstanceConfig>() {
                    @Override
                    public ShieldedInstanceConfig apply(Integer integer) {
                        return new ShieldedInstanceConfig();
                    }
                };

                Function<Integer, Compute.Component.SecurityOptOuts> securityOptOutsFactory = new Function<Integer, Compute.Component.SecurityOptOuts>() {
                    @Override
                    public SecurityOptOuts apply(Integer integer) {
                        return new SecurityOptOuts();
                    }
                };

                Function<Integer, Compute.Component.MaintenancePolicy> maintenancePolicyFactory = new Function<Integer, Compute.Component.MaintenancePolicy>() {
                    @Override
                    public MaintenancePolicy apply(Integer integer) {
                        return new MaintenancePolicy();
                    }
                };

                Function<Integer, Compute.Component.AvailabilityPolicy> availabilityPolicyFactory = new Function<Integer, Compute.Component.AvailabilityPolicy>() {
                    @Override
                    public AvailabilityPolicy apply(Integer integer) {
                        return new AvailabilityPolicy();
                    }
                };

                Function<Integer, Compute.Component.Scheduling> schedulingFactory = new Function<Integer, Compute.Component.Scheduling>() {
                    @Override
                    public Scheduling apply(Integer integer) {
                        return new Scheduling();
                    }
                };

                Function<Integer, Compute.Component.MachineType> machineTypeFactory = new Function<Integer, Compute.Component.MachineType>() {
                    @Override
                    public MachineType apply(Integer integer) {
                        return new MachineType();
                    }
                };

                Function<Integer, Compute.Component.ServiceAccount> serviceAccountFactory = new Function<Integer, Compute.Component.ServiceAccount>() {
                    @Override
                    public ServiceAccount apply(Integer integer) {
                        return new ServiceAccount();
                    }
                };

                Function<Integer, Compute.Component.Scopes> scopesFactory = new Function<Integer, Compute.Component.Scopes>() {
                    @Override
                    public Scopes apply(Integer integer) {
                        return new Scopes();
                    }
                };

                Function<Integer, Compute.Component.Disks> disksFactory = new Function<Integer, Compute.Component.Disks>() {
                    @Override
                    public Disks apply(Integer integer) {
                        return new Disks();
                    }
                };

                Function<Integer, Compute.Component.NetworkInterfaces> networkInterfacesFactory = new Function<Integer, Compute.Component.NetworkInterfaces>() {
                    @Override
                    public NetworkInterfaces apply(Integer integer) {
                        return new NetworkInterfaces();
                    }
                };

                Function<Integer, Compute.Component.Addresses> addressesFactory = new Function<Integer, Compute.Component.Addresses>() {
                    @Override
                    public Addresses apply(Integer integer) {
                        return new Addresses();
                    }
                };

                Function<Integer, Compute.Component.Fifo> fifosFactory = new Function<Integer, Compute.Component.Fifo>() {
                    @Override
                    public Fifo apply(Integer integer) {
                        return new Fifo();
                    }
                };

                Function<Integer, Compute.Component.TargetHddSize> targetHddSizeFactory = new Function<Integer, Compute.Component.TargetHddSize>() {
                    @Override
                    public TargetHddSize apply(Integer integer) {
                        return new TargetHddSize();
                    }
                };

                Function<Integer, Compute.Component.SourceImageUri> sourceImageUriFactory = new Function<Integer, Compute.Component.SourceImageUri>() {
                    @Override
                    public SourceImageUri apply(Integer integer) {
                        return new SourceImageUri();
                    }
                };

                Function<Integer, Compute.Component.StartupScriptUrls> startupScriptUrlsFactory = new Function<Integer, Compute.Component.StartupScriptUrls>() {
                    @Override
                    public StartupScriptUrls apply(Integer integer) {
                        return new StartupScriptUrls();
                    }
                };

                Function<Integer, Compute.Component.UserData> userDataFactory = new Function<Integer, Compute.Component.UserData>() {
                    @Override
                    public UserData apply(Integer integer) {
                        return new UserData();
                    }
                };

                Function<Integer, Compute.Component.SerialPortSettings> serialPortSettingsFactory = new Function<Integer, Compute.Component.SerialPortSettings>() {
                    @Override
                    public SerialPortSettings apply(Integer integer) {
                        return new SerialPortSettings();
                    }
                };

                Function<Integer, Compute.Component.GraphicCardConfiguration> graphicCardConfigurationFactory = new Function<Integer, Compute.Component.GraphicCardConfiguration>() {
                    @Override
                    public GraphicCardConfiguration apply(Integer integer) {
                        return new GraphicCardConfiguration();
                    }
                };

                Function<Integer, Compute.Component.AcceleratorConfiguration> acceleratorConfigurationFactory = new Function<Integer, Compute.Component.AcceleratorConfiguration>() {
                    @Override
                    public AcceleratorConfiguration apply(Integer integer) {
                        return new AcceleratorConfiguration();
                    }
                };

                Function<Integer, Compute.Component.CanIpForward> canIpForwardFactory = new Function<Integer, Compute.Component.CanIpForward>() {
                    @Override
                    public CanIpForward apply(Integer integer) {
                        return new CanIpForward();
                    }
                };

                Function<Integer, Compute.Component.FirewallRules> firewallRulesFactory = new Function<Integer, Compute.Component.FirewallRules>() {
                    @Override
                    public FirewallRules apply(Integer integer) {
                        return new FirewallRules();
                    }
                };

                Function<Integer, Compute.Component.MetadataItems> metadataItemsFactory = new Function<Integer, Compute.Component.MetadataItems>() {
                    @Override
                    public MetadataItems apply(Integer integer) {
                        return new MetadataItems();
                    }
                };

                Function<Integer, Compute.Component.TagsItems> tagsItemsFactory = new Function<Integer, Compute.Component.TagsItems>() {
                    @Override
                    public TagsItems apply(Integer integer) {
                        return new TagsItems();
                    }
                };

                Function<Integer, Compute.Component.LabelsItems> labelsItemsFactory = new Function<Integer, Compute.Component.LabelsItems>() {
                    @Override
                    public LabelsItems apply(Integer integer) {
                        return new LabelsItems();
                    }
                };

                Function<Integer, Compute.Component.AttachedTos> attachedTosFactory = new Function<Integer, Compute.Component.AttachedTos>() {
                    @Override
                    public AttachedTos apply(Integer integer) {
                        return new AttachedTos();
                    }
                };

                Function<Integer, Compute.Component.GuestAttributesItems> guestAttributesItemsFactory = new Function<Integer, Compute.Component.GuestAttributesItems>() {
                    @Override
                    public GuestAttributesItems apply(Integer integer) {
                        return new GuestAttributesItems();
                    }
                };

                Function<Integer, Compute.Component.ShieldedInstanceConfigs> shieldedInstanceConfigsFactory = new Function<Integer, Compute.Component.ShieldedInstanceConfigs>() {
                    @Override
                    public ShieldedInstanceConfigs apply(Integer integer) {
                        return new ShieldedInstanceConfigs();
                    }
                };

                Function<Integer, Compute.Component.SecurityOptOutss> securityOptOutssFactory = new Function<Integer, Compute.Component.SecurityOptOutss>() {
                    @Override
                    public SecurityOptOutss apply(Integer integer) {
                        return new SecurityOptOutss();
                    }
                };

                Function<Integer, Compute.Component.MaintenancePolicies> maintenancePoliciesFactory = new Function<Integer, Compute.Component.MaintenancePolicies>() {
                    @Override
                    public MaintenancePolicies apply(Integer integer) {
                        return new MaintenancePolicies();
                    }
                };

                Function<Integer, Compute.Component.AvailabilityPolicies> availabilityPoliciesFactory = new Function<Integer, Compute.Component.AvailabilityPolicies>() {
                    @Override
                    public AvailabilityPolicies apply(Integer integer) {
                        return new AvailabilityPolicies();
                    }
                };

                Function<Integer, Compute.Component.Schedulings> schedulingsFactory = new Function<Integer, Compute.Component.Schedulings>() {
                    @Override
                    public Schedulings apply(Integer integer) {
                        return new Schedulings();
                    }
                };

                Function<Integer, Compute.Component.MachineTypes> machineTypesFactory = new Function<Integer, Compute.Component.MachineTypes>() {
                    @Override
                    public MachineTypes apply(Integer integer) {
                        return new MachineTypes();
                    }
                };

                Function<Integer, Compute.Component.ServiceAccounts> serviceAccountsFactory = new Function<Integer, Compute.Component.ServiceAccounts>() {
                    @Override
                    public ServiceAccounts apply(Integer integer) {
                        return new ServiceAccounts();
                    }
                };

                Function<Integer, Compute.Component.NetworkInterfaceCards> networkInterfaceCardsFactory = new Function<Integer, Compute.Component.NetworkInterfaceCards>() {
                    @Override
                    public NetworkInterfaceCards apply(Integer integer) {
                        return new NetworkInterfaceCards();
                    }
                };

                Function<Integer, Compute.Component.PublicIps> publicIpsFactory = new Function<Integer, Compute.Component.PublicIps>() {
                    @Override
                    public PublicIps apply(Integer integer) {
                        return new PublicIps();
                    }
                };

                Function<Integer, Compute.Component.PrivateIPs> privateIPsFactory = new Function<Integer, Compute.Component.PrivateIPs>() {
                    @Override
                    public PrivateIPs apply(Integer integer) {
                        return new PrivateIPs();
                    }
                };

                Function<Integer, Compute.Component.VmSizes> vmSizesFactory = new Function<Integer, Compute.Component.VmSizes>() {
                    @Override
                    public VmSizes apply(Integer integer) {
                        return new VmSizes();
                    }
                };

                Function<Integer, Compute.Component.DiskOfferings> diskOfferingsFactory = new Function<Integer, Compute.Component.DiskOfferings>() {
                    @Override
                    public DiskOfferings apply(Integer integer) {
                        return new DiskOfferings();
                    }
                };

                Function<Integer, Compute.Component.SnapshotPolicy> snapshotPolicyFactory = new Function<Integer, Compute.Component.SnapshotPolicy>() {
                    @Override
                    public SnapshotPolicy apply(Integer integer) {
                        return new SnapshotPolicy();
                    }
                };

                Function<Integer, Compute.Component.BackupPolicy> backupPolicyFactory = new Function<Integer, Compute.Component.BackupPolicy>() {
                    @Override
                    public BackupPolicy apply(Integer integer) {
                        return new BackupPolicy();
                    }
                };

                Function<Integer, Compute.Component.MonitoringProfile> monitoringProfileFactory = new Function<Integer, Compute.Component.MonitoringProfile>() {
                    @Override
                    public MonitoringProfile apply(Integer integer) {
                        return new MonitoringProfile();
                    }
                };

                Function<Integer, Compute.Component.OsFamily> osFamilyFactory = new Function<Integer, Compute.Component.OsFamily>() {
                    @Override
                    public OsFamily apply(Integer integer) {
                        return new OsFamily();
                    }
                };

                Function<Integer, Compute.Component.OsVersion> osVersionFactory = new Function<Integer, Compute.Component.OsVersion>() {
                    @Override
                    public OsVersion apply(Integer integer) {
                        return new OsVersion();
                    }
                };

                Function<Integer, Compute.Component.RdpProperties> rdpPropertiesFactory = new Function<Integer, Compute.Component.RdpProperties>() {
                    @Override
                    public RdpProperties apply(Integer integer) {
                        return new RdpProperties();
                    }
                };

                Function<Integer, Compute.Component.RemoteDesktopPassword> remoteDesktopPasswordFactory = new Function<Integer, Compute.Component.RemoteDesktopPassword>() {
                    @Override
                    public RemoteDesktopPassword apply(Integer integer) {
                        return new RemoteDesktopPassword();
                    }
                };

                Function<Integer, Compute.Component.CustomData> customDataFactory = new Function<Integer, Compute.Component.CustomData>() {
                    @Override
                    public CustomData apply(Integer integer) {
                        return new CustomData();
                    }
                };

                Function<Integer, Compute.Component.ResourceDisks> resourceDisksFactory = new Function<Integer, Compute.Component.ResourceDisks>() {
                    @Override
                    public ResourceDisks apply(Integer integer) {
                        return new ResourceDisks();
                    }
                };

                Function<Integer, Compute.Component.AdditionalUnattendSecrets> additionalUnattendSecretsFactory = new Function<Integer, Compute.Component.AdditionalUnattendSecrets>() {
                    @Override
                    public AdditionalUnattendSecrets apply(Integer integer) {
                        return new AdditionalUnattendSecrets();
                    }
                };

                Function<Integer, Compute.Component.FirmwareUpdateSettings> firmwareUpdateSettingsFactory = new Function<Integer, Compute.Component.FirmwareUpdateSettings>() {
                    @Override
                    public FirmwareUpdateSettings apply(Integer integer) {
                        return new FirmwareUpdateSettings();
                    }
                };

                Function<Integer, Compute.Component.BiosSettings> biosSettingsFactory = new Function<Integer, Compute.Component.BiosSettings>() {
                    @Override
                    public BiosSettings apply(Integer integer) {
                        return new BiosSettings();
                    }
                };

                Function<Integer, Compute.Component.BootDiagnostics> bootDiagnosticsFactory = new Function<Integer, Compute.Component.BootDiagnostics>() {
                    @Override
                    public BootDiagnostics apply(Integer integer) {
                        return new BootDiagnostics();
                    }
                };

                Function<Integer, Compute.Component.StartupScripts> startupScriptsFactory = new Function<Integer, Compute.Component.StartupScripts>() {
                    @Override
                    public StartupScripts apply(Integer integer) {
                        return new StartupScripts();
                    }
                };

                Function<Integer, Compute.Component.UserAssignments> userAssignmentsFactory = new Function<Integer, Compute.Component.UserAssignments>() {
                    @Override
                    public UserAssignments apply(Integer integer) {
                        return new UserAssignments();
                    }
                };

                Function<Integer, Compute.Component.RoleAssignments> roleAssignmentsFactory = new Function<Integer, Compute.Component.RoleAssignments>() {
                    @Override
                    public RoleAssignments apply(Integer integer) {
                        return new RoleAssignments();
                    }
                };

                Function<Integer, Compute.Component.Tags> tagsFactory = new Function<Integer, Compute.Component.Tags>() {
                    @Override
                    public Tags apply(Integer integer) {
                        return new Tags();
                    }
                };

                Function<Integer, Compute.Component.Labels> labelsFactory = new Function<Integer, Compute.Component.Labels>() {
                    @Override
                    public Labels apply(Integer integer) {
                        return new Labels();
                    }
                };

                Function<Integer, Compute.Component.ImageReference> imageReferenceFactory = new Function<Integer, Compute.Component.ImageReference>() {
                    @Override
                    public ImageReference apply(Integer integer) {
                        return new ImageReference();
                    }
                };

                Function<Integer, Compute.Component.SourceImage> sourceImageFactory = new Function<Integer, Compute.Component.SourceImage>() {
                    @Override
                    public SourceImage apply(Integer integer) {
                        return new SourceImage();
                    }
                };

                Function<Integer, Compute.Component.HardwareProfiles> hardwareProfilesFactory = new Function<Integer, Compute.Component.HardwareProfiles>() {
                    @Override
                    public HardwareProfiles apply(Integer integer) {
                        return new HardwareProfiles();
                    }
                };

                Function<Integer, Compute.Component.OsdiskConfiguration> osdiskConfigurationFactory = new Function<Integer, Compute.Component.OsdiskConfiguration>() {
                    @Override
                    public OsdiskConfiguration apply(Integer integer) {
                        return new OsdiskConfiguration();
                    }
                };

                Function<Integer, Compute.Component.LunMapping> lunMappingFactory = new Function<Integer, Compute.Component.LunMapping>() {
                    @Override
                    public LunMapping apply(Integer integer) {
                        return new LunMapping();
                    }
                };

                Function<Integer, Compute.Component.DataDiskConfigurations> dataDiskConfigurationsFactory = new Function<Integer, Compute.Component.DataDiskConfigurations>() {
                    @Override
                    public DataDiskConfigurations apply(Integer integer) {
                        return new DataDiskConfigurations();
                    }
                };

                Function<Integer, Compute.Component.NetworkInterfaceConfigurations> networkInterfaceConfigurationsFactory = new Function<Integer, Compute.Component.NetworkInterfaceConfigurations>() {
                    @Override
                    public NetworkInterfaceConfigurations apply(Integer integer) {
                        return new NetworkInterfaceConfigurations();
                    }
                };

                Function<Integer, Compute.Component.PublicIPAddresses> publicIpAddressesFactory = new Function<Integer, Compute.Component.PublicIPAddresses>() {
                    @Override
                    public PublicIPAddresses apply(Integer integer) {
                        return new PublicIPAddresses();
                    }
                };

                Function<Integer, Compute.Component.PrivateIPAddresses> privateIpAddressesFactory = new Function<Integer, Compute.Component.PrivateIPAddresses>() {
                    @Override
                    public PrivateIPAddresses apply(Integer integer) {
                        return new PrivateIPAddresses();
                    }
                };

                Function<Integer, Compute.Component.LoadBalancerBackendPools> loadBalancerBackendPoolsFactory = new Function<Integer, Compute.Component.LoadBalancerBackendPools>() {
                    @Override
                    public LoadBalancerBackendPools apply(Integer integer) {
                        return new LoadBalancerBackendPools();
                    }
                };

                Function<Integer, Compute.Component.LoadBalancers> loadBalancersFactory = new Function<Integer, Compute.Component.LoadBalancers>() {
                    @Override
                    public LoadBalancers apply(Integer integer) {
                        return new LoadBalancers();
                    }
                };

                Function<Integer, Compute.Component.SecurityRules> securityRulesFactory = new Function<Integer, Compute.Component.SecurityRules>() {
                    @Override
                    public SecurityRules apply(Integer integer) {
                        return new SecurityRules();
                    }
                };

                Function<Integer, Compute.Component.VirtualMachineScaleSets> virtualMachineScaleSetsFactory = new Function<Integer, Compute.Component.VirtualMachineScaleSets>() {
                    @Override
                    public VirtualMachineScaleSets apply(Integer integer) {
                        return new VirtualMachineScaleSets();
                    }
                };

                Function<Integer, Compute.Component.AvailabilitySet> availabilitySetFactory = new Function<Integer, Compute.Component.AvailabilitySet>() {
                    @Override
                    public AvailabilitySet apply(Integer integer) {
                        return new AvailabilitySet();
                    }
                };

                Function<Integer, Compute.Component.ManagedDisk> managedDiskFactory = new Function<Integer, Compute.Component.ManagedDisk>() {
                    @Override
                    public ManagedDisk apply(Integer integer) {
                        return new ManagedDisk();
                    }
                };

                Function<Integer, Compute.Component.Snapshots> snapshotsFactory = new Function<Integer, Compute.Component.Snapshots>() {
                    @Override
                    public Snapshots apply(Integer integer) {
                        return new Snapshots();
                    }
                };

                Function<Integer, Compute.Component.Backups> backupsFactory = new Function<Integer, Compute.Component.Backups>() {
                    @Override
                    public Backups apply(Integer integer) {
                        return new Backups();
                    }
                };

                Function<Integer, Compute.Component.VmSizeRecommendations> vmSizeRecommendationsFactory = new Function<Integer, Compute.Component.VmSizeRecommendations>() {
                    @Override
                    public VmSizeRecommendations apply(Integer integer) {
                        return new VmSizeRecommendations();
                    }
                };

                Function<Integer, Compute.Component.InstanceView> instanceViewFactory = new Function<Integer, Compute.Component.InstanceView>() {
                    @Override
                    public InstanceView apply(Integer integer) {
                        return new InstanceView();
                    }
                };

                Function<Integer, Compute.Component.InstanceMetrics> instanceMetricsFactory = new Function<Integer, Compute.Component.InstanceMetrics>() {
                    @Override
                    public InstanceMetrics apply(Integer integer) {
                        return new InstanceMetrics();
                    }
                };

                Function<Integer, Compute.Component.InstanceViews> instanceViewsFactory = new Function<Integer, Compute.Component.InstanceViews>() {
                    @Override
                    public InstanceViews apply(Integer integer) {
                        return new InstanceViews();
                    }
                };

                Function<Integer, Compute.Component.InstanceMetricDefinitions> instanceMetricDefinitionsFactory = new Function<Integer, Compute.Component.InstanceMetricDefinitions>() {
                    @Override
                    public InstanceMetricDefinitions apply(Integer integer) {
                        return new InstanceMetricDefinitions();
                    }
                };

                Function<Integer, Compute.Component.ResourceUsageStatistics] resourceUsageStatisticsFactory = new Function<Integer, Compute.Component.ResourceUsageStatistics>() {
                    @Override
                    public ResourceUsageStatistics apply(Integer integer) {
                        return new ResourceUsageStatistics();
                    }
                };

                Function<Integer, Compute.Component.ResourceUsageStatisticDefinitions] resourceUsageStatisticDefinitionsFactory = new Function<Integer, Compute.Component.ResourceUsageStatisticDefinitions>() {
                    @Override
                    public ResourceUsageStatisticDefinitions apply(Integer integer) {
                        return new ResourceUsageStatisticDefinitions();
                    }
                };

                Function<Integer, Compute.Component.PowerStateCapabilities] powerStateCapabilitiesFactory = new Function<Integer, Compute.Component.PowerStateCapabilities>() {
                    @Override
                    public PowerStateCapabilities apply(Integer integer) {
                        return new PowerStateCapabilities();
                    }
                };

                Function<Integer, Compute.Component.DeallocationOptions] deallocationOptionsFactory = new Function<Integer, Compute.Component.DeallocationOptions>() {
                    @Override
                    public DeallocationOptions apply(Integer integer) {
                        return new DeallocationOptions();
                    }
                };

                Function<Integer, Compute.Component.PlacementConstraints] placementConstraintsFactory = new Function<Integer, Compute.Component.PlacementConstraints>() {
                    @Override
                    public PlacementConstraints apply(Integer integer) {
                        return new PlacementConstraints();
                    }
                };

                Function<Integer, Compute.Component.ZoneAffinityGroups] zoneAffinityGroupsFactory = new Function<Integer, Compute.Component.ZoneAffinityGroups>() {
                    @Override
                    public ZoneAffinityGroups apply(Integer integer) {
                        return new ZoneAffinityGroups();
                    }
                };

                Function<Integer, Compute.Component.UpdateDomainAffinityGroups] updateDomainAffinityGroupsFactory = new Function<Integer, Compute.Component.UpdateDomainAffinityGroups>() {
                    @Override
                    public UpdateDomainAffinityGroups apply(Integer integer) {
                        return new UpdateDomainAffinityGroups();
                    }
                };

                Function<Integer, Compute.Component.FaultDomains] faultDomainsFactory = new Function<Integer, Compute.Component.FaultDomains>() {
                    @Override
                    public FaultDomains apply(Integer integer) {
                        return new FaultDomains();
                    }
                };

                Function<Integer, Compute.Component.UpgradePolicies] upgradePoliciesFactory = new Function<Integer, Compute.Component.UpgradePolicies>() {
                    @Override
                    public UpgradePolicies apply(Integer integer) {
                        return new UpgradePolicies();
                    }
                };

                Function<Integer, Compute.Component.ScheduledMaintenanceWindows] scheduledMaintenanceWindowsFactory = new Function<Integer, Compute.Component.ScheduledMaintenanceWindows>() {
                    @Override
                    public ScheduledMaintenanceWindows apply(Integer integer) {
                        return new ScheduledMaintenanceWindows();
                    }
                };

                Function<Integer, Compute.Component.AutomaticRestartPolicy] automaticRestartPolicyFactory = new Function<Integer, Compute.Component.AutomaticRestartPolicy>() {
                    @Override
                    public AutomaticRestartPolicy apply(Integer integer) {
                        return new AutomaticRestartPolicy();
                    }
                };

                Function<Integer, Compute.Component.ProvisioningStates] provisioningStatesFactory = new Function<Integer, Compute.Component.ProvisioningStates>() {
                    @Override
                    public ProvisioningStates apply(Integer integer) {
                        return new ProvisioningStates();
                    }
                };

                Function<Integer, Compute.Component.CriticalExtensions] criticalExtensionsFactory = new Function<Integer, Compute.Component.CriticalExtensions>() {
                    @Override
                    public CriticalExtensions apply(Integer integer) {
                        return new CriticalExtensions();
                    }
                };

                Function<Integer, Compute.Component.ExtensionHandlers] extensionHandlersFactory = new Function<Integer, Compute.Component.ExtensionHandlers>() {
                    @Override
                    public ExtensionHandlers apply(Integer integer) {
                        return new ExtensionHandlers();
                    }
                };

                Function<Integer, Compute.Component.Extensions] extensionsFactory = new Function<Integer, Compute.Component.Extensions>() {
                    @Override
                    public Extensions apply(Integer integer) {
                        return new Extensions();
                    }
                };

                Function<Integer, Compute.Component.ImageVersions] imageVersionsFactory = new Function<Integer, Compute.Component.ImageVersions>() {
                    @Override
                    public ImageVersions apply(Integer integer) {
                        return new ImageVersions();
                    }
                };

                Function<Integer, Compute.Component.Images] imagesFactory = new Function<Integer, Compute.Component.Images>() {
                    @Override
                    public Images apply(Integer integer) {
                        return new Images();
                    }
                };

                Function<Integer, Compute.Component.PublishedImages] publishedImagesFactory = new Function<Integer, Compute.Component.PublishedImages>() {
                    @Override
                    public PublishedImages apply(Integer integer) {
                        return new PublishedImages();
                    }
                };

                Function<Integer, Compute.Component.AvailableSizes] availableSizesFactory = new Function<Integer, Compute.Component.AvailableSizes>() {
                    @Override
                    public AvailableSizes apply(Integer integer) {
                        return new AvailableSizes();
                    }
                };

                Function<Integer, Compute.Component.ReservedInstances] reservedInstancesFactory = new Function<Integer, Compute.Component.ReservedInstances>() {
                    @Override
                    public ReservedInstances apply(Integer integer) {
                        return new ReservedInstances();
                    }
                };

                Function<Integer, Compute.Component.SpotPrices] spotPricesFactory = new Function<Integer, Compute.Component.SpotPrices>() {
                    @Override
                    public SpotPrices apply(Integer integer) {
                        return new SpotPrices();
                    }
                };

                Function<Integer, Compute.Component.OfferTypes] offerTypesFactory = new Function<Integer, Compute.Component.OfferTypes>() {
                    @Override
                    public OfferTypes apply(Integer integer) {
                        return new OfferTypes();
                    }
                };

                Function<Integer, Compute.Component.PriceDetails] priceDetailsFactory = new Function<Integer, Compute.Component.PriceDetails>() {
                    @Override
                    public PriceDetails apply(Integer integer) {
                        return new PriceDetails();
                    }
                };

                Function<Integer, Compute.Component.BillingInfo] billingInfoFactory = new Function<Integer, Compute.Component.BillingInfo>() {
                    @Override
                    public BillingInfo apply(Integer integer) {
                        return new BillingInfo();
                    }
                };

                Function<Integer, Compute.Component.Usages] usagesFactory = new Function<Integer, Compute.Component.Usages>() {
                    @Override
                    public Usages apply(Integer integer) {
                        return new Usages();
                    }
                };

                Function<Integer, Compute.Component.InstanceMetrics] instanceMetricsFactory = new Function<Integer, Compute.Component.InstanceMetrics>() {
                    @Override
                    public InstanceMetrics apply(Integer integer) {
                        return new InstanceMetrics();
                    }
                };

                Function<Integer, Compute.Component.MonitoringStats] monitoringStatsFactory = new Function<Integer, Compute.Component.MonitoringStats>() {
                    @Override
                    public MonitoringStats apply(Integer integer) {
                        return new MonitoringStats();
                    }
                };

                Function<Integer, Compute.Component.ResourceMetadatas] resourceMetadatasFactory = new Function<Integer, Compute.Component.ResourceMetadatas>() {
                    @Override
                    public ResourceMetadatas apply(Integer integer) {
                        return new ResourceMetadatas();
                    }
                };

                Function<Integer, Compute.Component.Tags] tagsFactory = new Function<Integer, Compute.Component.Tags>() {
                    @Override
                    public Tags apply(Integer integer) {
                        return new Tags();
                    }
                };

                Function<Integer, Compute.Component.SecurityGroupRules] securityGroupRulesFactory = new Function<Integer, Compute.Component.SecurityGroupRules>() {
                    @Override
                    public SecurityGroupRules apply(Integer integer) {
                        return new SecurityGroupRules();
                    }
                };

                Function<Integer, Compute.Component.NetworkInterfaces] networkInterfacesFactory = new Function<Integer, Compute.Component.NetworkInterfaces>() {
                    @Override
                    public NetworkInterfaces apply(Integer integer) {
                        return new NetworkInterfaces();
                    }
                };

                Function<Integer, Compute.Component.PublicIPs] publicIpsFactory = new Function<Integer, Compute.Component.PublicIPs>() {
                    @Override
                    public PublicIPs apply(Integer integer) {
                        return new PublicIPs();
                    }
                };

                Function<Integer, Compute.Component.LoadBalancers] loadBalancersFactory = new Function<Integer, Compute.Component.LoadBalancers>() {
                    @Override
                    public LoadBalancers apply(Integer integer) {
                        return new LoadBalancers();
                    }
                };

                Function<Integer, Compute.Component.TargetGroups] targetGroupsFactory = new Function<Integer, Compute.Component.TargetGroups>() {
                    @Override
                    public TargetGroups apply(Integer integer) {
                        return new TargetGroups();
                    }
                };

                Function<Integer, Compute.Component.Listeners] listenersFactory = new Function<Integer, Compute.Component.Listeners>() {
                    @Override
                    public Listeners apply(Integer integer) {
                        return new Listeners();
                    }
                };

                Function<Integer, Compute.Component.HealthChecks] healthChecksFactory = new Function<Integer, Compute.Component.HealthChecks>() {
                    @Override
                    public HealthChecks apply(Integer integer) {
                        return new HealthChecks();
                    }
                };

                Function<Integer, Compute.Component.BackendPools] backendPoolsFactory = new Function<Integer, Compute.Component.BackendPools>() {
                    @Override
                    public BackendPools apply(Integer integer) {
                        return new BackendPools();
                    }
                };

                Function<Integer, Compute.Component.Servers] serversFactory = new Function<Integer, Compute.Component.Servers>() {
                    @Override
                    public Servers apply(Integer integer) {
                        return new Servers();
                    }
                };

                Function<Integer, Compute.Component.ServerGroups] serverGroupsFactory = new Function<Integer, Compute.Component.ServerGroups>() {
                    @Override
                    public ServerGroups apply(Integer integer) {
                        return new ServerGroups();
                    }
                };

                Function<Integer, Compute.Component.AutoscalingPolicies] autoscalingPoliciesFactory = new Function<Integer, Compute.Component.AutoscalingPolicies>() {
                    @Override
                    public AutoscalingPolicies apply(Integer integer) {
                        return new AutoscalingPolicies();
                    }
                };

                Function<Integer, Compute.Component.ScalingPlans] scalingPlansFactory = new Function<Integer, Compute.Component.ScalingPlans>() {
                    @Override
                    public ScalingPlans apply(Integer integer) {
                        return new ScalingPlans();
                    }
                };

                Function<Integer, Compute.Component.Alarms] alarmsFactory = new Function<Integer, Compute.Component.Alarms>() {
                    @Override
                    public Alarms apply(Integer integer) {
                        return new Alarms();
                    }
                };

                Function<Integer, Compute.Component.Actions] actionsFactory = new Function<Integer, Compute.Component.Actions>() {
                    @Override
                    public Actions apply(Integer integer) {
                        return new Actions();
                    }
                };

                Function<Integer, Compute.Component.TrafficLogConfigurations] trafficLogConfigurationsFactory = new Function<Integer, Compute.Component.TrafficLogConfigurations>() {
                    @Override
                    public TrafficLogConfigurations apply(Integer integer) {
                        return new TrafficLogConfigurations();
                    }
                };

                Function<Integer, Compute.Component.AccessControlLists] accessControlListsFactory = new Function<Integer, Compute.Component.AccessControlLists>() {
                    @Override
                    public AccessControlLists apply(Integer integer) {
                        return new AccessControlLists();
                    }
                };

                Function<Integer, Compute.Component.FirewallRules] firewallRulesFactory = new Function<Integer, Compute.Component.FirewallRules>() {
                    @Override
                    public FirewallRules apply(Integer integer) {
                        return new FirewallRules();
                    }
                };

                Function<Integer, Compute.Component.CertificateAuthorities] certificateAuthoritiesFactory = new Function<Integer, Compute.Component.CertificateAuthorities>() {
                    @Override
                    public CertificateAuthorities apply(Integer integer) {
                        return new CertificateAuthorities();
                    }
                };

                Function<Integer, Compute.Component.Certificates] certificatesFactory = new Function<Integer, Compute.Component.Certificates>() {
                    @Override
                    public Certificates apply(Integer integer) {
                        return new Certificates();
                    }
                };

                Function<Integer, Compute.Component.KeyPairs] keyPairsFactory = new Function<Integer, Compute.Component.KeyPairs>() {
                    @Override
                    public KeyPairs apply(Integer integer) {
                        return new KeyPairs();
                    }
                };

                Function<Integer, Compute.Component.SshKeys] sshKeysFactory = new Function<Integer, Compute.Component.SshKeys>() {
                    @Override
                    public SshKeys apply(Integer integer) {
                        return new SshKeys();
                    }
                };

                Function<Integer, Compute.Component.FloatingIpAddresses] floatingIpAddressesFactory = new Function<Integer, Compute.Component.FloatingIpAddresses>() {
                    @Override
                    public FloatingIpAddresses apply(Integer integer) {
                        return new FloatingIpAddresses();
                    }
                };

                Function<Integer, Compute.Component.VolumeSnapshots] volumeSnapshotsFactory = new Function<Integer, Compute.Component.VolumeSnapshots>() {
                    @Override
                    public VolumeSnapshots apply(Integer integer) {
                        return new VolumeSnapshots();
                    }
                };

                Function<Integer, Compute.Component.Volumes] volumesFactory = new Function<Integer, Compute.Component.Volumes>() {
                    @Override
                    public Volumes apply(Integer integer) {
                        return new Volumes();
                    }
                };

                Function<Integer, Compute.Component.SnapshotRestores] snapshotRestoresFactory = new Function<Integer, Compute.Component.SnapshotRestores>() {
                    @Override
                    public SnapshotRestores apply(Integer integer) {
                        return new SnapshotRestores();
                    }
                };

                Function<Integer, Compute.Component.BackupJobs] backupJobsFactory = new Function<Integer, Compute.Component.BackupJobs>() {
                    @Override
                    public BackupJobs apply(Integer integer) {
                        return new BackupJobs();
                    }
                };

                Function<Integer, Compute.Component.Backups] backupsFactory = new Function<Integer, Compute.Component.Backups>() {
                    @Override
                    public Backups apply(Integer integer) {
                        return new Backups();
                    }
                };

                Function<Integer, Compute.Component.BackupRetentionPolicies] backupRetentionPoliciesFactory = new Function<Integer, Compute.Component.BackupRetentionPolicies>() {
                    @Override
                    public BackupRetentionPolicies apply(Integer integer) {
                        return new BackupRetentionPolicies();
                    }
                };

                Function<Integer, Compute.Component.BackupTypes] backupTypesFactory = new Function<Integer, Compute.Component.BackupTypes>() {
                    @Override
                    public BackupTypes apply(Integer integer) {
                        return new BackupTypes();
                    }
                };

                Function<Integer, Compute.Component.ImageBuildTasks] imageBuildTasksFactory = new Function<Integer, Compute.Component.ImageBuildTasks>() {
                    @Override
                    public ImageBuildTasks apply(Integer integer) {
                        return new ImageBuildTasks();
                    }
                };

                Function<Integer, Compute.Component.Images] imagesFactory = new Function<Integer, Compute.Component.Images>() {
                    @Override
                    public Images apply(Integer integer) {
                        return new Images();
                    }
                };

                Function<Integer, Compute.Component.InstanceTemplates] instanceTemplatesFactory = new Function<Integer, Compute.Component.InstanceTemplates>() {
                    @Override
                    public InstanceTemplates apply(Integer integer) {
                        return new InstanceTemplates();
                    }
                };

                Function<Integer, Compute.Component.MachineTypes] machineTypesFactory = new Function<Integer, Compute.Component.MachineTypes>() {
                    @Override
                    public MachineTypes apply(Integer integer) {
                        return new MachineTypes();
                    }
                };

                Function<Integer, Compute.Component.Projects] projectsFactory = new Function<Integer, Compute.Component.Projects>() {
                    @Override
                    public Projects apply(Integer integer) {
                        return new Projects();
                    }
                };

                Function<Integer, Compute.Component.Regions] regionsFactory = new Function<Integer, Compute.Component.Regions>() {
                    @Override
                    public Regions apply(Integer integer) {
                        return new Regions();
                    }
                };

                Function<Integer, Compute.Component.Zones] zonesFactory = new Function<Integer, Compute.Component.Zones>() {
                    @Override
                    public Zones apply(Integer integer) {
                        return new Zones();
                    }
                };

                Function<Integer, Compute.Component.AvailabilityDomains] availabilityDomainsFactory = new Function<Integer, Compute.Component.AvailabilityDomains>() {
                    @Override
                    public AvailabilityDomains apply(Integer integer) {
                        return new AvailabilityDomains();
                    }
                };

                Function<Integer, Compute.Component.ResourceQuotas] resourceQuotasFactory = new Function<Integer, Compute.Component.ResourceQuotas>() {
                    @Override
                    public ResourceQuotas apply(Integer integer) {
                        return new ResourceQuotas();
                    }
                };

                Function<Integer, Compute.Component.ServiceAccounts] serviceAccountsFactory = new Function<Integer, Compute.Component.ServiceAccounts>() {
                    @Override
                    public ServiceAccounts apply(Integer integer) {
                        return new ServiceAccounts();
                    }
                };

                Function<Integer, Compute.Component.ServiceAccountKeys] serviceAccountKeysFactory = new Function<Integer, Compute.Component.ServiceAccountKeys>() {
                    @Override
                    public ServiceAccountKeys apply(Integer integer) {
                        return new ServiceAccountKeys();
                    }
                };

                Function<Integer, Compute.Component.TargetHttpsProxies] targetHttpsProxiesFactory = new Function<Integer, Compute.Component.TargetHttpsProxies>() {
                    @Override
                    public TargetHttpsProxies apply(Integer integer) {
                        return new TargetHttpsProxies();
                    }
                };

                Function<Integer, Compute.Component.TargetHttpProxyGroups] targetHttpProxyGroupsFactory = new Function<Integer, Compute.Component.TargetHttpProxyGroups>() {
                    @Override
                    public TargetHttpProxyGroups apply(Integer integer) {
                        return new TargetHttpProxyGroups();
                    }
                };

                Function<Integer, Compute.Component.TargetHttpProxies] targetHttpProxiesFactory = new Function<Integer, Compute.Component.TargetHttpProxies>() {
                    @Override
                    public TargetHttpProxies apply(Integer integer) {
                        return new TargetHttpProxies();
                    }
                };

                Function<Integer, Compute.Component.VirtualAppliances] virtualAppliancesFactory = new Function<Integer, Compute.Component.VirtualAppliances>() {
                    @Override
                    public VirtualAppliances apply(Integer integer) {
                        return new VirtualAppliances();
                    }
                };

                Function<Integer, Compute.Component.VpcNetworkPeeringConnections] vpcNetworkPeeringConnectionsFactory = new Function<Integer, Compute.Component.VpcNetworkPeeringConnections>() {
                    @Override
                    public VpcNetworkPeeringConnections apply(Integer integer) {
                        return new VpcNetworkPeeringConnections();
                    }
                };

                Function<Integer, Compute.Component.SecurityGroupRules] securityGroupRulesFactory = new Function<Integer, Compute.Component.SecurityGroupRules>() {
                    @Override
                    public SecurityGroupRules apply(Integer integer) {
                        return new SecurityGroupRules();
                    }
                };

                Function<Integer, Compute.Component.SecurityGroups] securityGroupsFactory = new Function<Integer, Compute.Component.SecurityGroups>() {
                    @Override
                    public SecurityGroups apply(Integer integer) {
                        return new SecurityGroups();
                    }
                };

                Function<Integer, Compute.Component.Subnets] subnetsFactory = new Function<Integer, Compute.Component.Subnets>() {
                    @Override
                    public Subnets apply(Integer integer) {
                        return new Subnets();
                    }
                };

                Function<Integer, Compute.Component.Tags] tagsFactory = new Function<Integer, Compute.Component.Tags>() {
                    @Override
                    public Tags apply(Integer integer) {
                        return new Tags();
                    }
                };

                Function<Integer, Compute.Component.UserMetadata] userMetadataFactory = new Function<Integer, Compute.Component.UserMetadata>() {
                    @Override
                    public UserMetadata apply(Integer integer) {
                        return new UserMetadata();
                    }
                };

                Function<Integer, Compute.Component.Users] usersFactory = new Function<Integer, Compute.Component.Users>() {
                    @Override
                    public Users apply(Integer integer) {
                        return new Users();
                    }
                };

                Function<Integer, Compute.Component.ZoneInstances] zoneInstancesFactory = new Function<Integer, Compute.Component.ZoneInstances>() {
                    @Override
                    public ZoneInstances apply(Integer integer) {
                        return new ZoneInstances();
                    }
                };

                Map<String, Function<? extends Component, ?>> componentFactoriesMap = ImmutableMap.<String, Function<? extends Component, ?>>builder()
                        .put("access_control_lists", accessControlListsFactory)
                        .put("addresses", addressesFactory)
                        .put("backup_types", backupTypesFactory)
                        .put("compute_instance_groups", computeInstanceGroupsFactory)
                        .put("computes", computersFactory)
                        .put("firewalls", firewallsFactory)
                        .put("image_build_tasks", imageBuildTasksFactory)
                        .put("images", imagesFactory)
                        .put("instance_templates", instanceTemplatesFactory)
                        .put("machine_types", machineTypesFactory)
                        .put("projects", projectsFactory)
                        .put("regions", regionsFactory)
                        .put("zones", zonesFactory)
                        .put("availability_domains", availabilityDomainsFactory)
                        .put("resource_quotas", resourceQuotasFactory)
                        .put("service_accounts", serviceAccountsFactory)
                        .put("service_account_keys", serviceAccountKeysFactory)
                        .put("target_https_proxies", targetHttpsProxiesFactory)
                        .put("target_http_proxy_groups", targetHttpProxyGroupsFactory)
                        .put("target_http_proxies", targetHttpProxiesFactory)
                        .put("virtual_appliances", virtualAppliancesFactory)
                        .put("vpc_network_peering_connections", vpcNetworkPeeringConnectionsFactory)
                        .put("security_group_rules", securityGroupRulesFactory)
                        .put("security_groups", securityGroupsFactory)
                        .put("subnets", subnetsFactory)
                        .put("tags", tagsFactory)
                        .put("user_metadata", userMetadataFactory)
                        .put("users", usersFactory)
                        .put("zone_instances", zoneInstancesFactory)
                        .build();

                for (Entry<String, Class> entry : COMPUTE_COMPONENTS.entrySet()) {
                    String name = entry.getKey();
                    Class clazz = entry.getValue();
                    if (!componentFactoriesMap.containsKey(name)) {
                        throw new IllegalArgumentException("Unknown component: " + name);
                    }
                    componentsMap.put(clazz, componentFactoriesMap.get(name));
                }
            }

            private static <C extends Component> C createComponentFromJson(Class<C> clazz, JsonObject jsonObj) throws IOException {
                Function<JsonObject, C> factory = componentsMap.get(clazz);
                return factory != null ? factory.apply(jsonObj) : null;
            }
        }
