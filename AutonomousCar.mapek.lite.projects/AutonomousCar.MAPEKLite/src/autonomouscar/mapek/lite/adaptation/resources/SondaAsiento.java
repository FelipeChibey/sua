package autonomouscar.mapek.lite.adaptation.resources;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import sua.autonomouscar.devices.interfaces.IHumanSensors;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;

public class SondaAsiento extends Probe implements ServiceListener {
	public static String ID = "Sonda Asiento";

	public SondaAsiento(BundleContext context) {
		super(context, ID);
		
		String filter = "(objectclass=" + IHumanSensors.class.getName() + ")"; // cambiar por ihumansensor
		try {
			context.addServiceListener(this, filter);
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void reportarEstadoAsiento(Boolean modo) {
		this.reportMeasure(modo);
	}


	@Override
	public void serviceChanged(ServiceEvent event) {
		// TODO Auto-generated method stub
		
		switch (event.getType()) {
			case ServiceEvent.REGISTERED:
			case ServiceEvent.MODIFIED:
				ServiceReference sr = event.getServiceReference();
				IHumanSensors seatSensor = (IHumanSensors) context.getService(sr);
				this.reportarEstadoAsiento(seatSensor.isDriverSeatOccupied());
		}
	}
}

