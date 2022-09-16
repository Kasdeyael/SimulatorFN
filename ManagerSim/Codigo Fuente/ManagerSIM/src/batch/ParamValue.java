package batch;

import java.math.BigDecimal;

import exception.IncompatibleParameterException;

public class ParamValue {

	private BigDecimal initValue;
	private BigDecimal endValue;
	private String name;
	private BigDecimal step;

	/**
	 * Constructor for ParamValue.
	 * @param name name of the parameter
	 */
	public ParamValue(String name) {
		this.name = name;
	}

	/**
	 * Constructor for ParamValue.
	 * @param name name of the parameter
	 * @param init initial value
	 * @param end end value
	 * @param step step to add in each execution
	 */
	public ParamValue(String name, BigDecimal init, BigDecimal end, BigDecimal step) {
		this.name = name;
		this.initValue = init;
		this.endValue = end;
		this.step = step;
	}


	/**
	 * Sets the initial value of the range
	 * @param initValue
	 */
	public void setInit(BigDecimal initValue) {
		this.initValue = initValue;
	}

	/**
	 * Sets the ending value of the range
	 * @param endValue
	 */
	public void setEnd(BigDecimal endValue) {
		this.endValue = endValue;
	}

	/**
	 * Sets the step at which we change the current value.
	 * @param step
	 */
	public void setStep(BigDecimal step) {
		this.step = step;
	}

	/**
	 * Gets the initial value of the range
	 * @return initValue
	 */
	public BigDecimal getInit() {
		return initValue;
	}

	/**
	 * Gets the ending value of the range
	 * @return endValue
	 */
	public BigDecimal getEnd() {
		return endValue;
	}

	/**
	 * Gets the step of the parameter
	 * @return step
	 */
	public BigDecimal getStep() {
		return step;
	}

	/**
	 * Returns the name of the parameter
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return total of values this can execute
	 * @return
	 */
	public BigDecimal getValues() {
		return endValue.subtract(initValue).divide(step);
	}

	public void checkValid() throws IncompatibleParameterException{
		// TODO Auto-generated method stub
		if(initValue == null || endValue == null || step == null) throw new IncompatibleParameterException("A value for "+name+" is invalid");
	}
}
