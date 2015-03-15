
public class Complex {
	
	/*
	 * A Complex Number a+ib or rcis(theta)
	 */

	protected double real;
	protected double imaginary;

	protected double modulus;
	protected double argument; // in radians

	public Complex(double real, double imaginary) {
		this.real = real;
		this.imaginary = imaginary;
		
		this.modulus = Math.sqrt( Math.pow(real, 2) + Math.pow(imaginary, 2) ); // calculate modulus
		this.argument = Math.atan( imaginary / real ); // calculate argument (theta) => in radians
	}
	
	/*
	 * @isDegrees should be either true or false depending on the angle provided
	 * the argument is stored in radians and conversion is made when the constructor is called.
	 * Also this constructor allows the use of Polar or Cartesian as a base for the Complex number
	 */
	public Complex(double modulus, double argument, boolean isDegrees) {
		
		// convert to radians beforehand 
		if (isDegrees)
			argument = Math.toRadians(argument);
		
		this.modulus = modulus;
		this.argument = argument;
			
		// Calculate order of real and imaginary
		this.real = argument * Math.cos(argument);
		this.imaginary = argument * Math.sin(argument);
	}
	
	public Complex square() {
		// return the square of the complex number
		// real part becomes: a^2 - b^2
		// imaginary part becomes: 2ab
		double real = Math.pow(this.real, 2) - Math.pow(this.imaginary, 2);
		double imaginary = 2.0 * this.real * this.imaginary;
		
		return new Complex( real, imaginary );
	}
	
	public double modulusSquared() {
		return Math.pow(modulus, 2);
	}
	
	public Complex add(Complex other) {
		return new Complex( this.real + other.getReal(), this.imaginary + other.getImaginary() );
	}
	
	public boolean equals(Complex other) {
		if (real == other.getRe() && imaginary == other.getIm())
			return true;
		return false;
	}

	public double getReal() {
		return real;
	}

	public double getImaginary() {
		return imaginary;
	}
	
	public double getRe() {
		return real;
	}

	public double getIm() {
		return imaginary;
	}

	public double getModulus() {
		return modulus;
	}

	public double getArgument() {
		return argument;
	}
	
	public String printCartesian() {
		return new String(this.real + " + (" + this.imaginary + ")i");
	}
	
	public String printPolar() {
		return String.format("%.02f cis(%.01f)", this.modulus, Math.toDegrees(this.argument));
	}

}
