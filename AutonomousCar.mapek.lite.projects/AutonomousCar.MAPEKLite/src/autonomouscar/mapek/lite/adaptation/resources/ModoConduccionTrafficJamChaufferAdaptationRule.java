package autonomouscar.mapek.lite.adaptation.resources;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.structures.systemconfiguration.interfaces.IRuleComponentsSystemConfiguration;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.AdaptationRule;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.exceptions.analyzing.RuleException;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.SystemConfigurationHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.structures.systemconfiguration.interfaces.IRuleSystemConfiguration;
import es.upv.pros.tatami.osgi.utils.interfaces.ITimeStamped;
import es.upv.pros.tatami.osgi.utils.logger.SmartLogger;
import sua.autonomouscar.infraestructure.devices.ARC.DistanceSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.EngineARC;
import sua.autonomouscar.infraestructure.devices.ARC.HumanSensorsARC;
import sua.autonomouscar.infraestructure.devices.ARC.LineSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.RoadSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.SteeringARC;
import sua.autonomouscar.infraestructure.driving.ARC.FallbackPlanARC;
import sua.autonomouscar.infraestructure.driving.ARC.L3_DrivingServiceARC;
import sua.autonomouscar.infraestructure.interaction.ARC.NotificationServiceARC;

public class ModoConduccionTrafficJamChaufferAdaptationRule extends AdaptationRule {
	
	protected static SmartLogger logger = SmartLogger.getLogger(ModoConduccionTrafficJamChaufferAdaptationRule.class);
	public static String ID = "Regla Modo Conduccion TrafficJamChauffer";
	
	IKnowledgeProperty kp_modo_conduccion = null;
	IKnowledgeProperty kp_modo_conduccion_nivel_autonomo = null;
	IKnowledgeProperty kp_tipo_carretera = null;
	
	public ModoConduccionTrafficJamChaufferAdaptationRule(BundleContext context) {
		super(context, ID);
		this.setListenToKnowledgePropertyChanges("TipoCarretera");
		this.setListenToKnowledgePropertyChanges("ModoConduccionNivelAutonomo");
		this.setListenToKnowledgePropertyChanges("ModoConduccion");

		kp_tipo_carretera = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("TipoCarretera");
		kp_modo_conduccion_nivel_autonomo = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("ModoConduccionNivelAutonomo");
		kp_modo_conduccion = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("ModoConduccion");
	}

	@Override
	public boolean checkAffectedByChange(IKnowledgeProperty property) {
		
		if (kp_modo_conduccion == null || kp_modo_conduccion_nivel_autonomo == null) {
			logger.trace("Required Knowledge property not set. Not executing the rule ...");
			return false;
		}
		return true;
	}
	
	@Override
	public IRuleSystemConfiguration onExecute(IKnowledgeProperty property) throws RuleException {
		
		String modoConduccion = null;
		String modoEstadoCarretera = null;
		String modoTipoCarretera = null;
		
		if ( kp_modo_conduccion.getValue() != null ) {
			
			modoConduccion = (String) kp_modo_conduccion.getValue();
		} else {
			logger.trace("FranjaDia NULL! Not executing the rule ...");
			throw new RuleException("FranjaDia null value!", "Not executing the rule ...");		
		}
		
		if ( kp_tipo_carretera.getValue() != null ) {
			
			modoTipoCarretera = (String) kp_tipo_carretera.getValue();
		} else {
			logger.trace("asdfasdf NULL! Not executing the rule ...");
			throw new RuleException("FranjaDia null value!", "Not executing the rule ...");		
		}

		if (modoConduccion.equals("3") && 	
				modoTipoCarretera.equals("HIGHWAY") && 
				(modoEstadoCarretera.equals("JAM") || modoEstadoCarretera.equals("COLLAPSED"))) {
			return this.configuracionSistemaActivarControlTrafficJamChauffer();
		} else if (modoTipoCarretera.equals("CITY") || modoEstadoCarretera.equals("FLUID")) {
 			return this.configuracionSistemaDesactivarControlTrafficJamChauffer();	
		} else {
			logger.trace("Cannot understand knowledge property value. Not executing the rule ...");
			throw new RuleException("Unknown property value",
					"Cannot understand knowledge property value. Not executing the rule ...");
		}
	}
	
	protected IRuleComponentsSystemConfiguration configuracionSistemaActivarControlTrafficJamChauffer() {
		// Activamos el controlador de trafico
		IRuleComponentsSystemConfiguration theNextSystemConfiguration = SystemConfigurationHelper.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		// Agregamos el controlador de trafico
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "driving.L3.TrafficJamChauffer", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "driving.FallbackPlan.Emergency", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "device.HumanSensors", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "device.FrontDistanceSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "device.RearDistanceSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "device.LeftDistanceSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "device.RightDistanceSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "device.LeftLineSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "device.RightLineSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "device.Steering", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "device.Engine", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "device.RoadSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "device.LIDAR", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "interaction.NotificationService", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "interaction.Seat.Driver", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "interaction.Seat.Copilot", "1.0.0");
		
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_NOTIFICATIONSERVICE,
				"interaction.NotificationService", "1.0.0", NotificationServiceARC.PROVIDED_SERVICE);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_ENGINE,
				"device.Engine", "1.0.0", EngineARC.PROVIDED_DEVICE);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_HUMANSENSORS,
				"device.HumanSensors", "1.0.0", HumanSensorsARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_ROADSENSOR,
				"device.RoadSensors", "1.0.0", RoadSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_STEERING,
				"device.Steering", "1.0.0", SteeringARC.PROVIDED_DEVICE);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_LEFTLINESENSOR,
				"device.LeftLineSensor", "1.0.0", LineSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_RIGHTLINESENSOR,
				"device.RightLineSensor", "1.0.0", LineSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.FallbackPlan.Emergency", "1.0.0", FallbackPlanARC.REQUIRED_ENGINE,
				"device.Engine", "1.0.0", EngineARC.PROVIDED_DEVICE);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.FallbackPlan.Emergency", "1.0.0", FallbackPlanARC.REQUIRED_STEERING,
				"device.Steering", "1.0.0", SteeringARC.PROVIDED_DEVICE);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_FALLBACKPLAN,
				"driving.FallbackPlan.Emergency", "1.0.0", FallbackPlanARC.PROVIDED_DRIVINGSERVICE);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_RIGHTDISTANCESENSOR,
				"device.RightDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_LEFTDISTANCESENSOR,
				"device.LeftDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_REARDISTANCESENSOR,
				"device.RearDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_FRONTDISTANCESENSOR,
				"device.FrontDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);

		return theNextSystemConfiguration;		
		
	}
	
	protected IRuleComponentsSystemConfiguration configuracionSistemaDesactivarControlTrafficJamChauffer() {
		// Activamos el controlador de trafico
		IRuleComponentsSystemConfiguration theNextSystemConfiguration = SystemConfigurationHelper.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		// Agregamos el controlador de trafico
		SystemConfigurationHelper.componentToRemove(theNextSystemConfiguration, "driving.L3.TrafficJamChauffer", "1.0.0");

		return theNextSystemConfiguration;		
		
	}
	
}
