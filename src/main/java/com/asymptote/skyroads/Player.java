package skyroads;

public class Player
{
	public enum State
	{
		ALIVE, EXPLODING, SUFFOCATING, EXHAUSTED, DEAD;
	}
	
	public static final int INIT_AIR = 100;
	public static final int INIT_FUEL = 100;
	
	private float remainingAir;
	private float remainingFuel;
	
	private State pState;
	private Ship ship;
	private Level level;
	
	public Player(Ship ship, Level level)
	{
		this.ship = ship;
		this.level = level;
		
		reset();
	}
	
	public float getShipSpeed()
	{
		return ship.getSpeed();
	}
	
	public float getShipMaxSpeed()
	{
		return ship.getMaxSpeed();
	}
	
	public float getAirPercent()
	{
		return remainingAir/INIT_AIR;
	}
	
	public float getFuelPercent()
	{
		return remainingFuel/INIT_FUEL;
	}
	
	public float getProgress()
	{
		float[] finish = level.getFinish();
		float length = level.getLength();
		
		return 1f - (finish[Level.DISTANCE]-ship.getDistance())/length;
	}
	
	public boolean canJump()
	{
		return pState == State.ALIVE && ship.isTouching();
	}
	
	public void massiveCollision()
	{
		System.out.println("BOOOM!!");
		setState(State.EXPLODING);
	}
	
	public void loseAir(float air)
	{
		remainingAir -= air;
		
		if (remainingAir < 0)
		{
			remainingAir = 0;
			setState(State.SUFFOCATING);			
		}
		
		if (remainingAir > INIT_AIR)
			remainingAir = INIT_AIR;
	}
	
	public void loseFuel(float fuel)
	{
		remainingFuel -= fuel;
		
		if (remainingFuel < 0)
		{
			remainingFuel = 0;
			setState(State.EXHAUSTED);
		}
		
		if (remainingFuel > INIT_FUEL)
			remainingFuel = INIT_FUEL;
	}
	
	public void reset()
	{
		setState(State.ALIVE);
		
		remainingAir = INIT_AIR;
		remainingFuel = INIT_FUEL;
	}
	
	public State getState()
	{
		return pState;
	}
	
	private void setState(State state)
	{
		if (pState == state)
			return;
		
		if (state == State.DEAD)
		{
			pState = State.DEAD;
			return;
		}
		
		if (state == State.EXPLODING)
		{
			pState = State.EXPLODING;
			return;
		}
		
		if (state == State.EXHAUSTED)
		{
			pState = State.EXHAUSTED;
			return;
		}
		
		// Veer in a random direction.
		if (state == State.SUFFOCATING)
		{
			pState = State.SUFFOCATING;
			
			int dir = Math.round((float)Math.random());
			
			ship.moveLeft(false);
			ship.moveRight(false);
			
			if (dir == 0)
				ship.moveLeft(true);
			
			if (dir == 1)
				ship.moveRight(true);
			
			return;
		}
		
		if (state == State.ALIVE)
		{
			pState = State.ALIVE;
			return;
		}
	}
}
