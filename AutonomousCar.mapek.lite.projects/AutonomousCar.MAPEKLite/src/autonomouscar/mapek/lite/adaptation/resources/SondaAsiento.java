package autonomouscar.mapek.lite.adaptation.resources;

import org.osgi.framework.BundleContext;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;

public class SondaAsiento extends Probe {
	
	public static String ID = "Sonda Asiento";

	public SondaAsiento(BundleContext context) {
		super(context, ID);
	}
	
	
	public void reportarInteraccion(String modo) {
		this.reportMeasure(modo.toUpperCase());
	}

}
