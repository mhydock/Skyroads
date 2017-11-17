package skyroads;

public enum PanelType
{
	HAZARD(new float[]{1.0f,0.45f,0.45f,1.0f}),		// Salmon
	SUPPLY(new float[]{0.3f,0.65f,1.0f,1.0f}),		// Sky blue
	FASTER(new float[]{0.25f,1.0f,0.25f,1.0f}),		// Lime green
	SLOWER(new float[]{0.0f,0.5f,0.0f,1.0f}),		// Forest green
	SLIPPY(new float[]{0.10f,0.10f,0.10f,1.0f}),	// Dark grey
	NORMAL(null);
	
	private final float[] color;
	PanelType(float[] color)
	{
		this.color = color;
	}
	
	public float[] getValue()
	{
		return color;
	}
}
