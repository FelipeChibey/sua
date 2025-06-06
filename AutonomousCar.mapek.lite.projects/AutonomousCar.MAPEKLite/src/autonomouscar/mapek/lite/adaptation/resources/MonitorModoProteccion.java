package autonomouscar.mapek.lite.adaptation.resources;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;

public class MonitorModoProteccion extends Monitor {
	
	public static String ID = "Monitor Modo Proteccion";

	public MonitorModoProteccion(BundleContext context) {
		super(context, ID);
	}

	@Override
	public IMonitor report(Object measure) {
		this.logger.debug(String.format("Received measure: %s", measure.toString()));
		try {
			Boolean value = (Boolean) measure;
			IKnowledgeProperty kp_modo_conduccion = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("ModoConduccion");
			IKnowledgeProperty kp_modo_proteccion = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("EstadoSensorCarril");
			
			if(value != null && value != kp_modo_proteccion.getValue()) {
				kp_modo_proteccion.setValue(value);
				if (value == true) {
					kp_modo_conduccion.setValue("3");
				} else {
					kp_modo_conduccion.setValue("0");
				}		
			}
			
		} catch (Exception e) {
			return this;
		}	
		return this;
	}
}
