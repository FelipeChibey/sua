package autonomouscar.mapek.lite.adaptation.resources;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;

public class MonitorEstadoCarretera extends Monitor {
	
	public static String ID = "Monitor Estado Carretera";

	public MonitorEstadoCarretera(BundleContext context) {
		super(context, ID);
	}

	@Override
	public IMonitor report(Object measure) {
		this.logger.debug(String.format("Received measure: %s", measure.toString()));
		try {
			String value = (String) measure;

			IKnowledgeProperty kp_estado_carretera = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("EstadoCarretera");
			
			if (value != null && value != kp_estado_carretera.getValue()) kp_estado_carretera.setValue(value);
			
		} catch (Exception e) {
			return this;
		}		
		return this;
	}
}
