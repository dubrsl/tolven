package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;


/**
 * Any lesion that can be accurately measured in at least one dimension (the
 * longest diameter to be recorded) with conventional techniques.
 * @version 1.0
 * @created 27-Sep-2006 9:56:16 AM
 */
@Entity
@DiscriminatorValue("MEAS")
public class Measurable extends LesionEvaluation implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * Result or product of multiplying dimensions (2 or 3) of a site or specimen
	 * expressed in centimeters.
	 */
	@Column private int dimensionProduct;
	/**
	 * Measurement of a lesion or location in the 'X' dimension in centimeters
	 * (longest measurement).
	 */
	@Column private int xDimension;
	/**
	 * The measurements of a lesion in Y (2nd or width) dimension in centimeters.
	 * 
	 */
	@Column private int yDimension;
	/**
	 * The measurements of the lesions in Z (3rd) dimension in centimeters.
	 * 
	 */
	@Column private int zDimension;

	public Measurable(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public int getDimensionProduct() {
		return dimensionProduct;
	}

	public void setDimensionProduct(int dimensionProduct) {
		this.dimensionProduct = dimensionProduct;
	}

	public int getXDimension() {
		return xDimension;
	}

	public void setXDimension(int dimension) {
		xDimension = dimension;
	}

	public int getYDimension() {
		return yDimension;
	}

	public void setYDimension(int dimension) {
		yDimension = dimension;
	}

	public int getZDimension() {
		return zDimension;
	}

	public void setZDimension(int dimension) {
		zDimension = dimension;
	}

}