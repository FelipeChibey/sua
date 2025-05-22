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
import sua.autonomouscar.infraestructure.driving.ARC.FallbackPlanARC;
import sua.autonomouscar.infraestructure.driving.ARC.L3_DrivingServiceARC;
import sua.autonomouscar.infraestructure.interaction.ARC.NotificationServiceARC;
import sua.autonomouscar.infraestructure.devices.ARC.DistanceSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.EngineARC;
import sua.autonomouscar.infraestructure.devices.ARC.HumanSensorsARC;
import sua.autonomouscar.infraestructure.devices.ARC.LineSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.RoadSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.SteeringARC;

public class AutoCuracionAdaptationRule extends AdaptationRule {
	
	protected static SmartLogger logger = SmartLogger.getLogger(AutoCuracionAdaptationRule.class);
	public static String ID = "Regla Autocuracion";
	
	IKnowledgeProperty kp_estado_sensor_carril = null;
	
	public AutoCuracionAdaptationRule(BundleContext context) {
		super(context, ID);
		this.setListenToKnowledgePropertyChanges("EstadoSensorCarril");

		kp_estado_sensor_carril = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("EstadoSensorCarril");
	}

	@Override
	public boolean checkAffectedByChange(IKnowledgeProperty property) {
		
		if (kp_estado_sensor_carril == null) {
			logger.trace("Required Knowledge property not set. Not executing the rule ...");
			return false;
		}
		return true;
	}
	
	@Override
	public IRuleSystemConfiguration onExecute(IKnowledgeProperty property) throws RuleException {
		Boolean estadoSensorCarril = null;
		
		if ( kp_estado_sensor_carril.getValue() != null ) {
			estadoSensorCarril = (Boolean) kp_estado_sensor_carril.getValue();
		} else {
			logger.trace("FranjaDia NULL! Not executing the rule ...");
			throw new RuleException("FranjaDia null value!", "Not executing the rule ...");		
		}
		
		if (estadoSensorCarril == false) {
			configuracionSistemaActivarNivel0();
		} else if (estadoSensorCarril == true) {
			configuracionSistemaDesctivarNivel0();
		} else {
			logger.trace("Cannot understand knowledge property value. Not executing the rule ...");
			throw new RuleException("Unknown property value",
					"Cannot understand knowledge property value. Not executing the rule ...");
		}
		return null;
	}	
	
	protected IRuleComponentsSystemConfiguration configuracionSistemaActivarNivel0() {
		// Activamos el controlador de trafico
		IRuleComponentsSystemConfiguration theNextSystemConfiguration = SystemConfigurationHelper.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());
		
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "driving.L0.ManualDriving", "1.0.0");
		
		return theNextSystemConfiguration;		
		
	}
	
	protected IRuleComponentsSystemConfiguration configuracionSistemaDesctivarNivel0() {
		// Activamos el controlador de trafico
		IRuleComponentsSystemConfiguration theNextSystemConfiguration = SystemConfigurationHelper.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		
		SystemConfigurationHelper.componentToRemove(theNextSystemConfiguration, "driving.L0.ManualDriving", "1.0.0");
		
		return theNextSystemConfiguration;		
		
	}
	
}
