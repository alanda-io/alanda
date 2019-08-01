package io.alanda.base.util;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A simple JavaBean to represent label-value pairs. This is most commonly used when constructing user interface
 * elements which have a label to be displayed to the user, and a corresponding value to be returned to the server. One
 * example is the <code>&lt;html:options&gt;</code> tag.
 * <p>
 * Note: this class has a natural ordering that is inconsistent with equals.
 * </p>
 *
 * @version $Rev: 471754 $ $Date: 2005-05-07 12:11:38 -0400 (Sat, 07 May 2005) $
 */
public class LabelValueBean implements Comparable<LabelValueBean>, Serializable {

  /**
   * Comparator that can be used for a case insensitive sort of <code>LabelValueBean</code> objects.
   */
  public static final Comparator<LabelValueBean> CASE_INSENSITIVE_ORDER = new Comparator<LabelValueBean>() {

    public int compare(LabelValueBean o1, LabelValueBean o2) {
      String label1 = o1.getLabel();
      String label2 = o2.getLabel();

      return label1.compareToIgnoreCase(label2);
    }
  };

  // ------------------------------------------------------------- Properties

  /**
   * The property which supplies the option label visible to the end user.
   */
  private String label = null;

  /**
   * The property which supplies the value returned to the server.
   */
  private String value = null;

  // ----------------------------------------------------------- Constructors

  /**
   * Default constructor.
   */
  public LabelValueBean() {
    super();
  }

  /**
   * Construct an instance with the supplied property values.
   *
   * @param label The label to be displayed to the user.
   * @param value The value to be returned to the server.
   */
  public LabelValueBean(String label, String value) {
    this.label = label;
    this.value = value;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  // --------------------------------------------------------- Public Methods

  /**
   * Compare LabelValueBeans based on the label, because that's the human viewable part of the object.
   *
   * @see Comparable
   */
  public int compareTo(LabelValueBean o) {
    // Implicitly tests for the correct type, throwing
    // ClassCastException as required by interface
    String otherLabel = o.getLabel();

    return this.getLabel().compareTo(otherLabel);
  }

  /**
   * Return a string representation of this object.
   */
  public String toString() {
    StringBuffer sb = new StringBuffer("LabelValueBean[");

    sb.append(this.label);
    sb.append(", ");
    sb.append(this.value);
    sb.append("]");

    return (sb.toString());
  }

  /**
   * LabelValueBeans are equal if their values are both null or equal.
   *
   * @see Object#equals(Object)
   */
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }

    if ( !(obj instanceof LabelValueBean)) {
      return false;
    }

    LabelValueBean bean = (LabelValueBean) obj;
    int nil = (this.getValue() == null) ? 1 : 0;

    nil += ((bean.getValue() == null) ? 1 : 0);

    if (nil == 2) {
      return true;
    } else if (nil == 1) {
      return false;
    } else {
      return this.getValue().equals(bean.getValue());
    }
  }

  /**
   * The hash code is based on the object's value.
   *
   * @see Object#hashCode()
   */
  public int hashCode() {
    return (this.getValue() == null) ? 17 : this.getValue().hashCode();
  }
}
