package sua.autonomouscar.devices.interfaces;

public interface ISeatSensor extends IThing {
	
	public boolean isSeatOccuppied();
	
	public ISeatSensor setSeatOccupied(boolean value); // for simulation purposes only

}
