package autonomouscar.mapek.lite.adaptation.resources;

import org.osgi.framework.BundleContext;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;

public class SondaModoConduccion extends Probe {
	
	public static String ID = "Sonda Modo Conduccion";

	public SondaModoConduccion(BundleContext context) {
		super(context, ID);
	}
	
	
	public void reportarModoConduccion(String modo) {
		this.reportMeasure(modo.toUpperCase());
	}

}
