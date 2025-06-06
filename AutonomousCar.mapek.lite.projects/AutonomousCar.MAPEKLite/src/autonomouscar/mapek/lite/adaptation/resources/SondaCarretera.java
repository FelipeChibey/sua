package autonomouscar.mapek.lite.adaptation.resources;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import sua.autonomouscar.devices.interfaces.IRoadSensor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;

public class SondaCarretera extends Probe implements ServiceListener {
	
	public static String ID = "Sonda Carretera";

	public SondaCarretera(BundleContext context) {
		super(context, ID);
		
		String filter = "(objectclass=" + IRoadSensor.class.getName() + ")";
		try {
			context.addServiceListener(this, filter);
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void reportarCarretera(String modo) {
		this.reportMeasure(modo.toUpperCase());
	}


	@Override
	public void serviceChanged(ServiceEvent event) {
		switch (event.getType()) {
			case ServiceEvent.REGISTERED:
			case ServiceEvent.MODIFIED:
				ServiceReference sr = event.getServiceReference();
				IRoadSensor roadSensor = (IRoadSensor) context.getService(sr);
				this.reportarCarretera(roadSensor.getRoadType().name());
		}
	}
}
